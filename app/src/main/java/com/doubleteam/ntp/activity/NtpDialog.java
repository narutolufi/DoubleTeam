package com.doubleteam.ntp.activity;

import java.util.TimeZone;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.doubleteam.ntp.R;
import com.doubleteam.ntp.client.SntpClientInfo;

public class NtpDialog extends Dialog {

	private SntpClientInfo sntpClient;
	private String server;
	private int timeout;
	private Context context;
	private final static int UPDATEDONE = 1;
	private final static int UPDATEERRO = 0;
	
	private Button btnUpdate;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATEDONE:
				refresh();
				break;
			case UPDATEERRO:
				Toast.makeText(context, "¸üÐÂÊ§°Ü£¡", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

	};

	public NtpDialog(Context context, String server, int timeout) {
		super(context);
		this.context = context;
		sntpClient = new SntpClientInfo();
		this.server = server;
		this.timeout = timeout;
		// TODO Auto-generated constructor stub
	}

	private void update() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (sntpClient.requestTime(server, timeout)) {
					handler.sendEmptyMessage(UPDATEDONE);
				} else {
					handler.sendEmptyMessage(UPDATEERRO);
				}

			}
		}).start();
	}

	private void refresh() {
		setText(R.id.dely, sntpClient.getDelay());
		setText(R.id.time_deviton, sntpClient.getOffset());
		setText(R.id.requst_time, sntpClient.getOriginatetime());
		setText(R.id.arrive_time, sntpClient.getRecievetime());
		setText(R.id.respone_time, sntpClient.getTransmittime());
		setText(R.id.return_time, sntpClient.getReferencetime());
		setText(R.id.requst_time, sntpClient.getOriginatetime());
		setText(R.id.type, sntpClient.getStratum());
		setText(R.id.leap, sntpClient.getLeap() + "");
		setText(R.id.version, sntpClient.getVersion() + "");
		setText(R.id.mode, sntpClient.getMode());
		setText(R.id.precision, sntpClient.getPrecision() + "");
		setText(R.id.poll, sntpClient.getPoll() + "");
		setText(R.id.rootdely, sntpClient.getRootdelay() + "");
		setText(R.id.maxdely, sntpClient.getRootdisperion() + "");
		setText(R.id.IP, sntpClient.getIp());
		btnUpdate.setEnabled(true);
	}

	private void setText(int id, String text) {
		((TextView) findViewById(id)).setText(text);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ntpdailog);
		//TimeZone.getDefault().getID();
		setText(R.id.time_zone, TimeZone.getDefault().getID());
		onclicklisenler name = new onclicklisenler();
		
		btnUpdate = (Button) findViewById(R.id.update);
		btnUpdate.setOnClickListener(name);
		btnUpdate.setEnabled(false);
		findViewById(R.id.close).setOnClickListener(name);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		update();
		super.show();
	}
class onclicklisenler implements View.OnClickListener
{

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.update:
			update();
			break;
		case R.id.close:
			dismiss();
			break;
		}
	}

	

	
}
	
}
