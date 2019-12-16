package com.example.boardgame.group;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Common {
    public static Bitmap bitmap;
    public static String URL_SERVER = "http://10.0.2.2:8080/BG_May/";
    //public static String playerId="chengchi1223";
    public static String playerId="abcd123";
    public static double rate_total=100;
    public static int rate_count=25;
    public static int shopId=11111;



    public static boolean networkConnected(Activity activity) {
        ConnectivityManager conManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager != null ? conManager.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();

    }

    public static void showToast(Context context, int messageResId) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show();
    }
}
