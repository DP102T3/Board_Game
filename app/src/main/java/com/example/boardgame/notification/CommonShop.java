package com.example.boardgame.notification;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


public class CommonShop {
    private static final String TAG ="CommonShop";
    public static String URL_SERVER = "http://10.0.2.2:8080/BoardGame_Web/";

    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void saveShop_id(Context context, int shop_id) {
        SharedPreferences preferences =
                context.getSharedPreferences("user", MODE_PRIVATE);
        preferences.edit().putInt("shop_id", shop_id).apply();
    }

    public static int loadPlayer_id(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences("user", MODE_PRIVATE);
        int shop_id = preferences.getInt("shop_id", 0);
        Log.d(TAG, "shop_id = " + shop_id);
        return shop_id;
    }

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }
}
