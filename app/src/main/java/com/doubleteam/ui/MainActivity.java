package com.doubleteam.ui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.doubleteam.ntp.R;
import com.doubleteam.util.SpUtil;
import com.doubleteam.widget.ToggleNavigationItemDescriptor;

import net.xpece.material.navigationdrawer.NavigationDrawerUtils;
import net.xpece.material.navigationdrawer.descriptors.BaseNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationItemDescriptor;
import net.xpece.material.navigationdrawer.descriptors.NavigationSectionDescriptor;
import net.xpece.material.navigationdrawer.descriptors.SimpleNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.list.NavigationListFragmentCallbacks;
import net.xpece.material.navigationdrawer.list.SupportNavigationListFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationListFragmentCallbacks {

    private DrawerLayout mDrawerLayout;

    private View mContent;

    private ActionBarDrawerToggle mDrawerToggle;

    private SupportNavigationListFragment mNavFragment;

    private int mSelectedItem;

    private static String serverSetting;
    private static int overtimeSetting;
    private static boolean autoSync;
    private static String timeDepartSetting;
    private static String themesSetting;
    private static String otherServer;

    private static final List<NavigationSectionDescriptor> SECTIONS = new ArrayList<>();
    private static final NavigationSectionDescriptor PRIMARY_SECTION;
    private static final NavigationSectionDescriptor SECOND_SECTION;
    private static final NavigationSectionDescriptor PINNED_SECTION;
    private static ToggleNavigationItemDescriptor toggleNavigationItemDescriptor;

    static {
        PRIMARY_SECTION = new NavigationSectionDescriptor().heading("服务器设置")
                .addItem(new SimpleNavigationItemDescriptor(1).text("自定义NTP服务器").badge(serverSetting).sticky()
                        .iconResource(R.drawable.ic_star_black_24dp)
                        .activeColorResource(R.color.myPrimaryColor)
                        .badgeColorResource(R.color.myPrimaryDarkColor)
                        .activatedBackgroundResource(R.color.material_light_blue_100))
                .addItem(new SimpleNavigationItemDescriptor(2).text("超时设置").badge(overtimeSetting).sticky()
                        .iconResource(R.drawable.ic_star_black_24dp)
                        .activeColorResource(R.color.myPrimaryColor)
                        .activatedBackgroundResource(R.color.material_light_blue_100)
                        .badgeColorResource(R.color.myPrimaryColor));
        SECTIONS.add(PRIMARY_SECTION);

        if(autoSync){
            toggleNavigationItemDescriptor = new ToggleNavigationItemDescriptor(3).checked(true);
        }else{
            toggleNavigationItemDescriptor = new ToggleNavigationItemDescriptor(3).checked(false);
        }

        SECOND_SECTION = new NavigationSectionDescriptor().heading("其他设置")
                .addItem(toggleNavigationItemDescriptor)
                .addItem(new SimpleNavigationItemDescriptor(4).text("设置同步时间").badge(timeDepartSetting).sticky()
                .iconResource(R.drawable.ic_star_black_24dp)
                .activeColorResource(R.color.myPrimaryColor)
                .activatedBackgroundResource(R.color.material_light_blue_100)
                .badgeColorResource(R.color.myPrimaryColor));
        SECTIONS.add(SECOND_SECTION);

        PINNED_SECTION = new NavigationSectionDescriptor()
                .addItem(new BaseNavigationItemDescriptor(5).text("恢复最初设置")
                        .iconResource(R.drawable.ic_settings_black_24dp))
                .addItem(new BaseNavigationItemDescriptor(6).text("关于")
                        .iconResource(R.drawable.ic_help_black_24dp));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mContent = findViewById(R.id.content);
        initView();
        if (mDrawerLayout != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, android.R.string.untitled, android.R.string.untitled);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            NavigationDrawerUtils.fixMinDrawerMargin(mDrawerLayout);
            mDrawerLayout.setDrawerShadow(R.drawable.mnd_shadow_left, Gravity.RIGHT);
            mDrawerLayout.setDrawerShadow(R.drawable.mnd_shadow_right, Gravity.LEFT);
        }
        mNavFragment = (SupportNavigationListFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        if (mNavFragment != null) {
            NavigationDrawerUtils.setProperNavigationDrawerWidth(mNavFragment.getView());
            mNavFragment.setHeaderView(mNavFragment.getLayoutInflater2().inflate(R.layout.mnd_custom_header, null), true);
            mNavFragment.setSections(SECTIONS);
            mNavFragment.setPinnedSection(PINNED_SECTION);
            mNavFragment.setBackgroundResource(R.color.myAccentColor);
        }
        if (savedInstanceState == null) {
            mSelectedItem = 1;
        } else {
            mSelectedItem = savedInstanceState.getInt("mSelectedItem");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mDrawerToggle != null) mDrawerToggle.syncState();

        if (savedInstanceState == null) {
            if (mNavFragment != null) mNavFragment.setSelectedItem(mSelectedItem);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mDrawerToggle != null) mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("mSelectedItem", mSelectedItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (mDrawerToggle != null) return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationItemSelected(View view, int position, int id, NavigationItemDescriptor item) {
        if (item != null && item.isSticky()) {
            mSelectedItem = id;
            if (mNavFragment != null) mNavFragment.setSelectedItem(id);
        }
        Log.i("francis", "position------>" + position);
        Log.i("francis", "id------>" + id);
        switch (id){
            case 1:
                selectSettingServer(id);
                break;
            case 2:
                selectSettingOvertime(id);
                break;
            case 4:
                selectSettingTimeDepart();
                break;
            case 5:
                selectSettingResetSettings();
                break;
            case 6:
                selectSettingAbout();
                break;
        }

    }

    private void initView(){
        serverSetting = SpUtil.getInstance(this).getSettingServer();
        overtimeSetting = SpUtil.getInstance(this).getSettingsOvertime();
        autoSync = SpUtil.getInstance(this).getSettingsAutoSync();
        otherServer = SpUtil.getInstance(this).getSettingOtherServer();
        timeDepartSetting = SpUtil.getInstance(this).getSettingsTimedepart();
    }


    private void selectSettingServer(final int id){
        new MaterialDialog.Builder(this)
                .title(R.string.settings_server)
                .items(R.array.server_value)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        String[] serverArray = MainActivity.this.getResources().getStringArray(R.array.server_value);
                        SpUtil.getInstance(MainActivity.this).setSettingServer(serverArray[which]);
                        ((SimpleNavigationItemDescriptor) (PRIMARY_SECTION.get(id-1))).badge(serverArray[which]);
                        mNavFragment.notifyDataSetChanged();
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();
    }


    private void selectSettingOvertime(final int id){
        new MaterialDialog.Builder(this)
                .title(R.string.settings_overtime)
                .items(R.array.overtime_value)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        String[] serverArray = MainActivity.this.getResources().getStringArray(R.array.overtime_value);
                        SpUtil.getInstance(MainActivity.this).setSettingsOvertime(Integer.valueOf(serverArray[which]));
                        ((SimpleNavigationItemDescriptor) (PRIMARY_SECTION.get(id-1))).badge(serverArray[which]);
                        mNavFragment.notifyDataSetChanged();
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();
    }


    private void selectSettingTimeDepart(){
        new MaterialDialog.Builder(this)
                .title(R.string.settings_time_depart)
                .items(R.array.time_depart)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        String[] serverArray = MainActivity.this.getResources().getStringArray(R.array.time_depart);
                        SpUtil.getInstance(MainActivity.this).setSettingsOvertime(Integer.valueOf(serverArray[which]));
                        ((SimpleNavigationItemDescriptor) (SECOND_SECTION.get(0))).badge(serverArray[which]);
                        mNavFragment.notifyDataSetChanged();
                        return true;
                    }
                })
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .show();
    }


    private void selectSettingResetSettings(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("NTP服务器地址：");
        stringBuffer.append(SpUtil.getInstance(this).getSettingServer() + "\n");
        stringBuffer.append("超时设置：");
        stringBuffer.append(SpUtil.getInstance(this).getSettingsOvertime() + "\n");
        stringBuffer.append("自动同步：");
        if(SpUtil.getInstance(this).getSettingsAutoSync()){
            stringBuffer.append("是\n");
        }else{
            stringBuffer.append("否\n");
        }
        stringBuffer.append("时间间隔：");
        stringBuffer.append(SpUtil.getInstance(this).getSettingsTimedepart() + "\n");
        new MaterialDialog.Builder(this)
                .title(R.string.settings_reset)
                .content(stringBuffer.toString())
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .icon(getResources().getDrawable(R.drawable.icon_app))
                .show();
    }

    private void selectSettingAbout(){
        new MaterialDialog.Builder(this)
                .title(R.string.settings_about)
                .content("本软件中国科学院国家授时中心版权所有!")
                .positiveText(R.string.choose)
                .negativeText(R.string.cancel)
                .icon(getResources().getDrawable(R.drawable.icon_app))
                .show();
    }

}
