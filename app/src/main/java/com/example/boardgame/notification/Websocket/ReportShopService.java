package com.example.boardgame.notification.Websocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.boardgame.notification.Common;
import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class ReportShopService extends Service {
    private Context context;
    private String title, content;
    private PowerManager.WakeLock wakeLock;
    private final String TAG = "ReportShopService";
    public static ReportShopWebSocketClient reportShopWebSocketClient;
    public final String NOTIFICATION_CHANNEL_ID = "notification";
    private int NOTIFICATION_ID = 1;
    private Gson gson;
    public String SERVER_URI =
            "ws://10.0.2.2:8080/BoardGame_Web/ReportShopServer/";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        connectServer();
        acquireWakeLock();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public class ReportShopWebSocketClient extends WebSocketClient {
        ReportShopWebSocketClient(URI serverURI) {
            // Draft_17是連接協議，就是標準的RFC 6455（JSR256）
            super(serverURI, new Draft_17());
            gson = new Gson();
        }

        @Override
        public void onOpen(ServerHandshake handshakeData) {
            String text = String.format(Locale.getDefault(),
                    "onOpen: Http status code = %d; status message = %s",
                    handshakeData.getHttpStatus(),
                    handshakeData.getHttpStatusMessage());
            Log.d(TAG, "onOpen: " + text);
        }

        @Override
        public void onMessage(String message) {
            Log.d(TAG, "onMessage: " + message);
            JsonObject nosJson = gson.fromJson(message, JsonObject.class);
            title = nosJson.get("title").getAsString();
            content = nosJson.get("content").getAsString();
            if (!title.isEmpty() && !content.isEmpty()) {
                sendNotification();
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            String text = String.format(Locale.getDefault(),
                    "code = %d, reason = %s, remote = %b",
                    code, reason, remote);
            Log.d(TAG, "onClose: " + text);
        }

        @Override
        public void onError(Exception ex) {
            Log.d(TAG, "onError: exception = " + ex.toString());
        }
    }

    private void sendNotification() {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "addfriend",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent nosIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, nosIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.bglogo))
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
        NOTIFICATION_ID++;
    }


    // 建立WebSocket連線
    public void connectServer() {
        URI uri = null;
        try {
            uri = new URI(SERVER_URI + Common.loadPlayer_id(context));
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        if (reportShopWebSocketClient == null) {
            reportShopWebSocketClient = new ReportShopWebSocketClient(uri);
            reportShopWebSocketClient.connect();
        }
    }

    // 中斷WebSocket連線
    public void disconnectServer() {
        if (reportShopWebSocketClient != null) {
            reportShopWebSocketClient.close();
            reportShopWebSocketClient = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectServer();
        releaseWakeLock();
    }

    private void acquireWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null && wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ServiceDemo:MyWakeLock");
            // 提供timeout時間以避免事情做完了還佔用著wakelock，一般設10分鐘
            wakeLock.acquire(10 * 60 * 1000);
            Log.d(TAG, "acquireWakeLock");
        }
    }

    // 釋放wake lock
    private void releaseWakeLock() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
            Log.d(TAG, "releaseWakeLock");
        }
    }
}



