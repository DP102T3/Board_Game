package com.example.boardgame.notification.SystemNotification;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class Common {
    private static final String TAG ="SystemCommon";
    public static String URL_SERVER = "http://10.0.2.2:8080/BoardGame_Web/";

    // check if the device connect to the network
    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }

    public static void savePlayer_id(Context context, String player_id) {
        SharedPreferences preferences =
                context.getSharedPreferences("player_id", MODE_PRIVATE);
        preferences.edit().putString("player_id", player_id).apply();
    }

    public static String loadPlayer_id(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences("player_id", MODE_PRIVATE);
        String player_id = preferences.getString("player_id", "");
        Log.d(TAG, "player_id = " + player_id);
        return player_id;
    }
}
