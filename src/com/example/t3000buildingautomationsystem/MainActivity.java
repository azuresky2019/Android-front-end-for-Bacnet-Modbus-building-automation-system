package com.example.t3000buildingautomationsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	final static byte PRODUCT_TSTAT_5B = 1;
	final static byte PRODUCT_TSTAT_5A = 2;
	final static byte PRODUCT_TSTAT_5B2 = 3;
	final static byte PRODUCT_TSTAT_5C = 4;
	final static byte PRODUCT_TSTAT_6 = 6;
	final static byte PRODUCT_TSTAT_7 = 7;
	final static byte PRODUCT_TSTAT_5D = 12;
	final static byte PRODUCT_TSTAT_5E = 16;
	final static byte PRODUCT_TSTAT_5F = 17;
	final static byte PRODUCT_TSTAT_5G_LED = 18;
	final static byte PRODUCT_TSTAT_5H = 19;
	final static byte PRODUCT_T3_8I13O = 20;
	final static byte PRODUCT_T3_8IAO = 21;
	final static byte PRODUCT_T3_32AI = 22;
	final static byte PRODUCT_T3_8AI16O = 23;
	final static byte PRODUCT_ZIGBEE = 24;
	final static byte PRODUCT_FLEX_DRIVER = 25;
	final static byte PRODUCT_T3_8I13O_2 = 26;
	final static byte PRODUCT_T3_PER = 27;
	final static byte PRODUCT_T3_4AO = 28;
	final static byte PRODUCT_T3_6CT = 29;
	final static byte PRODUCT_MINI_PANEL = 35;
	final static byte PRODUCT_PM5E = 41;
	final static byte PRODUCT_CM = 50;
	final static byte PRODUCT_ISTAT6 = 51;
	final static byte PRODUCT_NC = 100;
	
	//public String mIP = "192.168.0.212";
	//public int mPort = 10000;
	public Integer listenPort = 1234;
    DatagramSocket ds = null;
    Vector<DeviceDetail> deviceVector = new Vector<DeviceDetail>();
	
	private static final String UDP_BROADCAST_ADDRESS = "255.255.255.255";
	private final static int SCAN_DEVICE_LOOP_TIMEOUT = 3000;
	
	WifiManager.MulticastLock mWifilock;
	InternetSocket mInternetSocket = new InternetSocket();
	
	private String deviceNameString;
	
	public static boolean isNewDeviceHere = false;
	
	private static Handler loopHandler = new Handler();
	
	//LinearLayout ll;
	private ListView lv;

    //private OnItemClickListener mOnItemClickListener;
	private SimpleAdapter adapter = null;
	private Map<String, Object> map = null;//new HashMap<String, Object>();
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	
	//private ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //禁止屏幕锁屏
		setContentView(R.layout.list_view);

		lv = (ListView)findViewById(R.id.listview);
		lv.setOnItemClickListener(new OnItemClickListener()
	    {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				TstatActivity.mDevice = deviceVector.get(position);
				final Intent intent = new Intent(MainActivity.this, TstatActivity.class);
				startActivity(intent);
			}   
	    } );
		adapter = new SimpleAdapter(this,list,R.layout.activity_main,
                new String[]{"title","info","img"},
                new int[]{R.id.title,R.id.info,R.id.img});
		//listView = new ListView(this);
        lv.setAdapter(adapter);//new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getData()));
        //setListAdapter(adapter);
        
	    WifiManager manager = (WifiManager) this  
                .getSystemService(Context.WIFI_SERVICE);  
	    mWifilock= manager.createMulticastLock("test wifi");

	    //ll = new LinearLayout(this);
	    //ll.setOrientation(LinearLayout.VERTICAL);
	    //ll.layout(l, t, r, b)
	    
	    Toast.makeText(this, "Scanning......", Toast.LENGTH_LONG).show();

        try {
			ds = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		new udpBroadcast().start();
		new udpReceive().start();
		//UdpSendByteArray(getDeviceInfo);
		
		loopHandler.post(ScanDeviceRunnable);
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private Runnable ScanDeviceRunnable= new Runnable(){
		
		//boolean success = false;
		@Override
		public void run(){
		
			if(deviceVector.isEmpty()==false)
			{
				for(int i= 0;i<deviceVector.size();i++){
					if(deviceVector.get(i).isShow == false){
						map = new HashMap<String, Object>();
						map.put("title", deviceVector.get(i).name);
						//map.put("info", "☀    ❆    ☽   ☾    ⁂    ☽ ☾  ☽ ☾   19℃");
						String newStr = new String("IP:"+deviceVector.get(i).ipAddr+":"+deviceVector.get(i).ipPort
								+"   ID:"+deviceVector.get(i).modbusId);
						map.put("info", newStr);
						if(deviceVector.get(i).productId == PRODUCT_TSTAT_6)
							map.put("img", R.drawable.tstat6_pic);
						if(deviceVector.get(i).productId == PRODUCT_MINI_PANEL)
							map.put("img", R.drawable.lb_pic);
				        list.add(map);
				        adapter.notifyDataSetChanged();
				        deviceVector.get(i).isShow = true;
					}
		        //isNewDeviceHere = false;
				}
			}
			loopHandler.postDelayed(ScanDeviceRunnable, SCAN_DEVICE_LOOP_TIMEOUT);
		}
	};	
	

	
	/*private ListView.onItemClickListener mOnItemClickListener = new onItemClickListener(){
	    @Override
	    protected void onListItemClick(AdapterView<?> arg0, View view,
                int position, long id){
	        //Do stuff
	    }
	};
	@Override
	mOnItemClickListener = new onListItemClick(ListView l, View v, int position, long id){
		//String str = new String("Position"+position+"id"+id);
		//String str = deviceVector.get(position).name;
		//list.get(position).put("info", str);
		//adapter.notifyDataSetChanged();
		TstatActivity.mDevice = deviceVector.get(position);
		final Intent intent = new Intent(MainActivity.this, TstatActivity.class);
		startActivity(intent);
	}*/
	/*private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
 
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "lijun testing");
        map.put("info", "☀    ❆    ☽   ☾    ⁂    ☽ ☾  ☽ ☾   19℃");
        map.put("img", R.drawable.lb_pic);
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("title", "bedroom");
        map.put("info", "off");
        map.put("img", R.drawable.tstat6_pic);
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("title", "parlour");
        map.put("info", "off");
        map.put("img", R.drawable.tstat6_pic);
        list.add(map);
         
        return list;
    }*/
	

	
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
					e1.printStackTrace();
				}   
	        	//handler.sendMessage(); //告诉主线程执行任务 
	            try {
	                //s = new MulticastSocket();
	            	//mWifilock.acquire();
	            	//TODO  此处用模拟器测试，需用192.168.0.255,实际使用要用255.255.255.255
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
	                //System.out.println("alloc datagram socket");
	                try{
	                	//this.lock.acquire();
	                	//System.out.println("try to receive something");
	                	ds.setSoTimeout(2000);
	                	ds.receive(dp);
	                	//System.out.println("ds.receive(dp)");
	                	String strMsg = new String(dp.getData()).trim();
	                	byte[] byteMsg = dp.getData(); //new Byte[dp.getLength()];
	                	final String questIp = dp.getAddress().toString();
	                	System.out.println("收到来自："+questIp.substring(1)+"的UDP消息");
	                	System.out.println("收到内容："+strMsg);
	                	DeviceDetail tempDevice = new DeviceDetail();
	                	tempDevice.name = new String(strMsg.substring(37,50));
	                	tempDevice.ipAddr = new String(questIp.substring(1));
	                	tempDevice.ipPort = (int)byteMsg[24]+0x100*byteMsg[25];//dp.getPort();
	                	tempDevice.length = (int)byteMsg[2];//Integer.parseInt(strMsg.substring(2));
	                	tempDevice.seqNum = (int)byteMsg[4]+0x100*(int)byteMsg[6]+0x10000*(int)byteMsg[8]+0x1000000*(int)byteMsg[10];
	                	tempDevice.productId = (int)byteMsg[12];
	                	if(byteMsg[14]<0)
	                		tempDevice.modbusId = (int)byteMsg[14]+256;
	                	else
	                		tempDevice.modbusId = (int)byteMsg[14];
	                	//tempDevice.ipPort = Integer.parseInt(strMsg.substring(24,25));
	                	//System.out.println("Device Infomation: length-");
	                	
	                	if(deviceVector.isEmpty()){
	                		System.out.println("Vector empty**************************");
	                		//if(questIp.substring(1).equals("192.168.0.212"))
	                		{
		                		deviceVector.addElement(tempDevice);
	                			isNewDeviceHere = true;   //用来让ll组件显示
	                			deviceNameString = new String(strMsg.substring(37,50));
		                		//deviceVector.add(tempDevice);
	                		}
	                	}else{
	                		//System.out.println("Vector NOT empty**************************");
	                		
	                		for(int i=0;i<deviceVector.size();i++){
	                			if(tempDevice.seqNum == deviceVector.get(i).seqNum){
	                				System.out.println("Same sequenes num................");
	                				break;
	                			}else if(i==(deviceVector.size()-1)){
	                				System.out.println("Vector NOT empty**************************"+i);
	                				deviceVector.addElement(tempDevice);
	    	                		isNewDeviceHere = true;
	                			}
	                		}
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
	                	//System.out.println("接收失败");
	                	e.printStackTrace();
	                }
	            } catch (Exception e) {
	            	//System.out.println("申请失败");
	                e.printStackTrace();
	            }
			}
        }
	}
	
	
 /*   private int parseScanResponse(byte[] buffer)
    {
    	 short usDataPackage[40]={0};
    	 if(buffer.length>=0)
    	 {
    	  memcpy(usDataPackage,buffer+4,buffer.length*sizeof(short));

    	  long nSerial=usDataPackage[0]+usDataPackage[1]*256+usDataPackage[2]*256*256+usDataPackage[3]*256*256*256;
    	  int nproduct_id = usDataPackage[4];
    	  String nproduct_name = GetProductName(nproduct_id);
    	  if(nproduct_name.IsEmpty()) 
    	   //return m_refresh_net_device_data.size();
    		  return mDevice.size();

    	  String nip_address;
    	  //nip_address.Format(_T("%d.%d.%d.%d"),usDataPackage[6],usDataPackage[7],usDataPackage[8],usDataPackage[9]);

    	  int nport = usDataPackage[10];
    	  int nsw_version = usDataPackage[11];
    	  int nhw_version = usDataPackage[12];

    	  int modbusID=usDataPackage[5];
    	  //TRACE(_T("Serial = %u     ID = %d\r\n"),nSerial,modbusID);
    	  //g_Print.Format(_T("Refresh list :Serial = %u     ID = %d ,ip = %s  , Product name : %s"),nSerial,modbusID,nip_address ,nproduct_name);
    	  //DFTrace(g_Print);
    	  mDevice.ipPort = nport;
    	  mDevice.swRev = nsw_version;
    	  mDevice.hardRev = nhw_version;
    	  mDevice.ipAddr = nip_address;
    	  mDevice.productId = nproduct_id;
    	  mDevice.modbusId = modbusID;
    	  mDevice.seqNum = nSerial;
    	  //temp.NetCard_Address=local_enthernet_ip;

    	  //temp.sw_version = usDataPackage[11];
    	  //temp.hw_version = usDataPackage[12];
    	  mDevice.seqNumFather = usDataPackage[13] + usDataPackage[14]*65536;
    	  //unsigned char temp_obj[2];
    	  //unsigned char temp_panel[2];
    	  //memcpy(temp_obj,&usDataPackage[15],2);
    	  //memcpy(temp_panel,&usDataPackage[16],2);
    	  //temp.object_instance = temp_obj[0]*256 + temp_obj[1];
    	  //temp.panal_number = temp_panel[0];
    	 // temp.object_instance = usDataPackage[15] >>8 + (usDataPackage[15]&0x00ff)<<8;
    	  boolean find_exsit = false;

    	  for (int i=0;i<mDevice.size();i++)
    	  {
    	   if(mDevice.at(i).nSerial == nSerial)
    	   {
    	    find_exsit = true;
    	    break;
    	   }
    	  }

    	  if(!find_exsit)
    	  {
    		  //mDevice.push_back(temp);
    	  }
    	 }

    }*/
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
