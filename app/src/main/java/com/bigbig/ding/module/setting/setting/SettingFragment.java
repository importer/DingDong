package com.bigbig.ding.module.setting.setting;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.bigbig.ding.R;

import com.bigbig.ding.constants.Constans;
import com.bigbig.ding.module.setting.main.SettingMainActivity;
import com.bigbig.ding.utils.PreferencesUtil;
import com.bigbig.ding.utils.ThemeUtils;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/06/28
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SettingMainActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        mActivity = (SettingMainActivity) getActivity();
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            case Constans.THEME:
                changeTheme();
                break;
            case Constans.IS_USE_RECYCLE:
                getIsUseRecycle();
                break;
        }
    }

    private void changeTheme(){
        int newTheme = PreferencesUtil.getInt(Constans.THEME,Constans.theme);
        if (newTheme != Constans.theme && mActivity != null) {
            Constans.theme=newTheme;
            mActivity.setTheme(newTheme);
            ThemeUtils.resetToolbarColor(mActivity);
            ThemeUtils.resetWindowStatusBarColor(mActivity);
            this.onCreate(null);
        }
    }

    private void getIsUseRecycle(){
        Constans.isUseRecycleBin=PreferencesUtil.getBoolean(Constans.IS_USE_RECYCLE,true);
    }
}