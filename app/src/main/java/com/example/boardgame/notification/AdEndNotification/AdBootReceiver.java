package com.example.boardgame.notification.AdEndNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.example.boardgame.notification.AdEndNotification.AdCommon.KEY_ALARM_TIME;
import static com.example.boardgame.notification.AdEndNotification.AdCommon.PREFERENCES_NAME;

public class AdBootReceiver extends BroadcastReceiver {
    private static final String TAG = "TAG_MyBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null || !action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            return;
        }

        SharedPreferences preferences =
                context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        // 如果之前沒有設定alarm，偏好設定檔內就不會有alarm時間，那就直接結束
        long alarmTime = preferences.getLong("alarmTime", 0);
        if (alarmTime == 0) {
            return;
        }

        // 取出alarm時間
        String alarmStr = "Alarm time in preferences: " + AdCommon.getFormatTime(alarmTime);
        Log.d(TAG, alarmStr);

        long now = new Date().getTime();
        // 如果alarm沒有逾期就重設一次；逾期就從偏好設檔內移除設定時間
        if (alarmTime >= now) {
            AdCommon.setAlarm(context, alarmTime, false);
        } else {
            preferences.edit().remove(KEY_ALARM_TIME).apply();
        }
    }


}