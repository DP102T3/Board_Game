package com.example.boardgame.notification.UserAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.example.boardgame.notification.UserAlarm.SystemCommon.KEY_ALARM_TIME;
import static com.example.boardgame.notification.UserAlarm.SystemCommon.PREFERENCES_NAME;

public class SystemBootReceiver extends BroadcastReceiver {
    private static final String TAG = "TAG_MyBootReceiver";
    private Gson gson = new Gson();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null || !action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            return;
        }
        SharedPreferences preferences =
                context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        // 如果之前沒有設定alarm，偏好設定檔內就不會有alarm時間，那就直接結束
        long alarmTime = preferences.getLong("time", 0);


        Bundle bundle = new Bundle();
        String title = bundle.getString("title");
        String content = bundle.getString("content");
        if (alarmTime == 0) {
            return;
        }

        // 取出alarm時間
        String alarmStr = "Alarm time in preferences: " + SystemCommon.getFormatTime(alarmTime);
        Log.d(TAG, alarmStr);

        long now = new Date().getTime();
        // 如果alarm沒有逾期就重設一次；逾期就從偏好設檔內移除設定時間
        if (alarmTime >= now) {
            SystemCommon.setAlarm(context, alarmTime, false, title, content);
        } else {
            preferences.edit().remove(KEY_ALARM_TIME).apply();
        }
    }


}