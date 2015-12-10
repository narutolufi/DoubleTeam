package com.doubleteam.ntp.client;

import java.net.InetAddress;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;

public class SntpClientInfo {
	private NTPUDPClient ntpudpClient;
	private long time;//��ǰʱ��
	private String delay;//��ʱ
	private String offset;//ƫ��
	private String mode;//ģʽ
	private String originatetime;//����ʱ��
	private String recievetime;//����ʱ��
	private String transmittime;//��Ӧʱ��
	private String referencetime;//����ʱ��
	private String stratum;//Э������
	private int leap;//ͬ��״̬
	private int version;//�汾��
	private int precision;//��ȷ��
	private int poll;//�ֻ�ʱ��
	private float rootdelay;//����ʱ
	private float rootdisperion;//������
	private String ip;//ip��ַ

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
		        
		//	time = info.getReturnTime();//����ʱ��
			//	delay = info.getDelay();//��ʱʱ��
			offset = (time - info.getReturnTime()) + "ms";//ʱ��
			//	offset = info.getOffset();//ʱ��
			NtpV3Packet ntpV3Packet = info.getMessage();
			mode = ntpV3Packet.getModeName();//ģʽ
			originatetime = ntpV3Packet.getOriginateTimeStamp().toDateString().substring(16);//����ʱ��
			recievetime = ntpV3Packet.getReceiveTimeStamp().toDateString().substring(16);//����ʱ��
			transmittime = ntpV3Packet.getTransmitTimeStamp().toDateString().substring(16);//��Ӧʱ��
			referencetime = ntpV3Packet.getReferenceTimeStamp().toDateString().substring(16);//����ʱ��
			stratum = ntpV3Packet.getStratum() + ntpV3Packet.getType();//Э������
			leap = ntpV3Packet.getLeapIndicator();//ͬ��״̬
			version = ntpV3Packet.getVersion();//b�汾��
			mode = ntpV3Packet.getModeName() + "(" + ntpV3Packet.getMode() + ")";//ģʽ
			precision = ntpV3Packet.getPrecision();//��ȷ��
			poll = ntpV3Packet.getPoll();//�ֻ�ʱ��
			rootdelay = ntpV3Packet.getRootDelay() / 1000.0f;//����ʱ
			rootdisperion = ntpV3Packet.getRootDispersion() / 1000.0f;//������
			ip = ntpV3Packet.getReferenceIdString();////ip��ַ
			System.out.println(ntpV3Packet.getReferenceIdString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = false;
		}
		return result;
	}
}
