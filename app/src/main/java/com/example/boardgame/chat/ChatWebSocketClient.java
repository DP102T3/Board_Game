package com.example.boardgame.chat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Locale;

// WebSocket 的 Client端 設定
// 設置全域的廣播管理器，將 Server端 收到的訊息在需要時利用廣播傳出
public class ChatWebSocketClient extends WebSocketClient {
    private static final String TAG = "ChatWebSocketClient";
    private LocalBroadcastManager broadcastManager; // 建立廣播管理器，用於發送廣播
    private Gson gson;

    ChatWebSocketClient(URI serverURI, Context context) {
        super(serverURI, new Draft_17());   // Draft_17 是連接協議，就是標準的RFC 6455（JSR256）
        broadcastManager = LocalBroadcastManager.getInstance(context);  // 廣播管理器 實體化（使用 Context參數 作為全域廣播）
        gson = new Gson();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        // 紀錄Http狀態
        String text = String.format(Locale.getDefault(),
                "Http status code = %d, status message = %s",
                handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage());
        Log.d(TAG, "onOpen : " + text);
    }

    @Override
    public void onMessage(String jsonIn) {

        if (jsonIn != null) {
            String onMessage = "NewChatMsg";
            Intent intent = new Intent(onMessage);
            intent.putExtra("jsonIn", jsonIn);
            broadcastManager.sendBroadcast(intent);
            Log.d(TAG, "sendMessageBroadcast onMessage : " + onMessage);
        }else {
            Log.d(TAG, "No Any Message Received");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // 紀錄斷線時的參數值
        String text = String.format(Locale.getDefault(),
                "code = %d, reason = %s, remote = %b",
                code, reason, remote);
        Log.d(TAG, "onClose : " + text);
    }

    @Override
    public void onError(Exception e) {
        // 紀錄錯誤訊息
        Log.d(TAG, "onError: exception = " + e.toString());
    }
}
