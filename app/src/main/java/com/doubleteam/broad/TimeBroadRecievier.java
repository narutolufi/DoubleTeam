package com.doubleteam.broad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeBroadRecievier extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent intent2 = new Intent();
		intent2.setAction("com.doubleteam.sevice.TimeSevice");
		context.startService(intent2);
	}

}
