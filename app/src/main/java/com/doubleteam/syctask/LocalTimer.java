package com.doubleteam.syctask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AnalogClock;
import android.widget.TextView;
import android.widget.Toast;

public class LocalTimer extends AsyncTask<Object, Object, Boolean> {

	private Timer timer = null;
	private final static int Delay = 1000;//延迟一秒
	private Date date = null;
	private List<TextView> listtext = null;
	private boolean iscomit = false;

	public int getTextViewCount() {
		return listtext.size();
	}

	public boolean getComit() {
		return iscomit;
	}

	public void setComit(boolean comit) {
		iscomit = comit;
	}

	public LocalTimer() {
		timer = new Timer();
		listtext = new ArrayList<TextView>();
	}

	public void addTextView(TextView textview) {
		listtext.add(textview);
	}

	public void distory() {
		if (timer != null)
			timer.cancel();
	}

	@Override
	protected Boolean doInBackground(Object... arg0) {
		// TODO Auto-generated method stub

//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				publishProgress();
//			}
//		}, 0, Delay);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					//System.out.println("run");
					publishProgress();
					try {
						Thread.sleep(Delay);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		return true;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	private String getRemoteTime() {
		long time = 0;
		if (date != null) {
			time = date.getTime();
			date.setTime(time + Delay);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(time));
	}

	private String getlocalTime() {
		long time = 0;
		time = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(time));
	}

	public Date getDate() {
		return date;
	}

	protected void onProgressUpdate(Object... result) {
		listtext.get(0).setText(getlocalTime());
		if (listtext.size() == 2) {
			listtext.get(1).setText(getRemoteTime());
		}

//		String time = null;
//		if (listtext.size() > 1) {
//			time = getRemoteTime();
//			listtext.get(1).setText(time);
//		}
//		
//		if (iscomit) {
//			listtext.get(0).setText(time);
//		} else {
//			listtext.get(0).setText(getlocalTime());
//		}


		super.onProgressUpdate(result);
	}

}
