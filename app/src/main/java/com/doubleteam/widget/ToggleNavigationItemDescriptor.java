package com.doubleteam.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.doubleteam.ntp.R;
import com.doubleteam.util.SpUtil;

import net.xpece.material.navigationdrawer.descriptors.AbsNavigationItemDescriptor;
import net.xpece.material.navigationdrawer.internal.ViewHolder;

/**
 * Created by francis
 */
public class ToggleNavigationItemDescriptor extends AbsNavigationItemDescriptor {

    private boolean mChecked = false;

    public ToggleNavigationItemDescriptor(int id) {
        super(id);
    }

    public ToggleNavigationItemDescriptor checked(boolean checked) {
        this.mChecked = checked;
        return this;
    }

    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public int getLayoutId() {
        return R.layout.mnd_custom_item_toggle;
    }

    @Override
    public void bindView(final View view, final boolean selected) {
        super.bindView(view, false);

        setup(view);
    }

    @Override
    public boolean onClick(View view) {
        updateToggle(view, !mChecked);
        return true;
    }

    private void updateToggle(View view, boolean checked) {
        SwitchCompat toggle = ViewHolder.get(view, R.id.toggle);
        if (toggle.isChecked() != checked) {
            toggle.toggle();
        }
    }

    private void setup(final View view) {
        SwitchCompat toggle = ViewHolder.get(view, R.id.toggle);
        toggle.setOnCheckedChangeListener(null);
        mChecked = SpUtil.getInstance(view.getContext()).getSettingsAutoSync();
        toggle.setChecked(mChecked);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mChecked = !mChecked;
                updateText(view);

                onChange(view.getContext(), mChecked);
            }
        });

        updateText(view);
    }

    private void updateText(View view) {
        Context context = view.getContext();
        TextView text = ViewHolder.get(view, R.id.text);
        if (mChecked) {
            text.setTextColor(getColor(context, R.attr.colorAccent, Color.BLACK));
        } else {
            text.setTextColor(getColor(context, android.R.attr.textColorPrimaryNoDisable, Color.BLACK));
        }
        text.setText(mChecked ? "自动同步" : "非自动同步");
    }

    public static int getColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        try {
            return ta.getColor(0, fallback);
        } finally {
            ta.recycle();
        }
    }

    public void onChange(Context context, boolean checked) {
        doWork(context,checked);
    }

    @TargetApi(12)
    private void doWork(Context context,boolean mChecked) {
        SpUtil.getInstance(context).setSettingsAutoSync(mChecked);
    }
}
