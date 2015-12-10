package com.doubleteam.ntp.activity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AnalogClock;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doubleteam.ntp.R;
import com.doubleteam.ntp.client.SntpClient;
import com.doubleteam.service.TimeService;
import com.doubleteam.syctask.LocalTimer;

public class MainActivity extends MyActivity implements OnClickListener {

	private LocalTimer localTimer;

	private TextView tvOffset;
	private TextView tvSystemClock;
	private TextView tvAtomicClock;
	private TextView tvLastUpdate;
	private TextView tvTitle;
	private AnalogClock acClock;

	private boolean isRoot;

	private final static int FAIL_TIME = 0;
	private final static int SUCCESS_TIME = 1;
	public final static int LOCATION_DONW = 2;

	private boolean isOtherServer;
	private boolean isProcessing;
	private SharedPreferences preferenceSetting;
	

	private MsgReceiver msgReceiver;
	
	@Override
	protected void setMyView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		tvOffset = (TextView) findViewById(R.id.tv_offset);
		tvOffset.setOnClickListener(this);
		tvOffset.setText("等待获取标准时间");
		tvSystemClock = (TextView) findViewById(R.id.tv_system);
		tvSystemClock.setOnClickListener(this);
		tvAtomicClock = (TextView) findViewById(R.id.tv_atomic);
		tvAtomicClock.setOnClickListener(this);
		tvLastUpdate = (TextView) findViewById(R.id.tv_lasttime);
		tvLastUpdate.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tile);
		tvTitle.setOnClickListener(this);
		acClock = (AnalogClock) findViewById(R.id.ac_clock);
		acClock.setOnClickListener(this);
		LinearLayout llBackground = (LinearLayout) findViewById(R.id.ll_background);
		isRoot = false;
		try {
			Process process = Runtime.getRuntime().exec(new String[] { "su" });
			// System.out.println(process.exitValue());
			BufferedReader errReader = null;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			errReader = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			if (!errReader.ready()) {
				// Toast.makeText(this, "isroot", Toast.LENGTH_SHORT);
				isRoot = true;
				process.destroy();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SharedPreferences preferences = getSharedPreferences("lasttesttime",
				Context.MODE_PRIVATE);

		preferenceSetting = getSharedPreferences("setting",
				Context.MODE_PRIVATE);

		String lasttestime = preferences.getString("lasttesttime",
				getResources().getString(R.string.nolasttesttime));
		TextView lastview = (TextView) findViewById(R.id.tv_lasttime);
		lastview.setText(lasttestime);

		
		
		

		localTimer = new LocalTimer();
		localTimer.addTextView(tvSystemClock);
		localTimer.execute();

		// 开启本地更新
		initHandler(new HandlerListener() {

			@Override
			public void dealMessage(Message msg) {
				// TODO Auto-generated method stub
				isProcessing = false;
				switch (msg.arg1) {
				case FAIL_TIME:
					tvAtomicClock.setText("获取失败");
					break;
				case SUCCESS_TIME:
					updateOffset();
					/*
					 * if (preferenceSetting.getBoolean("auto_sync", false) ==
					 * true) { syncTime(); }
					 */
					break;
				}
			}
		});

		updateAtomicTime();

		int color = preferenceSetting.getInt("theme", -16777216);
		if (color == Color.rgb(255, 255, 255)) {
			int orange = Color.rgb(255, 128, 0);
			tvAtomicClock.setTextColor(orange);
			tvOffset.setTextColor(orange);
			tvSystemClock.setTextColor(orange);
			tvLastUpdate.setTextColor(orange);
			((TextView) findViewById(R.id.tile)).setTextColor(orange);
		}
		llBackground.setBackgroundColor(color);
		
		
		
		
		Intent intent = new Intent("com.doubleteam.service.MSG_ACTION");
		startService(intent);
		
		msgReceiver = new MsgReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.doubleteam.service.RECEIVER");
		registerReceiver(msgReceiver, intentFilter);
	}

	
	public void updateAtomicTime() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SntpClient client = new SntpClient();
				String server = preferenceSetting.getString("server",
						"pool.ntp.org");
				int overtime = (int) (preferenceSetting.getLong("overtime", 30) * 1000);
				if (server.equals("其他服务器")) {
					isOtherServer = true;
					server = preferenceSetting.getString("other_server",
							"pool.ntp.org");
				}
				if (client.requestTime(server, overtime)) {
					long now = client.getNtpTime()
							+ SystemClock.elapsedRealtime()
							- client.getNtpTimeReference();
					Date current = new Date(now);
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					System.out.println(format.format(current));
					// date = current;
					// 开启远程更新
					// localTimer = new LocalTimer();
					localTimer.setDate(current);
					if (localTimer.getTextViewCount() != 2) {
						localTimer.addTextView(tvAtomicClock);
					}
					Message msg = new Message();
					msg.arg1 = SUCCESS_TIME;
					sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.arg1 = FAIL_TIME;
					sendMessage(msg);
				}
			}
		}).start();
	}

	public void updateOffset() {
		double offset = localTimer.getDate().getTime()
				- System.currentTimeMillis();
		if (offset > 0) {
			tvOffset.setText("+" + String.format("%.6f", offset / 1000));
		} else {
			tvOffset.setText(String.format("%.6f", offset / 1000));
		}
	}

	// 自动同步时间
	public void timeSynchronization() {
		Date date = null;
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss");
			date = localTimer.getDate();
			String datetime = sdf.format(date);
			os.writeBytes("date -s \"" + datetime + "\"" + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {

		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		
		updateLastTime(getTime(date));

	}

	private void updateLastTime(String time) {
		SharedPreferences preferences = getSharedPreferences("lasttesttime",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("lasttesttime", time);
		editor.commit();
		TextView lastview = (TextView) findViewById(R.id.tv_lasttime);
		lastview.setText(time);
	}

	private String getTime(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	public void syncTime() {
		Date date = localTimer.getDate();
		if (date != null) {
			if (isRoot) {
				timeSynchronization();
				//tvOffset.setText(String.format("%.6f", 0f));
				//localTimer.setComit(true);
				updateOffset();
			} else {
				Toast.makeText(this, "时间同步没有权限设置时间", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "标准时间尚未获取成功", Toast.LENGTH_SHORT).show();
		}

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_update:
			localTimer.setComit(false);
			tvAtomicClock.setText("正在获取");
			updateAtomicTime();
			break;
		case R.id.action_sync:
			syncTime();
			break;// 自动同步

		case R.id.action_settings:
			activityJump(SettingActivity.class);
			break;// 设置
		}
		return false;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_atomic:
			if (isOtherServer) {
				new NtpDialog(this, preferenceSetting.getString("other_server",
						"pool.ntp.org"), 30000).show();
			} else {
				new NtpDialog(this, preferenceSetting.getString("server",
						"pool.ntp.org"), 30000).show();
			}

			break;
		case R.id.tv_system:
		case R.id.tv_offset:
			if (!isProcessing) {
				isProcessing = true;
				localTimer.setComit(false);
				tvAtomicClock.setText("正在获取");
				updateAtomicTime();
			} else {
				System.out.println("3");
				Toast.makeText(this, "正在获取", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tile:
		case R.id.ac_clock:
			activityJump(SettingActivity.class);
			break;
		case R.id.tv_lasttime:
			syncTime();
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(msgReceiver);
		
		super.onDestroy();
	}

	public class MsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			long date = intent.getLongExtra("date", 0);
			if (localTimer!= null) {
				localTimer.setDate(new Date(date));
				updateOffset();
				updateLastTime(getTime(new Date(date)));
			}
			
		}
		
	}
}
