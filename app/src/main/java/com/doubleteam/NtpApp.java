package com.doubleteam;

import android.app.Application;

import com.doubleteam.util.SpUtil;

/**
 * Created by francis on 2015/12/15.
 */
public class NtpApp extends Application{

    public static SpUtil spUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        spUtil = SpUtil.getInstance(this);
    }
}
