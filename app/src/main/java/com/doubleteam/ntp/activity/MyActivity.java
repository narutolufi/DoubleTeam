package com.doubleteam.ntp.activity;

import com.doubleteam.ntp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Window;

public abstract class MyActivity extends Activity {

	private boolean keyDownEnable;
	
	private Class<?> backActivity = null;
	
	private Handler handler;
	
	private int activityInStyle;
	private int activityOutStyle;
	private int backInStyle;
	private int backOutStyle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setMyView();
		initData();
		initView();
		activityInStyle = R.anim.activity_move_in_from_right;
		activityOutStyle = R.anim.activity_move_out_to_left;
		backInStyle = R.anim.activity_move_in_from_left;
		backOutStyle = R.anim.activity_move_out_to_right;
	}
	
	protected void initHandler(final HandlerListener listener) {
		if (handler == null) {
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					listener.dealMessage(msg);
				}
			};
		}
		
	}
	
	protected Handler getHandler() {
		return handler;
	}
	
	protected boolean sendMessage(int what) {
		if (handler != null) {
			handler.sendEmptyMessage(what);
			return true;
		}
		
		return false;
	}
	
	protected boolean sendMessage(Message msg) {
		if (handler != null) {
			handler.sendMessage(msg);
			return true;
		}
		
		return false;
	}
	
	
	protected abstract void setMyView();
	
	protected abstract void initView();
	
	protected abstract void initData();
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (keyDownEnable) {
				back();
			} else {
				finish(); 
				android.os.Process.killProcess(android.os.Process.myPid()); //ÍêÈ«ÍË³ö
			}
			
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	protected void setBackActivity(Class<?> backActivity) {
		this.backActivity = backActivity;
		keyDownEnable = true;
	}
	
	protected void back() {
		if (backActivity != null) {
			Intent intent = new Intent(this, backActivity);
			startActivity(intent);
			overridePendingTransition(backInStyle, backOutStyle);
			finish();
		}
	}
	
	public void setBackStyle(int in, int out) {
		this.backInStyle = in;
		this.backOutStyle = out;
	}
	
	public void setActivityJumpStyle(int in, int out) {
		this.activityInStyle = in;
		this.activityOutStyle = out;
	}
	
	public void activityJumpNoFinish(Class<?> nextActivity) {
		keyDownEnable = true;
		Intent intent = new Intent(this, nextActivity);
		startActivity(intent);
		overridePendingTransition(activityInStyle, activityOutStyle);
	}
	
	public void activityJump(Class<?> nextActivity) {
		activityJumpNoFinish(nextActivity);
		finish();
	}
	
	interface HandlerListener {
		public void dealMessage(Message msg);
	}
}
