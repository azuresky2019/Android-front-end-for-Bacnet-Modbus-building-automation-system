package com.example.t3000buildingautomationsystem;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.t3000buildingautomationsystem.MainActivity;

public class TstatActivity extends Activity{
	


	private final static int GET_REGISTER_LOOP_TIMEOUT = 3000;
	
	ImageView testImageButton;
	ImageView dayOrNightImageButton;
	ImageView snowflakeImage;
	
	TextView deviceNameText;
	TextView temperatureText;
	TextView setpointText;
	TextView humidityText;
	TextView coolingSetpointText;
	TextView heatingSetpointText;
	TextView fanModeText;
	
	String deviceNameString;
	
	ImageView upCoolButton;
	ImageView downCoolButton;
	ImageView upHeatButton;
	ImageView downHeatButton;


	protected static final int KEY_HOLD_TIMEOUT = 600;
	public static Handler loopHandler = new Handler();

	public static DeviceDetail mDevice;// = new DeviceDetail();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //½ûÖ¹ÆÁÄ»ËøÆÁ
		setContentView(R.layout.tstat_detail);
		
		deviceNameText = (TextView)findViewById(R.id.device_name);
	    temperatureText = (TextView)findViewById(R.id.temperature_value);
	    setpointText = (TextView)findViewById(R.id.setpoint_value);
	    humidityText = (TextView)findViewById(R.id.humidity_value);
	    coolingSetpointText = (TextView)findViewById(R.id.coolingSetpoint_value);
	    heatingSetpointText = (TextView)findViewById(R.id.heatingSetpoint_value);
	    fanModeText = (TextView)findViewById(R.id.fanMode_value);
	    
	    dayOrNightImageButton = (ImageView)findViewById(R.id.sun_pic);
	    snowflakeImage = (ImageView)findViewById(R.id.snowflake_pic);
	    
	    upCoolButton = (ImageView)findViewById(R.id.upCool_button);
	    downCoolButton = (ImageView)findViewById(R.id.downCool_button);
	    upHeatButton = (ImageView)findViewById(R.id.upHeat_button);
	    downHeatButton = (ImageView)findViewById(R.id.downHeat_button);
	    
	    upCoolButton.setOnClickListener(mUpCoolButtonListener);
	    downCoolButton.setOnClickListener(mDownCoolButtonListener);
	    upHeatButton.setOnClickListener(mUpHeatButtonListener);
	    downHeatButton.setOnClickListener(mDownHeatButtonListener);
	    
	    dayOrNightImageButton.setOnClickListener(mDayOrNightImageButton);
	    

        loopHandler.post(CheckNewDeviceRunnable);
	   // testImageButton = new ImageView(this);
	   // testImageButton.setBackgroundResource(resid);
	   // testImageButton.setImageResource(R.drawable.tstat6_pic);
	   // testImageButton.setVisibility(View.GONE);
		//testImageButton = (ImageView)findViewById(R.id.first_device);
		
		//testImageButton.setOnClickListener(mtestImageButtonListener);
		//testImageButton.setOnLongClickListener(mtestImageLongListener);
	}
	
	public Runnable CheckNewDeviceRunnable = new Runnable(){
		
		@Override
		public void run(){
			//if(MainActivity.isNewDeviceHere){
			{
				//testImageButton.setVisibility(View.VISIBLE);
        		//ll.addView(testImageButton);
        		//setContentView(ll);
        		//MainActivity.isNewDeviceHere = false;

        		deviceNameText.setText(mDevice.name);
        		//new Thread(mDevice.GetTemperatureRunnable).start(); 
        		//new Thread(mDevice.GetSetpointRunnable).start();
        		new Thread(mDevice.GetAllRegistersRunnable).start();
        		temperatureText.setText(Float.toString(mDevice.currentTemperature)+"¡æ");
        		setpointText.setText(Float.toString(mDevice.currentSetpoint)+"¡æ");
        		humidityText.setText(Float.toString(mDevice.currentHumidity)+"%");
        		
        		
        		switch(mDevice.currentFanMode){
	        		case 0x00:
	        			fanModeText.setText("Tstat6cf");
	        			break;
	        		case 0x01:
	        			fanModeText.setText("Fan Off");
	        			break;
	        		case 0x02:
	        			fanModeText.setText("Medium Speed");
	        			break;
	        		case 0x03:
	        			fanModeText.setText("High Speed");
	        			break;
	        		case 0x04:
	        			fanModeText.setText("Auto");
	        			break;
	        		case 0x05:
	        			fanModeText.setText("Heat");
	        			break;
	        		case 0x06:
	        			fanModeText.setText("Cool");
	        			break;
	        		default:
	        			fanModeText.setText("    ");
	        			break;
        		}
        		if(mDevice.occupiedMode == 1){
        			dayOrNightImageButton.setImageResource(R.drawable.sun);
        			//dayOrNightImageButton.setVisibility(View.VISIBLE);
        			System.out.println("(((((((((((((((((((((((((dayOrNightImageButton set as sun");
        			coolingSetpointText.setText(Float.toString(mDevice.currentDayCoolingSetpoint)+"¡æ");
        			heatingSetpointText.setText(Float.toString(mDevice.currentDayHeatingSetpoint)+"¡æ");
        			//dayOrNightImageButton.setVisibility(View.GONE);
        		}else{
        			dayOrNightImageButton.setImageResource(R.drawable.moon);
        			//dayOrNightImageButton.setVisibility(View.VISIBLE);
        			System.out.println("))))))))))))))))))))))))))dayOrNightImageButton set as moon");
        			coolingSetpointText.setText(Float.toString(mDevice.currentNightCoolingSetpoint)+"¡æ");
        			heatingSetpointText.setText(Float.toString(mDevice.currentNightHeatingSetpoint)+"¡æ");
        			//dayOrNightImageButton.setVisibility(View.GONE);
        		}
        		
        		if(mDevice.currentCoolHeatMode == 0){
        			snowflakeImage.setImageResource(R.drawable.green);
        		}else if(mDevice.currentCoolHeatMode == 1){
        			snowflakeImage.setImageResource(R.drawable.snowflake);
        		}else if(mDevice.currentCoolHeatMode == 2){
        			snowflakeImage.setImageResource(R.drawable.fire);
        		}
			}
			loopHandler.postDelayed(CheckNewDeviceRunnable, GET_REGISTER_LOOP_TIMEOUT);
		}
	};
	
	private View.OnClickListener mDayOrNightImageButton = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if(MainActivity.isNewDeviceHere){
				if(mDevice.occupiedMode == 1){

        			mDevice.occupiedMode = 0;
					dayOrNightImageButton.setImageResource(R.drawable.moon);
					mDevice.createModbusSingleWriteCmd(mDevice.modbusId,
							TstatRegisterListEnum.getIdByName("MODBUS_INFO_BYTE"),
							//(int)(mDevice.currentDayOrNightMode & 0xfe),mDevice.writeBuffer);
							0,mDevice.writeBuffer);
					mDevice.mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
					//mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
				}else{

        			mDevice.occupiedMode = 1;
					dayOrNightImageButton.setImageResource(R.drawable.sun);
					mDevice.createModbusSingleWriteCmd(mDevice.modbusId,
							TstatRegisterListEnum.getIdByName("MODBUS_INFO_BYTE"),
							//(int)(mDevice.currentDayOrNightMode | 0x01),mDevice.writeBuffer);
							1,mDevice.writeBuffer);
					mDevice.mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
				}
			}
			
		}
	};
	
	private View.OnClickListener mUpCoolButtonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			upCoolButton.setAlpha(100);
			if(MainActivity.isNewDeviceHere){
				if(mDevice.occupiedMode == 1){
					if(mDevice.currentDayCoolingSetpoint!=0){
						mDevice.currentDayCoolingSetpoint = mDevice.currentDayCoolingSetpoint+1.0f;
						mDevice.createModbusSingleWriteCmd(mDevice.modbusId,
								TstatRegisterListEnum.getIdByName("MODBUS_DAY_COOLING_SETPOINT"),
								(int)(mDevice.currentDayCoolingSetpoint*10),mDevice.writeBuffer);
						mDevice.mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
						coolingSetpointText.setText(Float.toString(mDevice.currentDayCoolingSetpoint)+"¡æ");
						
					}
				}else{
					if(mDevice.currentNightCoolingSetpoint!=0){
						mDevice.currentNightCoolingSetpoint = mDevice.currentNightCoolingSetpoint+1.0f;
						mDevice.createModbusSingleWriteCmd(mDevice.modbusId,
								TstatRegisterListEnum.getIdByName("MODBUS_NIGHT_COOLING_SETPOINT"),
								(int)(mDevice.currentNightCoolingSetpoint*10),mDevice.writeBuffer);
						mDevice.mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
						coolingSetpointText.setText(Float.toString(mDevice.currentNightCoolingSetpoint)+"¡æ");
					}
				}
			}
			
			new Handler().postDelayed(new Runnable(){
				public void run(){
					upCoolButton.setAlpha(255);
				}
			}, KEY_HOLD_TIMEOUT);
		}
	};
	
	private View.OnClickListener mDownCoolButtonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			upCoolButton.setAlpha(100);
			if(MainActivity.isNewDeviceHere){
				if(mDevice.occupiedMode == 1){
					if(mDevice.currentDayCoolingSetpoint!=0){
						mDevice.currentDayCoolingSetpoint = mDevice.currentDayCoolingSetpoint-1.0f;
						mDevice.createModbusSingleWriteCmd(mDevice.modbusId,
								TstatRegisterListEnum.getIdByName("MODBUS_DAY_COOLING_SETPOINT"),
								(int)(mDevice.currentDayCoolingSetpoint*10),mDevice.writeBuffer);
						mDevice.mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
						coolingSetpointText.setText(Float.toString(mDevice.currentDayCoolingSetpoint)+"¡æ");
						
					}
				}else{
					if(mDevice.currentNightCoolingSetpoint!=0){
						mDevice.currentNightCoolingSetpoint = mDevice.currentNightCoolingSetpoint-1.0f;
						mDevice.createModbusSingleWriteCmd(mDevice.modbusId,
								TstatRegisterListEnum.getIdByName("MODBUS_NIGHT_COOLING_SETPOINT"),
								(int)(mDevice.currentNightCoolingSetpoint*10),mDevice.writeBuffer);
						mDevice.mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
						coolingSetpointText.setText(Float.toString(mDevice.currentNightCoolingSetpoint)+"¡æ");
					}
				}
			}
			
			new Handler().postDelayed(new Runnable(){
				public void run(){
					downCoolButton.setAlpha(255);
				}
			}, KEY_HOLD_TIMEOUT);
		}
	};
	
	private View.OnClickListener mUpHeatButtonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			upCoolButton.setAlpha(100);
			if(MainActivity.isNewDeviceHere){
				if(mDevice.occupiedMode == 1){
					if(mDevice.currentDayHeatingSetpoint!=0){
						mDevice.currentDayHeatingSetpoint = mDevice.currentDayHeatingSetpoint+1.0f;
						mDevice.createModbusSingleWriteCmd(mDevice.modbusId,
								TstatRegisterListEnum.getIdByName("MODBUS_DAY_HEATING_SETPOINT"),
								(int)(mDevice.currentDayHeatingSetpoint*10),mDevice.writeBuffer);
						mDevice.mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
						coolingSetpointText.setText(Float.toString(mDevice.currentDayHeatingSetpoint)+"¡æ");
						
					}
				}else{
					if(mDevice.currentNightHeatingSetpoint!=0){
						mDevice.currentNightHeatingSetpoint = mDevice.currentNightHeatingSetpoint+1.0f;
						mDevice.createModbusSingleWriteCmd(mDevice.modbusId,
								TstatRegisterListEnum.getIdByName("MODBUS_NIGHT_HEATING_SETPOINT"),
								(int)(mDevice.currentNightHeatingSetpoint*10),mDevice.writeBuffer);
						mDevice.mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
						coolingSetpointText.setText(Float.toString(mDevice.currentNightHeatingSetpoint)+"¡æ");
					}
				}
			}
			
			new Handler().postDelayed(new Runnable(){
				public void run(){
					upHeatButton.setAlpha(255);
				}
			}, KEY_HOLD_TIMEOUT);
		}
	};
	
	private View.OnClickListener mDownHeatButtonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			downHeatButton.setAlpha(100);
			if(MainActivity.isNewDeviceHere){
				if(mDevice.occupiedMode == 1){
					if(mDevice.currentDayHeatingSetpoint!=0){
						mDevice.currentDayHeatingSetpoint = mDevice.currentDayHeatingSetpoint-1.0f;
						mDevice.createModbusSingleWriteCmd(mDevice.modbusId,
								TstatRegisterListEnum.getIdByName("MODBUS_DAY_HEATING_SETPOINT"),
								(int)(mDevice.currentDayHeatingSetpoint*10),mDevice.writeBuffer);
						mDevice.mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
						coolingSetpointText.setText(Float.toString(mDevice.currentDayHeatingSetpoint)+"¡æ");
						
					}
				}else{
					if(mDevice.currentNightHeatingSetpoint!=0){
						mDevice.currentNightHeatingSetpoint = mDevice.currentNightHeatingSetpoint-1.0f;
						mDevice.createModbusSingleWriteCmd(mDevice.modbusId,
								TstatRegisterListEnum.getIdByName("MODBUS_NIGHT_HEATING_SETPOINT"),
								(int)(mDevice.currentNightHeatingSetpoint*10),mDevice.writeBuffer);
						mDevice.mInternetSocket.WriteInternetByte(mDevice.writeBuffer);
						coolingSetpointText.setText(Float.toString(mDevice.currentNightHeatingSetpoint)+"¡æ");
					}
				}
			}
			
			new Handler().postDelayed(new Runnable(){
				public void run(){
					downHeatButton.setAlpha(255);
				}
			}, KEY_HOLD_TIMEOUT);
		}
	};
}