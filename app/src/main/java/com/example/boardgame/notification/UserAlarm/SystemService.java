package com.example.boardgame.notification.UserAlarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class SystemService extends Service {
    private Context context;
    public static String title, content;
    private long setup_time;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private PowerManager.WakeLock wakeLock;
    private final String TAG = "WebSocketClient";
    public static InviteFriendWebSocketClient inviteFriendWebSocketClient;
    private Gson gson;
    public static String SERVER_URI =
            "ws://10.0.2.2:8080/BoardGame_Web/SystemNotificationServer/";

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        connectServer();

        alarmManager =
                (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        acquireWakeLock();
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
            Log.d("systemOnMessage: ", message);
            SystemNotification nosJson = gson.fromJson(message, SystemNotification.class);
            title = nosJson.getBnote_title();
            Log.e("title",title);
            content = nosJson.getBnote_content();
            Log.e("content",title);
            setup_time = nosJson.getSetup_time();

            if (!title.isEmpty() && !content.isEmpty() && setup_time!=0L) {
                pendingIntent = SystemCommon.setAlarm(context, setup_time, true,title,content);
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

    // 建立WebSocket連線
    public void connectServer() {
        URI uri = null;
        try {
            uri = new URI(SERVER_URI + SystemCommon.loadPlayer_id(context));
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
        releaseWakeLock();
    }

    private void acquireWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null && wakeLock == null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ServiceDemo:MyWakeLock");
            // 提供timeout時間以避免事情做完了還佔用著wakelock，一般設10分鐘
            //wakeLock.acquire(10 * 60 * 1000);
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
