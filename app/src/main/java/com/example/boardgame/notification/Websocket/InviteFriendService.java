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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.notification.Common;
import com.example.boardgame.notification.CommonShop;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;


public class InviteFriendService extends Service {
    private Context context;
    private String title, content;
    private PowerManager.WakeLock wakeLock;
    private final String TAG = "WebSocketClient";
    public static InviteFriendWebSocketClient inviteFriendWebSocketClient;
    private static final String NOTIFICATION_CHANNEL_ID = "notification";
    private int NOTIFICATION_ID = 1;
    private Gson gson;
    public static String SERVER_URI =
            "ws://10.0.2.2:8080/BoardGame_Web/InviteFriendServer/";
    private static String player_id;
    private static int shop_id;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        player_id = Common.loadPlayer_id(context);
        shop_id = CommonShop.loadShop_id(context);
        connectServer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public class InviteFriendWebSocketClient extends WebSocketClient {
        InviteFriendWebSocketClient(URI serverURI) {
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
            Log.d("InviteFriendOnMessage: ",  message);
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
            if(!player_id.isEmpty()){
                uri = new URI(SERVER_URI + player_id);
            }else {
                uri = new URI(SERVER_URI + shop_id);
            }
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        if (inviteFriendWebSocketClient == null) {
            inviteFriendWebSocketClient = new InviteFriendWebSocketClient(uri);
            inviteFriendWebSocketClient.connect();
        }
    }

    // 中斷WebSocket連線
    public void disconnectServer() {
        if (inviteFriendWebSocketClient != null) {
            inviteFriendWebSocketClient.close();
            inviteFriendWebSocketClient = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectServer();
    }
}


