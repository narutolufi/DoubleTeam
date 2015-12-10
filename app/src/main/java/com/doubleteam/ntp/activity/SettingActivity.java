package com.doubleteam.ntp.activity;

import com.doubleteam.ntp.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.KeyEvent;

public class SettingActivity extends PreferenceActivity implements OnPreferenceChangeListener,
		OnPreferenceClickListener {

	private String serverSetting;
	private String overtimeSetting;
	private String autoSync;
	private String themesSetting;
	private String otherServer;
	private ListPreference lpServer;
	private ListPreference lpOvertime;
	private ListPreference lpThemesSetting;
	private ListPreference lpTimedepart;
	private EditTextPreference etpOtherServer;
	private CheckBoxPreference cbpAutoSync;
	private Preference reset;
	private SharedPreferences preferences;

	private String[] color = { "黑色", "白色", "棕色" };
	private int[] colorValue = { -16777216, -1, -6401529 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
		preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);

		serverSetting = getResources().getString(R.string.server_setting);
		overtimeSetting = getResources().getString(R.string.overtime_setting);
		autoSync = getResources().getString(R.string.auto_sync);
		themesSetting = getResources().getString(R.string.themes_setting);

		lpServer = (ListPreference) findPreference(serverSetting);
		lpServer.setValue(preferences.getString("server", "pool.ntp.org"));
		lpServer.setOnPreferenceChangeListener(this);
		lpOvertime = (ListPreference) findPreference(overtimeSetting);
		lpOvertime.setValue(String.valueOf(preferences.getLong("overtime", 30)));
		lpOvertime.setOnPreferenceChangeListener(this);
		lpTimedepart = (ListPreference) findPreference(getResources().getString(R.string.time_depart));
		lpTimedepart.setOnPreferenceChangeListener(this);
		lpTimedepart.setValue(preferences.getString("timedepart", "1"));
		cbpAutoSync = (CheckBoxPreference) findPreference(autoSync);
		cbpAutoSync.setChecked(preferences.getBoolean("auto_sync", false));
		cbpAutoSync.setOnPreferenceChangeListener(this);
		reset = findPreference(getResources().getString(R.string.reset));
		reset.setOnPreferenceClickListener(this);
		findPreference(getResources().getString(R.string.about)).setOnPreferenceClickListener(this);
		lpThemesSetting = (ListPreference) findPreference(themesSetting);
		lpThemesSetting.setOnPreferenceChangeListener(this);
		otherServer = getResources().getString(R.string.other_server);
		etpOtherServer = (EditTextPreference) findPreference(otherServer);
		boolean isauto = preferences.getBoolean("auto_sync", false);
		findPreference(getResources().getString(R.string.time_depart)).setEnabled(isauto);

		String defaultTimeDepart = preferences.getString("timedepart", "120秒");
		lpTimedepart.setValue(defaultTimeDepart);
		if (preferences.getString("server", "pool.ntp.org").equals("其他服务器")) {
			etpOtherServer.setEnabled(true);
			etpOtherServer.setSummary(preferences.getString("other_server", "pool.ntp.org"));
		}
		etpOtherServer.setOnPreferenceChangeListener(this);
		int defaultColor = preferences.getInt("theme", Color.rgb(0, 0, 0));
		for (int i = 0; i < color.length; i++) {
			if (defaultColor == colorValue[i]) {
				lpThemesSetting.setValue(color[i]);
				break;
			}
		}

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_move_in_from_left, R.anim.activity_move_out_to_right);
			finish();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		Log.v("SysemSetting", "Preference is clicked");
		Log.v("Key", preference.getKey());
		Editor editor = preferences.edit();
		if (preference.getKey().equals(serverSetting)) {
			Log.v("change", "server");
			lpServer.setValue(newValue.toString());
			if (newValue.toString().equals("其他服务器")) {
				etpOtherServer.setEnabled(true);
			} else {
				etpOtherServer.setEnabled(false);
			}
			editor.putString("server", newValue.toString());
		} else if (preference.getKey().equals(overtimeSetting)) {
			Log.v("change", "overtime");
			editor.putLong("overtime", Long.valueOf(newValue.toString()));
		} else if (preference.getKey().equals(autoSync)) {
			Log.v("change", "auto");
			cbpAutoSync.setDefaultValue(newValue);
			findPreference(getResources().getString(R.string.time_depart)).setEnabled((Boolean) newValue);
			editor.putBoolean("auto_sync", (Boolean) newValue);
			if ((Boolean) newValue) {
				editor.putString("timedepart", lpTimedepart.getValue());
			}
		} else if (preference.getKey().equals(themesSetting)) {
			lpThemesSetting.setValue(newValue.toString());
			for (int i = 0; i < color.length; i++) {
				if (newValue.toString().equals(color[i])) {
					editor.putInt("theme", colorValue[i]);
					break;
				}
			}
		} else if (preference.getKey().equals(otherServer)) {
			etpOtherServer.setSummary(newValue.toString());
			editor.putString("other_server", newValue.toString());
		} else if (preference.getKey().equals(getResources().getString(R.string.time_depart))) {
			lpTimedepart.setValue(newValue.toString());
			//	lpTimedepart.setValue(preferences.getString("timedepart", "1"));
			editor.putString("timedepart", newValue.toString());
		}
		editor.commit();
		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		Editor editor = preferences.edit();

		if (preference.getKey().equals(getResources().getString(R.string.reset))) {
			lpServer.setValue("pool.ntp.org");
			lpOvertime.setValue("30");
			lpThemesSetting.setValue(color[0]);
			cbpAutoSync.setChecked(false);
			editor.putString("server", "pool.ntp.org");
			editor.putLong("overtime", 30);
			editor.putBoolean("auto_sync", false);
			editor.putInt("theme", colorValue[0]);
			editor.putBoolean("gps_mode", false);
		} else if (preference.getKey().equals(getResources().getString(R.string.about))) {
			new AlertDialog.Builder(SettingActivity.this).setTitle("关于")
					.setMessage(getResources().getString(R.string.about_info)).setPositiveButton("确定", null).show();
		} else {
			return false;
		}
		editor.commit();
		return true;
	}

}
