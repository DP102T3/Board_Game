package com.example.boardgame;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.boardgame.Notification.Websocket.NetWorkService;
import com.example.boardgame.notification.CommonShop;
import com.example.boardgame.notification.CommonTask;
import com.example.boardgame.notification.ShopNotification;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "TAG_MainActivity";
    public static final int ONLY_BOTTOM = 0;
    public static final int BOTH_TAB_AND_BOTTOM = 1;
    public static final int NEITHER_TAB_AND_BOTTOM = 2;

    private static BottomNavigationView tabNavigationView;
    private static BottomNavigationView bottomNavigationView;

    private int width;
    int height;

    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;
    public static int shop_id=1;
    private static final String NOTIFICATION_CHANNEL_ID = "notification";
    private static int NOTIFICATION_ID = 1;
    public static String title, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 設置 TabBar
        tabNavigationView = findViewById(R.id.tabNavigation);
        tabNavigationView.setVisibility(View.GONE);
        NavController tabNavController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(tabNavigationView, tabNavController);

        // 設置 BottomBar
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        NavController bottomNavController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, bottomNavController);


        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        height = metric.heightPixels;

        //開啟網路偵測服務及Websocket服務(notification)
        Intent networkIntent = new Intent(this, NetWorkService.class);
        this.startService(networkIntent);


        //透過networkCallback回調網路連線狀態,包括行動網路及wifi皆會回調
        networkCallback = new NetworkCallbackImpl();
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest request = builder.build();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }





    // 變更 TabBar 或 BottomBar 的狀態
    public static void changeBarsStatus(int barsStatus) {
        switch (barsStatus) {
            case ONLY_BOTTOM:   // 只有 BottomVar
                tabNavigationView.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                break;
            case BOTH_TAB_AND_BOTTOM:   // 兩個都有
                tabNavigationView.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.VISIBLE);
                break;
            case NEITHER_TAB_AND_BOTTOM:    // 兩個都沒有
                tabNavigationView.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.GONE);
                break;
        }
    }

    //    –––––––––––––––––––––––––––––｜點擊空白處隱藏鍵盤｜–––––––––––––––––––––––––––––
    // 獲取點擊事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 當螢幕被觸碰
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            // 呼叫 isHideInput()方法 判斷是否要隱藏
            if (isHideInput(view, event)) {
                try {
                    // 隱藏鍵盤
                    HideSoftInput(view.getWindowToken());
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // 判斷是否要隱藏
    private boolean isHideInput(View view, MotionEvent event) {
        // 若當前 Focus 在 EditText 上
        if (view instanceof EditText) {
            // 初始位置（螢幕左上角）座標為[0, 0]
            int[] location = {0, 0};
            Log.d(TAG, "location = [" + location[0] + ", " + location[1] + "]");

            // 將 view 的位置存入 location[]  （其中 view = getCurrentFocus();）
            view.getLocationInWindow(location);
            Log.d(TAG, "After do (view.getLocationInWindow(location);), location = [" + location[0] + ", " + location[1] + "]");

            // 設定 view元件的 [left, right] = [x起點, x終點]（橫向方向）
            int left = 0, right = width;
            // 設定 view元件的 [top, bottom] = [y起點, y終點]（縱向方向）
            int top = location[1], bottom = top + view.getHeight();
            Log.d(TAG, "view.getWidth() = " + view.getWidth() + ", view.getHeight() = " + view.getHeight());

            if (event.getX() >= left && event.getX() <= right && event.getY() >= top
                    && event.getY() <= bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    // 隱藏鍵盤
    private void HideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (manager != null) {
                manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
//    ––––––––––––––––––––––––––––––｜點擊空白處隱藏鍵盤｜––––––––––––––––––––––––––––––

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.bggear){
//            Intent intent = new Intent(getActivity(),
//                    setupFragment.class);
//            startActivity(intent);
//        }
//
//        return true;

        switch (item.getItemId()) {
            case R.id.bggear:
//                Intent intent = new Intent(getActivity(),
//                        setupFragment.class);
                Navigation.findNavController(this, R.id.fragment).navigate(R.id.action_shop_infoFragment_to_setupFragment);
//                startActivity(intent);
                break;
        }
        return true;
    }


    //透過繼承ConnectivityManager實作各種回調狀態
    private class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {//當客戶連線時將未上線而沒收到的通知發給客戶
            super.onAvailable(network);
            Log.e("networkTest", "onAvailable");
            List<ShopNotification> notifications = getNotifications();

            if(notifications != null) {
                for (ShopNotification nos : notifications) {
                    title = nos.getSnote_title();
                    content = nos.getSnote_info();
                    sendNotification();
                    Log.e("MainActivity-title&content:", title + "," + content);
                }
                updateNosState();
            }else{
                Log.d(TAG, "User offline");
            }
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
            Log.e("networkTest", "onLosing");
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.e("networkTest", "onLost");
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            Log.e("networkTest", "onCapabilitiesChanged");

        }

        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
            Log.e("networkTest", "onLinkPropertiesChanged");
        }
    }

    //取得user因未連線而沒收到的所有通知
    private List<ShopNotification> getNotifications() {
        List<ShopNotification> notifications = null;
        if (CommonShop.networkConnected(this)) {
            String url = CommonShop.URL_SERVER + "ShopServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getUnnotifiedNos");
            jsonObject.addProperty("shop_id",shop_id );
            String jsonOut = jsonObject.toString();
            CommonTask getNosTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getNosTask.execute().get();
                Type listType = new TypeToken<List<ShopNotification>>() {
                }.getType();
                notifications = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            CommonShop.showToast(this, R.string.textNoNetwork);
        }
        return notifications;
    }

    //將DB中notification_player的pnote_state通知狀態由0(未通知)改為1(已通知)
    private void updateNosState() {
        int count = 0;
        if (CommonShop.networkConnected(this)) {
            String url = CommonShop.URL_SERVER + "ShopServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "updateNosState");
            jsonObject.addProperty("shop_id",shop_id);

            try {
                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                count = Integer.valueOf(result);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                Log.e("MainActivity-updateNosState：", "更新通知狀態失敗/無資料更新");
            } else {
                Log.e("MainActivity-updateNosState：", "更新通知狀態成功");
            }
        } else {
            Log.e("MainActivity-updateNosState：", "連線server失敗");
        }
    }

    //client發送通知器
    private void sendNotification() {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "UnnotifiedNos",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent nosIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, nosIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        android.app.Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.bglogo))
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
        NOTIFICATION_ID++;  //可能會有多則通知同時發送,以此設置不同id才能發送
    }
}
