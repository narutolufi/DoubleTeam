package com.doubleteam.ntp.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.doubleteam.ntp.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;

public class LogoActivity extends Activity {
	private Handler startHandler;

	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 取消标题
		setContentView(R.layout.activity_logo);
		startHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
					case 0x01:
						Intent intent = new Intent(LogoActivity.this,
								MainActivity.class);
						startActivity(intent);
						overridePendingTransition(
								R.anim.activity_move_in_from_right,
								R.anim.activity_move_out_to_left);
						finish();
						break;
				}
			}
		};
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				startHandler.sendEmptyMessage(0x01);
			}

		}, 1000);
	}

}
