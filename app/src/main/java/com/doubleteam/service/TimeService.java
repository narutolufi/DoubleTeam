package com.doubleteam.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import com.doubleteam.ntp.client.SntpClient;
import com.doubleteam.util.SpUtil;

public class TimeService extends Service {

    private SntpClient client;
    private SpUtil preferences;
    private boolean isRunning;

    private Intent intent = new Intent("com.doubleteam.service.RECEIVER");

    public TimeService() {
        super();
        isRunning = true;
        client = new SntpClient();

    }

    public void setRunning(boolean isRunning) {
        if (this.isRunning == true) {
            if (isRunning == false) {
                this.isRunning = false;
            }
        } else {
            if (isRunning == true) {
                this.isRunning = true;
                startsyc();
            }
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    private String getString(String s) {
        String resutlt = null;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            resutlt = matcher.group();
            break;
        }
        return resutlt;
    }

    private void startsyc() {

//        String time = preferences.getString("timedepart", "30");
        String time = preferences.getSettingsTimedepart();
        final long depattime;
        String me = getString(time);
        if (time.equals("120秒")) {
            depattime = Long.valueOf(me) * 1000;
        } else if (time.equals("15分钟") || time.equals("30分钟")) {
            depattime = Long.valueOf(me) * 1000 * 60;
        } else if (time.equals("1小时") || time.equals("3小时")
                || time.equals("6小时") || time.equals("12小时")) {
            depattime = Long.valueOf(me) * 1000 * 60 * 60;
        } else if (time.equals("1日") || time.equals("3日")) {
            depattime = Long.valueOf(me) * 1000 * 60 * 60 * 12;
        } else if (time.equals("1周")) {
            depattime = Long.valueOf(me) * 1000 * 60 * 60 * 12;
        } else {
            depattime = 0;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(depattime);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
//                    String server = preferences.getString("server", "pool.ntp.org");
                    String server = preferences.getSettingServer();
//                    int overtime = (int) (preferences.getLong("overtime", 30) * 1000);
                    int overtime = preferences.getSettingsOvertime();
                    if (server.equals("其他服务器")) {
                        server = preferences.getSettingOtherServer();
                    }
                    if (client.requestTime(server, overtime)) {
                        long now = client.getNtpTime()
                                + SystemClock.elapsedRealtime()
                                - client.getNtpTimeReference();
                        Date current = new Date(now);
                        Process process = null;
                        DataOutputStream os = null;

                        try {

                            process = Runtime.getRuntime().exec("su");
                            os = new DataOutputStream(process.getOutputStream());

                            SimpleDateFormat sdf = new SimpleDateFormat(
                                    "yyyyMMdd.HHmmss");
                            String datetime = sdf.format(current);
                            os.writeBytes("date -s \"" + datetime + "\"" + "\n");
                            os.writeBytes("exit\n");
                            os.flush();
                            process.waitFor();
                            intent.putExtra("date", now);
                            sendBroadcast(intent);
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

                    }
                }
            }
        }).start();
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        System.out.println("onCreate");
        startAutoSync();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = SpUtil.getInstance(getApplicationContext());

        boolean run = preferences.getSettingsAutoSync();
        if (this.isRunning == true) {
            if (run == false) {
                this.isRunning = false;
            }
        } else {
            if (run == true) {
                this.isRunning = true;
                startsyc();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startAutoSync() {
        preferences = SpUtil.getInstance(getApplicationContext());
        if (preferences.getSettingsAutoSync() == true) {
            isRunning = true;
            try {
                Runtime.getRuntime().exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }
            startsyc();
        } else {
            isRunning = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
