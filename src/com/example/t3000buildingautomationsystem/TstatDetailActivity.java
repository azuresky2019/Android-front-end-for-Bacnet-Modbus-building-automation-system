package com.example.t3000buildingautomationsystem;

import com.example.t3000buildingautomationsystem.InternetSocket;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class TstatDetailActivity extends Activity{
	
	public String mIP = "192.168.0.212";
	public int mPort = 10000;
	private boolean mTcpConnected = false;
	private int ModbusTcpSequenceId = 0;
	InternetSocket mInternetSocket = new InternetSocket();
	
	private ImageView leftKey;
	private ImageView downKey;
	private ImageView upKey;
	private ImageView rightKey;
	private TextView valueArea;
	private TextView titleArea;
	
	private final static int KEY_HOLD_TIMEOUT = 600;
	private final static int SCREEN_HOLD_TIMEOUT = 5000;
	
	private enum displayModeEnum{isTemp,isSetpoint,isSystem};
	displayModeEnum displayMode = displayModeEnum.isTemp;
	
	private static Handler keyHandler = new Handler();
	private static Handler loopHandler = new Handler();
	
	private float currentTemperature;
	private float currentSetpoint;

	
	private final static int GET_TCP_MSG_LOOP_TIMEOUT = 3000;
	
	private final static byte MODBUS_READ_COMMAND = 3;
	private final static byte MODBUS_SINGLE_WRITE_COMMAND = 6;
	private final static int MODBUS_TEMPERATURE_REGISTER = 0x79;
	private final static int MODBUS_SETPOINT_REGISTER = 0x159;
	private final static int MODBUS_TCP_READ_CMD_LENGTH = 6;
	private final static int MODBUS_TCP_SINGLE_WRITE_CMD_LENGTH = 6;
	
	//测试用modbus id 1
	private final static int MODBUS_TEST_ID = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止屏幕锁屏
		setContentView(R.layout.tstat_detail);
		
		leftKey = (ImageView)findViewById(R.id.left_button);
		downKey = (ImageView)findViewById(R.id.down_button);
		upKey = (ImageView)findViewById(R.id.up_button);
		rightKey = (ImageView)findViewById(R.id.right_button);
		
		leftKey.setOnClickListener(mLeftKeyListener);
		downKey.setOnClickListener(mDownKeyListener);
		upKey.setOnClickListener(mUpKeyListener);
		rightKey.setOnClickListener(mRightKeyListener);
		

		titleArea = (TextView)findViewById(R.id.title_area);
		valueArea = (TextView)findViewById(R.id.value_area);
		valueArea.setText("0.0 ℃");

		
		loopHandler.post(GetTcpMsgRunnable);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private byte[] tempBuf = new byte[32];
	private Runnable GetTemperatureRunnable = new Runnable(){

		@Override
		public void run() {
			
			if(mTcpConnected == true){
				byte[] getTemp = new byte[12];
				int temperatureSeqId = ModbusTcpSequenceId;
				createModbusReadCmd(MODBUS_TEST_ID,MODBUS_TEMPERATURE_REGISTER,1,getTemp);
				System.out.println("Connect successed");
				mInternetSocket.WriteInternetByte(getTemp);
				mInternetSocket.ReadInternet(tempBuf);
				/*if( temperatureSeqId == (int)(((int)tempBuf[0]&0xff)*256+((int)tempBuf[1]&0xff))){
					System.out.println("Sequence number is right, send number is"+Integer.toString(temperatureSeqId)
							+"   resc num is"+ Integer.toString((int)(((int)tempBuf[0]&0xff)*256+((int)tempBuf[1]&0xff))));
				}else{
					System.out.println("Sequence number is Wrong!!!!!!!!!!!!!!send number is"+Integer.toString(temperatureSeqId)
							+"   resc num is"+ Integer.toString((int)(((int)tempBuf[0]&0xff)*256+((int)tempBuf[1]&0xff))));
				}*/  // 测试消息的序列号是否正确
				System.out.println("Command id: "+Byte.toString(tempBuf[7])+
						"tempBuf[9]= "+Byte.toString(tempBuf[9])+",tempBuf[10]= "+Byte.toString(tempBuf[10]));
				if( MODBUS_READ_COMMAND == tempBuf[7])
					currentTemperature = (float)((int)tempBuf[9]*256+
							((int)tempBuf[10]&0xff))/10.0f; //此处byte想转为无符号int，需要与0xff
				
			}else{
				mTcpConnected = mInternetSocket.Client(mIP,mPort);
				System.out.println("Failed to connect");
			}
			
		}
		
	};
	
	private Runnable GetSetpointRunnable = new Runnable(){

		@Override
		public void run() {
			byte[] getTemp = new byte[12];
			if(mTcpConnected == true){
				createModbusReadCmd(MODBUS_TEST_ID,MODBUS_SETPOINT_REGISTER,1,getTemp);
				mInternetSocket.WriteInternetByte(getTemp);
				mInternetSocket.ReadInternet(tempBuf);
				if( MODBUS_READ_COMMAND == tempBuf[7])
					currentSetpoint = (float)((int)tempBuf[9]*256+
							((int)tempBuf[10]&0xff))/10.0f; //此处byte想转为无符号int，需要与0xff
				//System.out.println(Float.toString(currentTemperature));
			}else{
				mTcpConnected = mInternetSocket.Client(mIP,mPort);
			}
		}
		 
	};
	
	private Runnable GetTcpMsgRunnable = new Runnable(){
		
		//boolean success = false;
		@Override
		public void run(){
			
			if(displayMode == displayModeEnum.isTemp){
				try{
					new Thread(GetTemperatureRunnable).start();
				}catch (Exception e) {
					e.printStackTrace();
				}
				if( mTcpConnected == true){
					/*success = startPing(mIP);
					if(success == false)
					{
						System.out.println("ping failed");
						mTcpConnected = false;
						ClientClose();
					}*/
				}
				valueArea.setText(Float.toString(currentTemperature)+"℃");
				
			}else if(displayMode == displayModeEnum.isSetpoint){
				try{
					new Thread(GetSetpointRunnable).start();
				}catch (Exception e) {
					e.printStackTrace();
				}
				valueArea.setText(Float.toString(currentSetpoint)+"℃");
			}
			loopHandler.postDelayed(GetTcpMsgRunnable, GET_TCP_MSG_LOOP_TIMEOUT);
		}
	};
	
	private Runnable backToTemperatureScreenRunnable = new Runnable(){
		@Override
		public void run() {
			displayMode = displayModeEnum.isTemp;
			titleArea.setText(R.string.temperature);
			valueArea.setText(Float.toString(currentTemperature));
		}
		
	};
	
	private View.OnClickListener mLeftKeyListener = new View.OnClickListener() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			leftKey.setAlpha(100);
			if((displayMode == displayModeEnum.isTemp)||(displayMode == displayModeEnum.isSetpoint)){
				titleArea.setText(R.string.system);
				/*try{
					new Thread(GetSetpointRunnable).start();
				}catch (Exception e) {
					e.printStackTrace();
				}*/
				//valueArea.setText(Float.toString(currentSetpoint)+"℃");
				displayMode = displayModeEnum.isSystem;
				keyHandler.postDelayed(backToTemperatureScreenRunnable, SCREEN_HOLD_TIMEOUT);
			}else if(displayMode == displayModeEnum.isSystem){
				//TODO 发送修改system消息
				keyHandler.removeCallbacks(backToTemperatureScreenRunnable);
				//currentSetpoint = currentSetpoint+1.0f;
				
				//byte[] writeBuf = new byte[12];
				//createModbusSingleWriteCmd(MODBUS_TEST_ID,MODBUS_SETPOINT_REGISTER,(int)(currentSetpoint*10),writeBuf);
				//WriteInternetByte(writeBuf);
				
				//valueArea.setText(Float.toString(currentSetpoint)+"℃");
				keyHandler.postDelayed(backToTemperatureScreenRunnable, SCREEN_HOLD_TIMEOUT);
			}else {
				// do nothing here
			}
			new Handler().postDelayed(new Runnable(){
				public void run(){
					leftKey.setAlpha(255);
				}
			}, KEY_HOLD_TIMEOUT);
		}
	};
	
	
	private View.OnClickListener mDownKeyListener = new View.OnClickListener() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0) {
			downKey.setAlpha(100);
			//loopHandler.removeCallbacks(GetTemperatureRunnable);
			if((displayMode == displayModeEnum.isTemp)||(displayMode == displayModeEnum.isSystem)){
				titleArea.setText(R.string.setpoint);
				try{
					new Thread(GetSetpointRunnable).start();
				}catch (Exception e) {
					e.printStackTrace();
				}
				valueArea.setText(Float.toString(currentSetpoint)+"℃");
				displayMode = displayModeEnum.isSetpoint;
				keyHandler.postDelayed(backToTemperatureScreenRunnable, SCREEN_HOLD_TIMEOUT);
			}else if(displayMode == displayModeEnum.isSetpoint){
				//TODO 发送修改setpoint消息
				keyHandler.removeCallbacks(backToTemperatureScreenRunnable);
				currentSetpoint = currentSetpoint-1.0f;
				
				byte[] writeBuf = new byte[12];
				createModbusSingleWriteCmd(MODBUS_TEST_ID,MODBUS_SETPOINT_REGISTER,(int)(currentSetpoint*10),writeBuf);
				mInternetSocket.WriteInternetByte(writeBuf);
				
				valueArea.setText(Float.toString(currentSetpoint)+"℃");
				keyHandler.postDelayed(backToTemperatureScreenRunnable, SCREEN_HOLD_TIMEOUT);
			}else {
				// do nothing here
			}
			//loopHandler.postDelayed(GetTemperatureRunnable, GET_TCP_MSG_LOOP_TIMEOUT);
			new Handler().postDelayed(new Runnable(){
				public void run(){
					downKey.setAlpha(255);
				}
			}, KEY_HOLD_TIMEOUT);
		}
	};
	
	private View.OnClickListener mUpKeyListener = new View.OnClickListener() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			upKey.setAlpha(100);
			//loopHandler.removeCallbacks(GetTemperatureRunnable);
			if((displayMode == displayModeEnum.isTemp)||(displayMode == displayModeEnum.isSystem)){
				titleArea.setText(R.string.setpoint);
				try{
					new Thread(GetSetpointRunnable).start();
				}catch (Exception e) {
					e.printStackTrace();
				}
				valueArea.setText(Float.toString(currentSetpoint)+"℃");
				displayMode = displayModeEnum.isSetpoint;
				keyHandler.postDelayed(backToTemperatureScreenRunnable, SCREEN_HOLD_TIMEOUT);
			}else if(displayMode == displayModeEnum.isSetpoint){
				//TODO 发送修改setpoint消息
				keyHandler.removeCallbacks(backToTemperatureScreenRunnable);
				currentSetpoint = currentSetpoint+1.0f;
				
				byte[] writeBuf = new byte[12];
				createModbusSingleWriteCmd(MODBUS_TEST_ID,MODBUS_SETPOINT_REGISTER,(int)(currentSetpoint*10),writeBuf);
				mInternetSocket.WriteInternetByte(writeBuf);
				
				valueArea.setText(Float.toString(currentSetpoint)+"℃");
				keyHandler.postDelayed(backToTemperatureScreenRunnable, SCREEN_HOLD_TIMEOUT);
			}else {
				// do nothing here
			}
			//loopHandler.postDelayed(GetTemperatureRunnable, GET_TCP_MSG_LOOP_TIMEOUT);
			new Handler().postDelayed(new Runnable(){
				public void run(){
					upKey.setAlpha(255);
				}
			}, KEY_HOLD_TIMEOUT);
		}
	};
	
	private View.OnClickListener mRightKeyListener = new View.OnClickListener() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			rightKey.setAlpha(100);
			if((displayMode == displayModeEnum.isTemp)||(displayMode == displayModeEnum.isSetpoint)){
				titleArea.setText(R.string.system);
				/*try{
					new Thread(GetSetpointRunnable).start();
				}catch (Exception e) {
					e.printStackTrace();
				}*/
				//valueArea.setText(Float.toString(currentSetpoint)+"℃");
				displayMode = displayModeEnum.isSystem;
				keyHandler.postDelayed(backToTemperatureScreenRunnable, SCREEN_HOLD_TIMEOUT);
			}else if(displayMode == displayModeEnum.isSystem){
				//TODO 发送修改system消息
				keyHandler.removeCallbacks(backToTemperatureScreenRunnable);
				//currentSetpoint = currentSetpoint+1.0f;
				
				//byte[] writeBuf = new byte[12];
				//createModbusSingleWriteCmd(MODBUS_TEST_ID,MODBUS_SETPOINT_REGISTER,(int)(currentSetpoint*10),writeBuf);
				//WriteInternetByte(writeBuf);
				
				//valueArea.setText(Float.toString(currentSetpoint)+"℃");
				keyHandler.postDelayed(backToTemperatureScreenRunnable, SCREEN_HOLD_TIMEOUT);
			}else {
				// do nothing here
			}
			new Handler().postDelayed(new Runnable(){
				public void run(){
					rightKey.setAlpha(255);
				}
			}, KEY_HOLD_TIMEOUT);
		}
	};
	
	private void createModbusReadCmd(int modbusId, int firstAddr, int length, byte[] sendBuf){
		sendBuf[0] = (byte)(ModbusTcpSequenceId>>8);
		sendBuf[1] = (byte)ModbusTcpSequenceId;
		ModbusTcpSequenceId = ModbusTcpSequenceId+1;
		System.out.println("create read seq id is"+Integer.toString(ModbusTcpSequenceId));
		if(ModbusTcpSequenceId>=0xffff)
			ModbusTcpSequenceId = 0;
		sendBuf[2] = 0;
		sendBuf[3] = 0;
		sendBuf[4] = (byte)(MODBUS_TCP_READ_CMD_LENGTH>>8);
		sendBuf[5] = (byte)(MODBUS_TCP_READ_CMD_LENGTH);
		sendBuf[6] = (byte)(modbusId);
		sendBuf[7] = MODBUS_READ_COMMAND;
		sendBuf[8] = (byte)(firstAddr>>8);
		sendBuf[9] = (byte)firstAddr;
		sendBuf[10] = (byte)(length>>8);
		sendBuf[11] = (byte)length;
	}
	
	private void createModbusSingleWriteCmd(int modbusId, int Addr, int value, byte[] sendBuf){
		sendBuf[0] = (byte)(ModbusTcpSequenceId>>8);
		sendBuf[1] = (byte)ModbusTcpSequenceId;
		ModbusTcpSequenceId = ModbusTcpSequenceId+1;
		System.out.println("create single write seq id is"+Integer.toString(ModbusTcpSequenceId));
		if(ModbusTcpSequenceId>=0xffff)
			ModbusTcpSequenceId = 0;
		sendBuf[2] = 0;
		sendBuf[3] = 0;
		sendBuf[4] = (byte)(MODBUS_TCP_SINGLE_WRITE_CMD_LENGTH>>8);
		sendBuf[5] = (byte)(MODBUS_TCP_SINGLE_WRITE_CMD_LENGTH);
		sendBuf[6] = (byte)(modbusId);
		sendBuf[7] = MODBUS_SINGLE_WRITE_COMMAND;
		sendBuf[8] = (byte)(Addr>>8);
		sendBuf[9] = (byte)Addr;
		sendBuf[10] = (byte)(value>>8);
		sendBuf[11] = (byte)value;
	}
	
	
	
}