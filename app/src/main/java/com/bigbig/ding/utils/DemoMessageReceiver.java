package com.bigbig.ding.utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.bigbig.ding.R;
import com.bigbig.ding.bean.NoteFolder;
import com.bigbig.ding.constants.Constans;
import com.blankj.utilcode.util.TimeUtils;
//import com.xiaomi.mipush.sdk.Constants;
import com.blankj.utilcode.util.Utils;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.bigbig.ding.MainApplication;
import com.bigbig.ding.bean.Note;
import com.bigbig.ding.module.notes.main.NoteMainActivity;

import org.litepal.crud.DataSupport;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 DemoMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="com.xiaomi.mipushdemo.DemoMessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、DemoMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、DemoMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、DemoMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、DemoMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、DemoMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 *
 * @author mayixiang
 */
public class DemoMessageReceiver extends PushMessageReceiver {

    public   static String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;
    long years = (long)12 * 30 * 24 * 60 * 60 * 1000;
    long month =(long) 24 * 60 * 60 * 1000 * 30;
    long days = (long)24 * 60 * 60 * 1000;
    long m=(long)60*1000;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.v(MainApplication.TAG,
                "onReceivePassThroughMessage is called. " + message.toString());
        String log = context.getString(R.string.recv_passthrough_message, message.getContent());
//        MainActivity.logList.add(0, getSimpleDate() + " " + log);
        //MainActivity.mLogView.setText("状态记录:\n\n"+message.getContent());
        //保存到 Sqlite 数据库
        Note note2 = new Note();
        long time = TimeUtils.getNowMills();
        note2.setCreatedTime(time-m-m-m-m);
        note2.setModifiedTime(time-m-m-m-m);
        note2.setNoteFolderId(1);
        note2.setNoteContent(message.getExtra().get("md").replace("|||","\r\n"));
        note2.setIsPrivacy(0);
        note2.setInRecycleBin(0);
        note2.setNoteId(UUID.randomUUID().toString());
        note2.save();
        Log.v(MainApplication.TAG,
                "note message  is . " + note2.toString());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

//        Message msg = Message.obtain();
//        msg.obj = log;
//        MyApplication.getHandler().sendMessage(msg);
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {

        /*
        此处是在任务栏点击推送消息后的通知
        * */
        Log.v(MainApplication.TAG,
                "onNotificationMessageArrived is called. " + message.toString());
        //String log = context.getString(R.string.arrive_notification_message)+message.getContent();
        String log = "消息为:"+message.getDescription();
//        getNoteCount
        NoteFolder folder= DataSupport.where("folderName = ? ","随手记").find(NoteFolder.class).get(0);
        int folderid =folder.getId();
        int noteCount=folder.getNoteCount();

        Note note2 = new Note();
        long time = TimeUtils.getNowMills();
        note2.setCreatedTime(time-m-m-m-m);
        note2.setModifiedTime(time-m-m-m-m);
        note2.setNoteFolderId(folderid);
        note2.setNoteContent(message.getExtra().get("md").replace("|||","\r\n"));
        note2.setIsPrivacy(0);
        note2.setInRecycleBin(0);
        note2.setNoteId(UUID.randomUUID().toString());
        note2.save();
        noteCount+=1;
        Log.v(MainApplication.TAG,
                "nnoteCount is . " + noteCount);
        folder.setNoteCount(noteCount);
        folder.save();
        Log.v(MainApplication.TAG,
                "note message  is . " + note2.getNoteContent());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

//        Message msg = Message.obtain();
//        msg.obj = log;
//        MyApplication.getHandler().sendMessage(msg);
        Intent intent =new Intent(context, NoteMainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.v(MainApplication.TAG,
                "onNotificationMessageArrived is called. " + message.toString());
        //String log = context.getString(R.string.arrive_notification_message)+message.getContent();
        String log = "消息为:"+message.getDescription();
        //保存到 Sqlite 数据库
//        Note note2 = new Note();
//        long time = TimeUtils.getNowMills();
//        note2.setCreatedTime(time-m-m-m-m);
//        note2.setModifiedTime(time-m-m-m-m);
//        note2.setNoteFolderId(1);
//        note2.setNoteContent(message.getExtra().get("md").replace("|||","\r\n"));
//        note2.setIsPrivacy(0);
//        note2.setInRecycleBin(0);
//        note2.setNoteId(UUID.randomUUID().toString());
//        note2.save();
//        Log.v(MainApplication.TAG,
//                "note message  is . " + note2.getNoteFolderId());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

//        Message msg = Message.obtain();
//        msg.obj = log;
//        MyApplication.getHandler().sendMessage(msg);
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.v(MainApplication.TAG,
                "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;

                log = context.getString(R.string.register_success);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.set_alias_success, mAlias);
            } else {
                log = context.getString(R.string.set_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.unset_alias_success, mAlias);
            } else {
                log = context.getString(R.string.unset_alias_fail, message.getReason());
            }
        }
        //此处是注册成功后的标志
        else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                log = context.getString(R.string.set_account_success, mAccount);
            } else {
                log = context.getString(R.string.set_account_fail, message.getReason());
            }
        }
        else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                log = context.getString(R.string.unset_account_success, mAccount);
            } else {
                log = context.getString(R.string.unset_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.subscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.subscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.unsubscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.unsubscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
                log = context.getString(R.string.set_accept_time_success, mStartTime, mEndTime);
            } else {
                log = context.getString(R.string.set_accept_time_fail, message.getReason());
            }
        } else {
            log = message.getReason();
        }
//        MainActivity.logList.add(0, getSimpleDate() + "    " + log);

//        Message msg = Message.obtain();
//        msg.obj = log;
//        MainApplication.getHandler().sendMessage(msg);
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.v(MainApplication.TAG,
                "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String log;
        Note note;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                PreferencesUtil.saveString(Constans.mRegId,mRegId);
                Constans.xmRegId=PreferencesUtil.getString(Constans.mRegId,"");
                NoteFolder folder= DataSupport.where("folderName = ? ","随手记").find(NoteFolder.class).get(0);
                int folderid =folder.getId();
                int noteCount=folder.getNoteCount();
                //更新到笔记
                List<Note> notelist= DataSupport.where("isPrivacy=0 and inRecycleBin=0 and noteContent like ? ","推送网址%").find(Note.class);
                if (notelist.size() <1){
                    note=new Note();
                    long time = TimeUtils.getNowMills();
                    note.setCreatedTime(time-m-m);
                    note.setModifiedTime(time -m-m);
                    note.setNoteFolderId(folderid);
                    note.setIsPrivacy(0);
                    note.setInRecycleBin(0);
                    note.setNoteId(UUID.randomUUID().toString());
                    noteCount+=1;
                    folder.setNoteCount(noteCount);
                    folder.save();
                }
                else {
                    note=notelist.get(0);
                }
                String newContent=String.format(Utils.getContext().getResources().getString(R.string.database_content_five),mRegId);
                Log.v(MainApplication.TAG,newContent);
                note.setNoteContent(newContent);
                note.save();

                log = context.getString(R.string.register_success)+mRegId;
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else {
            log = message.getReason();
        }
        Log.d(MainApplication.TAG, log);
    }

    @Override
    public void onRequirePermissions(Context context, String[] permissions) {
        super.onRequirePermissions(context, permissions);
        Log.e(MainApplication.TAG,
                "onRequirePermissions is called. need permission" + arrayToString(permissions));

        if (Build.VERSION.SDK_INT >= 23 && context.getApplicationInfo().targetSdkVersion >= 23) {
            Intent intent = new Intent();
            intent.putExtra("permissions", permissions);
            intent.setComponent(new ComponentName(context.getPackageName(), PermissionActivity.class.getCanonicalName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }


    public String arrayToString(String[] strings) {
        String result = " ";
        for (String str : strings) {
            result = result + str + " ";
        }
        return result;
    }
}
