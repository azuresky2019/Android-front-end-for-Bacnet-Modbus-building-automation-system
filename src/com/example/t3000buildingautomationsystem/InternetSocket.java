package com.example.t3000buildingautomationsystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;


public class InternetSocket {

	private final static String LOG_TAG = InternetSocket.class.getSimpleName(); 
	Socket socket;
	DataInputStream dis; 
	DataOutputStream dos;
	
	boolean Client(String IP, int port){
		boolean success = true; //使用socket.isconnected会崩溃，用此处代替
		try{
			// 创建一个socket流连接到目标主机
			socket = new Socket(IP, port);
			// 输入流读出数据，输出流写入数据
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		}catch(IOException e){
			success = false;
			e.printStackTrace();
		}
		return success;
	}
	
	void ClientClose(){
		try{
			// 关闭socket
			socket.close();
			dis = null;
			dos = null;
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	// 写数据到socket
	void WriteInternetByte(byte[] buffer){
		try{
			dos.write(buffer);
			dos.flush();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	// 从socket读数据
	boolean ReadInternet(byte[] rsvBuf){
		try{
			// TODO
			//byte[] rsvBuf = new byte[128]; 
			dis.read(rsvBuf);
			System.out.println("***Receive TCP:");//+ new String(rsvBuf));
			//System.out.println(rsvBuf);
			//for(byte thebyte:rsvBuf){
			//	System.out.println(Integer.toHexString(thebyte));
			//}
			//getIp.setText(new String(rsvBuf).subSequence(0, 36));
			return true;
		}catch(IOException ioe){
			System.out.println("&&&Timeout receive");
			ioe.printStackTrace();
			return false;
		}
	}
	
    boolean startPing(String ip){   
    	System.out.println("Ping"+"startPing...");  
	    boolean success=false;  
	    Process p =null;  
	      
	     try {   
	            p = Runtime.getRuntime().exec("ping -c 1 -i 0.2 -W 1 " +ip); 
	            //  -c 1为发送的次数，1为表示发送1次，-w 表示发送后等待响应的时间。
	            int status = p.waitFor();   
	            if (status == 0) {   
	                success=true;   
	            } else {   
	                success=false;    
	            }   
	            } catch (IOException e) {   
	                success=false;     
	            } catch (InterruptedException e) {   
	                success=false;     
	            }finally{  
	                p.destroy();  
	            }  
	           
	     return success;  
    }  
    
    
    private String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(LOG_TAG, ex.toString());
        }
        return null;
    }
    
    public String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        return ip.getHostAddress();
                    }
                }
            }
        }
        catch(SocketException e)
        {
                Log.e("feige", "获取本地ip地址失败");
                e.printStackTrace();
        }
        return ipaddress;
    }
}