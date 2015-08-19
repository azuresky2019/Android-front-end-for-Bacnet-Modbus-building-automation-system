package com.example.t3000buildingautomationsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity {

	public String mIP = "192.168.0.212";
	public int mPort = 10000;
	public Integer listenPort = 1234;
    DatagramSocket ds = null;
    Vector<String> deviceVector = new Vector<String>();
	
    private static final byte PRODUCT_MINI_PANEL = 35;
    private static final byte PRODUCT_TSTAT_6 = 6;
	private static final String UDP_BROADCAST_ADDRESS = "255.255.255.255";
	private ImageView testImageButton;
	WifiManager.MulticastLock mWifilock;
	InternetSocket mInternetSocket = new InternetSocket();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止屏幕锁屏
		setContentView(R.layout.activity_main);
		
	    WifiManager manager = (WifiManager) this  
                .getSystemService(Context.WIFI_SERVICE);  
	    mWifilock= manager.createMulticastLock("test wifi");  

		testImageButton = (ImageView)findViewById(R.id.first_device);
		
		testImageButton.setOnClickListener(mtestImageButtonListener);
		testImageButton.setOnLongClickListener(mtestImageLongListener);

        try {
			ds = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		new udpBroadcast().start();
		new udpReceive().start();
		//UdpSendByteArray(getDeviceInfo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private View.OnClickListener mtestImageButtonListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			final Intent intent = new Intent(MainActivity.this, TstatDetailActivity.class);
			startActivity(intent);
		}
	};
	
	private void add(){
		LayoutInflater inflater = LayoutInflater.from(this);
		View addView = inflater.inflate(R.layout.input_name, null);
		final DialogWrapper wrapper = new DialogWrapper(addView);
		
		new AlertDialog.Builder(this)
			.setTitle(R.string.input_name)
			.setView(addView)
			.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0,
								int whichButton) {
							// TODO Auto-generated method stub
							
						}
					})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface arg0,
										int whichButton) {
									// ignore, just dismiss
								}
							})
							.show();
	}
	
	private View.OnLongClickListener mtestImageLongListener = new View.OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View arg0) {
			add();
			return false;
		}
	};

    //private void UdpSendByteArray( byte[] message )
	private class udpBroadcast extends Thread {
        //message = ( message == null ? {0x64,0,0,0,0} : message ); 
        int server_port = 1234;
        //MulticastSocket s = null;
        InetAddress local = null;
        DatagramPacket dj = null;
        byte[] getDeviceInfo = {0x64,0,0,0,0};
        
        @Override
        public void run() {
        	while(true){
	        	try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}   
	        	//handler.sendMessage(); //告诉主线程执行任务 
	            try {
	                //s = new MulticastSocket();
	            	//mWifilock.acquire();
	                local = InetAddress.getByName("192.168.0.255");//255.255.255.255");//"224.0.0.1");
	                dj = new DatagramPacket(getDeviceInfo,getDeviceInfo.length,local,server_port);
	                //System.out.println("Send UDP group broadcast");
	                ds.setBroadcast(true);
	                ds.send(dj);
	                //ds.disconnect();
	                //ds.close();
	                //mWifilock.release();
	                
	            } catch(IOException e) {
	                e.printStackTrace();
	            }
        	}
        }
        /*@Override
        public void run(){
	        try{  
	            s = new MulticastSocket();//DatagramSocket( );  //
	       // }catch ( SocketException e ){  
	       //     e.printStackTrace( );  
	        } catch (IOException e) {
				e.printStackTrace();
			} 
	        try{
	            // 换成服务器端IP  
	            local = InetAddress.getByName( UDP_BROADCAST_ADDRESS );  
	        }catch ( UnknownHostException e ){  
	            e.printStackTrace( );  
	        }  
	      
	        //int msg_length = message.length( );
	        //byte[] messagemessageByte = message.getBytes( );  
	        DatagramPacket p = new DatagramPacket( getDeviceInfo, getDeviceInfo.length, local,server_port ); 
	        //while(true)
	        {
		        try{  
		            s.send( p );  
		            System.out.println("Send UDP broadcast");
		        }catch ( IOException e ){  
		            e.printStackTrace();
		        } 
	        }
        }*/
    }  
	
	
	class DialogWrapper {
		EditText inputName = null;
		View base = null;
		
		DialogWrapper(View base){
			this.base = base;
			inputName=(EditText)base.findViewById(R.id.alias);
		}
		
		String getName(){
			return(getNameField().getText().toString());
		}
		
		private EditText getNameField(){
			if(inputName==null){
				inputName=(EditText)base.findViewById(R.id.alias);
			}
			return(inputName);
		}
	}
	
	private class udpReceive extends Thread{
		
		byte[] message = new byte[1024];
		/*@Override
		public void run(){
			try{
				InetAddress globalAddress = InetAddress.getByName(UDP_BROADCAST_ADDRESS);
			}catch (Exception e) {
                e.printStackTrace();
            }
		}*/
		
		@Override
		public void run(){
			System.out.println("start receive udp");
			while (true) {
				DatagramPacket dp = null;
				//DatagramSocket ds = null;
	            try {
	                //ds = new DatagramSocket(listenPort);
	                //ds.setBroadcast(true);
	                dp = new DatagramPacket(message,message.length);
	                System.out.println("alloc datagram socket");
	                try{
	                	//this.lock.acquire();
	                	System.out.println("try to receive something");
	                	ds.setSoTimeout(2000);
	                	ds.receive(dp);
	                	System.out.println("ds.receive(dp)");
	                	String strMsg = new String(dp.getData()).trim();
	                	final String questIp = dp.getAddress().toString();
	                	System.out.println("收到来自："+questIp.substring(1)+"的UDP消息");
	                	System.out.println("收到内容："+strMsg);
	                	if(deviceVector.isEmpty()){
	                		System.out.println("Vector empty**************************");
	                		if(questIp.substring(1).equals("192.168.0.212")){
		                		deviceVector.add(strMsg);
		                		testImageButton.setVisibility(View.VISIBLE);
	                		}
	                	}else{
	                		System.out.println("Vector NOT empty**************************");
	                	/*	long serialNumNew = 0;
	                		long serialNum = 0;
	                		serialNumNew = Byte.parseByte(strMsg.substring(4, 5))+0x100*Byte.parseByte(strMsg.substring(4, 5))
	                				+0x10000*Byte.parseByte(strMsg.substring(4, 5))+0x1000000*Byte.parseByte(strMsg.substring(4, 5));
	                		for(int i=0;i<deviceVector.size();i++){
	                			serialNum = Byte.parseByte(deviceVector.get(i).substring(4, 5))+0x100*Byte.parseByte(deviceVector.get(i).substring(4, 5))
		                				+0x10000*Byte.parseByte(deviceVector.get(i).substring(4, 5))+0x1000000*Byte.parseByte(deviceVector.get(i).substring(4, 5));
	                			if(serialNumNew == serialNum){
	                				break;
	                			}
	                		}*/
	                	}
	                	//lock.release();
	                }catch(IOException e){
	                	System.out.println("接收失败");
	                	e.printStackTrace();
	                }
	            } catch (Exception e) {
	            	System.out.println("申请失败");
	                e.printStackTrace();
	            }
			}
        }
	}
	
    
	/*
    // 按下返回键时，关闭 多播socket ms
    @Override
    public void onBackPressed() {
        ms.close();
        super.onBackPressed();
    }
    
    
	int AddNetDeviceForRefreshList(BYTE* buffer, int nBufLen,  sockaddr_in& siBind)
	{
	 int nLen=buffer[2]+buffer[3]*256;
	 //int n =sizeof(char)+sizeof(unsigned char)+sizeof( unsigned short)*9;
	 unsigned short usDataPackage[40]={0};
	 if(nLen>=0)
	 {
	  refresh_net_device temp;
	  memcpy(usDataPackage,buffer+4,nLen*sizeof(unsigned short));

	  DWORD nSerial=usDataPackage[0]+usDataPackage[1]*256+usDataPackage[2]*256*256+usDataPackage[3]*256*256*256;
	  int nproduct_id = usDataPackage[4];
	  CString nproduct_name = GetProductName(nproduct_id);
	  if(nproduct_name.IsEmpty()) //如果产品号 没定义过，不认识这个产品 就exit;
	   return m_refresh_net_device_data.size();

	  CString nip_address;
	  nip_address.Format(_T("%d.%d.%d.%d"),usDataPackage[6],usDataPackage[7],usDataPackage[8],usDataPackage[9]);

	  int nport = usDataPackage[10];
	  int nsw_version = usDataPackage[11];
	  int nhw_version = usDataPackage[12];

	  int modbusID=usDataPackage[5];
	  //TRACE(_T("Serial = %u     ID = %d\r\n"),nSerial,modbusID);
	  g_Print.Format(_T("Refresh list :Serial = %u     ID = %d ,ip = %s  , Product name : %s"),nSerial,modbusID,nip_address ,nproduct_name);
	  DFTrace(g_Print);
	  temp.nport = nport;
	  temp.sw_version = nsw_version;
	  temp.hw_version = nhw_version;
	  temp.ip_address = nip_address;
	  temp.product_id = nproduct_id;
	  temp.modbusID = modbusID;
	  temp.nSerial = nSerial;
	  temp.NetCard_Address=local_enthernet_ip;

	  temp.sw_version = usDataPackage[11];
	  temp.hw_version = usDataPackage[12];
	  temp.parent_serial_number = usDataPackage[13] + usDataPackage[14]*65536;
	  unsigned char temp_obj[2];
	  unsigned char temp_panel[2];
	  memcpy(temp_obj,&usDataPackage[15],2);
	  memcpy(temp_panel,&usDataPackage[16],2);
	  temp.object_instance = temp_obj[0]*256 + temp_obj[1];
	  temp.panal_number = temp_panel[0];
	 // temp.object_instance = usDataPackage[15] >>8 + (usDataPackage[15]&0x00ff)<<8;
	  bool find_exsit = false;

	  for (int i=0;i<(int)m_refresh_net_device_data.size();i++)
	  {
	   if(m_refresh_net_device_data.at(i).nSerial == nSerial)
	   {
	    find_exsit = true;
	    break;
	   }
	  }

	  if(!find_exsit)
	  {
	   m_refresh_net_device_data.push_back(temp);
	  }
	 }


	 return m_refresh_net_device_data.size();
	}*/
}
