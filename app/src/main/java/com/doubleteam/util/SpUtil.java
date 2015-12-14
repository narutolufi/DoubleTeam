package com.doubleteam.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {

    private static final String PREF_NAME = "settings.preference";
    private static SpUtil instance;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor spEditor;

    private SpUtil() {
    }

    public static SpUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SpUtil();
        }
        if (preferences == null && context != null) {
            preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            spEditor = preferences.edit();
        }
        return instance;
    }

    public String getSettingServer() {
        return preferences.getString("server", "pool.ntp.org");
    }

    public boolean setSettingServer(String server){
        spEditor.putString("server", server);
        return spEditor.commit();
    }

    public String getSettingOtherServer() {
        return preferences.getString("other_server", "pool.ntp.org");
    }

    public boolean setSettingOtherServer(String other_server){
        spEditor.putString("other_server", other_server);
        return spEditor.commit();
    }

    public int getSettingsOvertime(){
        return preferences.getInt("overtime", 30);
    }

    public boolean setSettingsOvertime(int overtime){
        spEditor.putInt("overtime", overtime);
        return spEditor.commit();
    }

    public String getSettingsTimedepart(){
        return preferences.getString("timedepart", "120秒");
    }

    public boolean setSettingsTimedepart(String timedepart){
        spEditor.putString("timedepart", timedepart);
        return spEditor.commit();
    }

    public boolean getSettingsAutoSync(){
        return preferences.getBoolean("auto_sync", false);
    }

    public boolean setSettingsAutoSync(boolean auto_sync){
        spEditor.putBoolean("auto_sync", auto_sync);
        return spEditor.commit();
    }
}
