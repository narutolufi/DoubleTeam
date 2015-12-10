package com.doubleteam.ntp.client;

import java.net.InetAddress;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;

public class SntpClientInfo {
	private NTPUDPClient ntpudpClient;
	private long time;//当前时间
	private String delay;//延时
	private String offset;//偏差
	private String mode;//模式
	private String originatetime;//请求时间
	private String recievetime;//到达时间
	private String transmittime;//响应时间
	private String referencetime;//返回时间
	private String stratum;//协议类型
	private int leap;//同步状态
	private int version;//版本号
	private int precision;//精确度
	private int poll;//轮换时间
	private float rootdelay;//总延时
	private float rootdisperion;//最大误差
	private String ip;//ip地址

	public long getTime() {
		return time;
	}

	public String getDelay() {
		return delay;
	}

	public String getOffset() {
		return offset;
	}

	public String getMode() {
		return mode;
	}

	public String getOriginatetime() {
		return originatetime;
	}

	public String getRecievetime() {
		return recievetime;
	}

	public String getTransmittime() {
		return transmittime;
	}

	public String getReferencetime() {
		return referencetime;
	}

	public String getStratum() {
		return stratum;
	}

	public int getLeap() {
		return leap;
	}

	public int getVersion() {
		return version;
	}

	public int getPrecision() {
		return precision;
	}

	public int getPoll() {
		return poll;
	}

	public float getRootdelay() {
		return rootdelay;
	}

	public float getRootdisperion() {
		return rootdisperion;
	}

	public String getIp() {
		return ip;
	}

	public SntpClientInfo() {
		ntpudpClient = new NTPUDPClient();
	}

	public boolean requestTime(String serve, int timeout) {
		boolean result = true;
		try {
			InetAddress host = InetAddress.getByName(serve);
			long requsttime = System.currentTimeMillis();
			ntpudpClient.open();
			ntpudpClient.setSoTimeout(timeout);
			TimeInfo info = ntpudpClient.getTime(host);
			delay = (System.currentTimeMillis() - requsttime) + "ms";
			
			time = info.getMessage().getTransmitTimeStamp().getDate().getTime();  
		        
		//	time = info.getReturnTime();//返回时间
			//	delay = info.getDelay();//延时时间
			offset = (time - info.getReturnTime()) + "ms";//时差
			//	offset = info.getOffset();//时差
			NtpV3Packet ntpV3Packet = info.getMessage();
			mode = ntpV3Packet.getModeName();//模式
			originatetime = ntpV3Packet.getOriginateTimeStamp().toDateString().substring(16);//请求时间
			recievetime = ntpV3Packet.getReceiveTimeStamp().toDateString().substring(16);//到达时间
			transmittime = ntpV3Packet.getTransmitTimeStamp().toDateString().substring(16);//响应时刻
			referencetime = ntpV3Packet.getReferenceTimeStamp().toDateString().substring(16);//返回时刻
			stratum = ntpV3Packet.getStratum() + ntpV3Packet.getType();//协议类型
			leap = ntpV3Packet.getLeapIndicator();//同步状态
			version = ntpV3Packet.getVersion();//b版本号
			mode = ntpV3Packet.getModeName() + "(" + ntpV3Packet.getMode() + ")";//模式
			precision = ntpV3Packet.getPrecision();//精确度
			poll = ntpV3Packet.getPoll();//轮换时间
			rootdelay = ntpV3Packet.getRootDelay() / 1000.0f;//总延时
			rootdisperion = ntpV3Packet.getRootDispersion() / 1000.0f;//最大误差
			ip = ntpV3Packet.getReferenceIdString();////ip地址
			System.out.println(ntpV3Packet.getReferenceIdString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}
