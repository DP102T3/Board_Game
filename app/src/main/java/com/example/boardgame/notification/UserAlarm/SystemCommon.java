package com.example.boardgame.notification.UserAlarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class SystemCommon {
    private static final String TAG = "TAG_Common";
    public static final String PREFERENCES_NAME = "preferences";
    private Context context =getContext();
    private static Gson gson;
    private static int key;
    private static String keys;
    public static final String KEY_ALARM_TIME = "alarmTime";

    public static PendingIntent setAlarm(Context context, long time, boolean save, String title, String content) {
        if (save) {
            // 將alarm設定時間存入偏好設定檔，方便重開機後取得
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
            preferences.edit().putLong(KEY_ALARM_TIME, time).apply();
        }

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return null;
        }

        final int REQ_ALARM = 10;
        // 建立的Intent需指定廣播接收器以攔截AlarmManager發出的廣播
        Intent intentMeeting = new Intent(context, SystemNotificationReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("title",title );
        bundle.putString("content",content );
        intentMeeting.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                REQ_ALARM, intentMeeting, PendingIntent.FLAG_CANCEL_CURRENT);
        // 設定單次準時alarm，而且裝置在低電源、休眠時仍可執行
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent);

        String text = "Alarm scheduled at: " + getFormatTime(time);
        Log.e(TAG, text);
        return pendingIntent;
    }

    public static String getFormatTime(long time) {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        return format.format(new Date(time));
    }

    public static void savePlayer_id(Context context, String player_id) {
        SharedPreferences preferences =
                context.getSharedPreferences("user", MODE_PRIVATE);
        preferences.edit().putString("player_id", player_id).apply();
    }

    public static String loadPlayer_id(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences("user", MODE_PRIVATE);
        String player_id = preferences.getString("player_id", "");
        Log.d(TAG, "player_id = " + player_id);
        return player_id;
    }

    public Context getContext() {
        return context;
    }
}
