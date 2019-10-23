package com.example.boardgame.chat;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;

import static android.content.Context.MODE_PRIVATE;

// 存放 通用方法 的類別
public class Common {
    private static final String TAG = "Common";

    public static Bitmap bitmap;

    // 連線 Servlet 所需的 URI ，以取得玩家的參團列表
    public static String SERVLET_URI = "http://10.0.2.2:8080/DevBG//ChatServlet";


    // 連線 WebSocket 所需的 URI ，以發送即時的聊天內容
    public static final String SOCKET_URI = "ws://10.0.2.2:8080/DevBG/ChatSocket/";
    public static ChatWebSocketClient chatWebSocketClient;  // 利用此類別的設定，進一步與 WebSocket 的Server端 連線



    // 確認裝置的網路連線（於 Servlet連線 前呼叫此方法）
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }



    // 建立 WebSocket連線 的自定義方法
    public static void connectSocket(Context context, String playerId) {
        URI uri =null;
        try {
            uri = new URI(SOCKET_URI + playerId);
        }
        catch (URISyntaxException e){
            Log.e(TAG, e.toString());
        }

        // 以 ChatWebSocket 實體 呼叫 connect() 方法，以建立連線
        if(chatWebSocketClient == null){
            chatWebSocketClient = new ChatWebSocketClient(uri, context);
            chatWebSocketClient.connect();
        }
    }
    // 中斷 WebSocket連線 的自定義方法（釋放資源）
    public static void disConnectSocket() {
        if(chatWebSocketClient != null){
            chatWebSocketClient.close();
            chatWebSocketClient = null;
        }
    }



    // 自定義方法，將呼叫此方法的 playerId 存入偏好設定
    public static void savePlayerId(Context context, String playerId){
        SharedPreferences preferences =
                context.getSharedPreferences("playerId", MODE_PRIVATE );
        preferences.edit().putString("playerId", playerId).apply();
    }
    // 自定義方法，將 savePlayerId()方法 存入的 playerId 從偏好設定取出
    public static String loadPlayerId(Context context){
        SharedPreferences preferences =
                context.getSharedPreferences("playerId", MODE_PRIVATE);
        String playerId = preferences.getString("playerId", "");
        return playerId;
    }



    // Toast訊息 的方法（兩種簽章；使用CharSequence 或 resId）
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void showToast(Context context, int stringId){
        Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
    }
}
