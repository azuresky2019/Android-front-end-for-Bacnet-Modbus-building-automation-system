package com.example.t3000buildingautomationsystem; 

import android.os.Handler;

//import com.example.t3000buildingautomationsystem.TstatDetailActivity.displayModeEnum;
import com.example.t3000buildingautomationsystem.TstatRegisterListEnum;

public class DeviceDetail {
	

	private boolean mTcpConnected = false;
	private int ModbusTcpSequenceId = 0;
	InternetSocket mInternetSocket = new InternetSocket();
	
	public byte[] writeBuffer = new byte[12];
	
	float currentTemperature;
	float currentSetpoint;
	float currentHumidity;
	float currentDayCoolingSetpoint;
	float currentDayHeatingSetpoint;
	float currentNightCoolingSetpoint;
	float currentNightHeatingSetpoint;
	int currentFanMode;
	int currentDayOrNightMode;
	int currentCoolHeatMode;
	
	int occupiedMode;
	

	
	private final static byte MODBUS_READ_COMMAND = 3;
	private final static byte MODBUS_SINGLE_WRITE_COMMAND = 6;
	private final static int MODBUS_TEMPERATURE_REGISTER = 0x79;
	private final static int MODBUS_SETPOINT_REGISTER = 0x159;
	private final static int MODBUS_TCP_READ_CMD_LENGTH = 6;
	private final static int MODBUS_TCP_SINGLE_WRITE_CMD_LENGTH = 6;
	

	//private short[] registerBuf = new short[1024];
	private byte[] registerBuf = new byte[4096];
	private byte[] tempBuf = new byte[128];
	//测试用modbus id 1
	//	public final static int MODBUS_TEST_ID = 1;
		
	
	int length = 0;
	long seqNum = 0;
	int productId = 0;
	int modbusId = 0;
	String ipAddr;
	int ipPort = 0;
	int swRev = 0;
	int hardRev = 0;
	long seqNumFather = 0;
	String name;
	boolean isShow = false;
	
	int size(){
		return 0;
	}
	
	void at(int num){
		
	}
	
	
	Runnable GetTemperatureRunnable = new Runnable(){

		@Override
		public void run() {
			
			if(mTcpConnected == true){
				byte[] getTemp = new byte[12];
				int temperatureSeqId = ModbusTcpSequenceId;
				createModbusReadCmd(modbusId,MODBUS_TEMPERATURE_REGISTER,1,getTemp);
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
				mTcpConnected = mInternetSocket.Client(ipAddr,ipPort);
				System.out.println("Failed to connect");
			}
			
		}
		
	};
	
	Runnable GetSetpointRunnable = new Runnable(){

		@Override
		public void run() {
			byte[] getTemp = new byte[12];
			if(mTcpConnected == true){
				createModbusReadCmd(modbusId,MODBUS_SETPOINT_REGISTER,1,getTemp);
				mInternetSocket.WriteInternetByte(getTemp);
				mInternetSocket.ReadInternet(tempBuf);
				if( MODBUS_READ_COMMAND == tempBuf[7])
					currentSetpoint = (float)((int)tempBuf[9]*256+
							((int)tempBuf[10]&0xff))/10.0f; //此处byte想转为无符号int，需要与0xff
				//System.out.println(Float.toString(currentTemperature));
			}else{
				mTcpConnected = mInternetSocket.Client(ipAddr,ipPort);
			}
		}
		 
	};
	
	private int readRegisterIndex = 0;
	private byte[] readRegisterReceiveBuffer = new byte[512];
	public Runnable GetAllRegistersRunnable = new Runnable(){ 

		boolean ret = false;
		@Override
		public void run() {
			byte[] getTemp = new byte[12];
			if(mTcpConnected == true){
				//for(int i=0;i<1;i++){
				
					createModbusReadCmd(modbusId,100*readRegisterIndex,100,getTemp);
					mInternetSocket.WriteInternetByte(getTemp);
					try {
						Thread.currentThread();
						Thread.sleep(500);     //等待若干毫秒
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ret = mInternetSocket.ReadInternet(readRegisterReceiveBuffer);
					System.out.println("得到多读消息");
					//System.out.println(readRegisterReceiveBuffer.toString());
					/*for(byte thebyte:readRegisterReceiveBuffer){
						System.out.print(Integer.toHexString(thebyte)+" ");
					}*/
					//parseAllRegisterMessage(readRegisterReceiveBuffer);
					
					
						
					//currentTemperature = (float)((int)registerBuf[121*2]*256+((int)registerBuf[121*2+1]&0xff))/10.0f; //此处byte想转为无符号int，需要与0xff
					//System.out.println("温度寄存器的值"+Byte.toString(registerBuf[121*2]))
					
					/*try{
						System.arraycopy(registerBuf, readRegisterIndex*200, readRegisterReceiveBuffer, 6, 200);
					}catch(IndexOutOfBoundsException ex){
						// 发生越界异常，数据不会改变
						System.out.println("拷贝发生异常：数据越界。");
					}*/
					//byte[] test = {1,2,3,4};
					//System.arraycopy(registerBuf, 0, test, 2, 2);
					//registerBuf = readRegisterReceiveBuffer;
					for(byte thebyte:registerBuf){
						System.out.print(Integer.toHexString(thebyte)+" ");
						//System.out.print
					}
					
					if(ret == true){
						//break;    //若没有收到结果，终止循环
						if(readRegisterReceiveBuffer[7]==MODBUS_READ_COMMAND){
							for(int i=0;i<200;i++){
								registerBuf[readRegisterIndex*200+i] = readRegisterReceiveBuffer[i+9];
							}
							if( (TstatRegisterListEnum.getIdByName("MODBUS_TEMPRATURE_CHIP")>=readRegisterIndex*100)&&
									(TstatRegisterListEnum.getIdByName("MODBUS_TEMPRATURE_CHIP")<(readRegisterIndex*100+100))){
								currentCoolHeatMode = (int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_COOL_HEAT_MODE")*2+1];
								
								currentTemperature = (float)((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_TEMPRATURE_CHIP")*2]*256+
										((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_TEMPRATURE_CHIP")*2+1]&0xff))/10.0f; //此处byte想转为无符号int，需要与0xff
							
							
								currentHumidity = (float)((int)registerBuf[TstatRegisterListEnum.getIdByName("MODUBS_HUMIDITY_RH")*2]*256+
										((int)registerBuf[TstatRegisterListEnum.getIdByName("MODUBS_HUMIDITY_RH")*2+1]&0xff))/10.0f;
								
								currentDayOrNightMode = (int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_INFO_BYTE")*2+1];
														
								occupiedMode = currentDayOrNightMode & 0x01;
							}
								
							if( (TstatRegisterListEnum.getIdByName("MODBUS_DAY_SETPOINT")>=readRegisterIndex*100)&&
									(TstatRegisterListEnum.getIdByName("MODBUS_DAY_SETPOINT")<(readRegisterIndex*100+100))){
								currentSetpoint = (float)((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_DAY_SETPOINT")*2]*256+
										((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_DAY_SETPOINT")*2+1]&0xff))/10.0f;
							
							
								currentDayCoolingSetpoint = (float)((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_DAY_COOLING_SETPOINT")*2]*256+
										((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_DAY_COOLING_SETPOINT")*2+1]&0xff))/10.0f;
								
							
								currentDayHeatingSetpoint = (float)((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_DAY_HEATING_SETPOINT")*2]*256+
										((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_DAY_HEATING_SETPOINT")*2+1]&0xff))/10.0f;
							
							
								currentNightCoolingSetpoint = (float)((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_NIGHT_COOLING_SETPOINT")*2]*256+
										((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_NIGHT_COOLING_SETPOINT")*2+1]&0xff))/10.0f;
								
								currentNightHeatingSetpoint = (float)((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_NIGHT_HEATING_SETPOINT")*2]*256+
										((int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_NIGHT_HEATING_SETPOINT")*2+1]&0xff))/10.0f;
							}
							
							if( (TstatRegisterListEnum.getIdByName("MODBUS_FAN_SPEED")>=readRegisterIndex*100)&&
									(TstatRegisterListEnum.getIdByName("MODBUS_FAN_SPEED")<(readRegisterIndex*100+100))){
								currentFanMode = (int)registerBuf[TstatRegisterListEnum.getIdByName("MODBUS_FAN_SPEED")*2+1]&0xff;
							}
						}
						
						readRegisterIndex++;
						readRegisterIndex %= 8;
						System.out.println("readRegisterIndex="+Integer.toString(readRegisterIndex));
					}else{
						//Thread.
					}
				//}
				//System.out.println(Float.toString(currentTemperature));
			}else{
				mTcpConnected = mInternetSocket.Client(ipAddr,ipPort);
			}
			
			//loopHandler.postDelayed(GetAllRegistersRunnable, GET_REGISTER_LOOP_TIMEOUT);
		}
		 
	};
	
	private void parseAllRegisterMessage(byte[] inBuf){
		System.arraycopy(registerBuf, readRegisterIndex*200, inBuf, 6, 200);
		for(byte thebyte:inBuf){
			System.out.print(Integer.toHexString(thebyte)+" ");
		}
		System.out.print("列表数据");
		for(byte thebyte:registerBuf){
			System.out.print(Integer.toHexString(thebyte)+" ");
			//System.out.print
		}
	}
	
	
	
	private void createModbusReadCmd(int modbusId, int firstAddr, int length, byte[] sendBuf){
		sendBuf[0] = (byte)(ModbusTcpSequenceId>>8);
		sendBuf[1] = (byte)ModbusTcpSequenceId;
		ModbusTcpSequenceId = ModbusTcpSequenceId+1;
		//System.out.println("create read seq id is"+Integer.toString(ModbusTcpSequenceId));
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
	
	void createModbusSingleWriteCmd(int modbusId, int Addr, int value, byte[] sendBuf){
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