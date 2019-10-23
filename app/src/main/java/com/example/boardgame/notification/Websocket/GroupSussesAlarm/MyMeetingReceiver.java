package com.example.boardgame.notification.Websocket.GroupSussesAlarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.boardgame.MainActivity;
import com.example.boardgame.notification.Common;
import com.example.boardgame.R;
import com.google.gson.JsonObject;

import static android.content.Context.NOTIFICATION_SERVICE;


public class MyMeetingReceiver extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "activityTime";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Common.networkConnected(context)) {
            String url = Common.URL_SERVER + "BoardGameServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "group_np");
            try {
                String result = new com.example.boardgame.notification
                        .CommonTask(url, jsonObject.toString()).execute().get();

            } catch (Exception e) {
                Log.e("GroupSussesAlarm", e.toString());
            }
        }
            sendNotification(context);
    }

    private void sendNotification(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 重要性越高，提示(打擾)user方式就越明確，設為IMPORTANCE_HIGH會懸浮通知並發出聲音
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "activityTime",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        String title = "揪團成團通知";
        String content = "請至個人資料查看確認";
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.bglogo))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}