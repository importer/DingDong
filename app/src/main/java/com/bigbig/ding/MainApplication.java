package com.bigbig.ding;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.blankj.utilcode.util.Utils;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;

import java.util.List;

import com.bigbig.ding.constants.CacheManager;
import com.bigbig.ding.constants.Constans;
import com.bigbig.ding.constants.FolderListConstans;
import com.bigbig.ding.constants.NoteListConstans;
import com.bigbig.ding.utils.PreferencesUtil;
import cn.bmob.v3.Bmob;

/**
 * <pre>
 *     author : FaDai
 *     e-mail : i_fadai@163.com
 *     time   : 2017/06/02
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */

public class MainApplication extends LitePalApplication {

    public static Context mContext;
    public static String TAG = "com.bigbig.ding";
    //小米消息推送APP_ID ，APP_KEY
    private static final String APP_ID = "2882303761518023989";
    private static final String APP_KEY = "5731802361989";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        init();
        initBmob();
        initXiaomiPush();
        getCacheData();
        setUpdateForVersionCode1();
    }

    private void init(){
        Utils.init(getApplicationContext());
        Connector.getDatabase();
}

    private void initBmob(){
        Bmob.initialize(this,getResources().getString(R.string.bmob_app_id));
    }

    private void getCacheData(){
        Constans.isFirst= PreferencesUtil.getBoolean(Constans.IS_FIRST,true);
        Constans.currentFolder= PreferencesUtil.getInt(Constans.CURRENT_FOLDER, FolderListConstans.ITEM_ALL);
        Constans.noteListShowMode=PreferencesUtil.getInt(Constans.NOTE_LIST_SHOW_MODE, NoteListConstans.STYLE_GRID);
        Constans.theme=PreferencesUtil.getInt(Constans.THEME,Constans.theme);
        Constans.isUseRecycleBin=PreferencesUtil.getBoolean(Constans.IS_USE_RECYCLE,Constans.isUseRecycleBin);
        Constans.isLocked=PreferencesUtil.getBoolean(Constans.IS_LOCKED,Constans.isLocked);
        Constans.lockPassword=PreferencesUtil.getString(Constans.LOCK_PASSWORD,"");
        Constans.xmRegId=PreferencesUtil.getString(Constans.mRegId,"");
    }
    /**
     * 初始化小米消息推送
     **/
    private void initXiaomiPush() {
        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }


    public static void reInitPush(Context ctx) {
        MiPushClient.registerPush(ctx.getApplicationContext(), APP_ID, APP_KEY);
    }
    // 为了兼容1.0.1版本，将其的缓存信息进行备份修改
    private void setUpdateForVersionCode1(){
        // 1.0.1版本使用的key值，如果是false，说明之前是V1.0.1版本
        boolean isFirst= PreferencesUtil.getBoolean("isFirst",true);
        if(!isFirst){
            boolean isGrid= PreferencesUtil.getBoolean("is_grid",false);
            boolean isUseRecycleBin=PreferencesUtil.getBoolean("recycle_bin",false);

            CacheManager.setAndSaveIsFirst(false);
            CacheManager.setAndSaveCurrentFolder(FolderListConstans.ITEM_ALL);
            CacheManager.setAndSaveIsUseRecycleBin(isUseRecycleBin);
            if(isGrid){
                CacheManager.setAndSaveNoteListShowMode(NoteListConstans.MODE_GRID);
            } else {
                CacheManager.setAndSaveNoteListShowMode(NoteListConstans.MODE_LIST);
            }
            // isLock、lockPassword key值一样；主题key值不用修改。

        }
    }
}
