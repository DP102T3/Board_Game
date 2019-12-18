package com.example.boardgame.notification.Websocket;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.boardgame.MainActivity;
import com.example.boardgame.notification.Common;
import com.example.boardgame.notification.CommonShop;
import com.example.boardgame.notification.CommonTask;
import com.example.boardgame.notification.ShopNotification;
import com.example.boardgame.R;

import com.example.boardgame.notification.PlayerNotificationList.Notification;
import com.example.boardgame.notification.UserAlarm.SystemService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class NetWorkService extends Service {
    public Context context;
    private static final String TAG = "NetWorkService";
    private PowerManager.WakeLock wakeLock;
    private ConnectivityManager.NetworkCallback networkCallback;
    private ConnectivityManager connectivityManager;
    public CommonTask getNosTask;
    private static final String NOTIFICATION_CHANNEL_ID = "notification";
    private static int NOTIFICATION_ID = 1;
    public static String GROUP_KEY = "group_key";
    private static String title, content;
    private static String player_id;
    private static int shop_id;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        player_id = Common.loadPlayer_id(context);
        shop_id = CommonShop.loadShop_id(context);


        //透過networkCallback回調網路連線狀態,包括行動網路及wifi皆會回調
        networkCallback = new NetworkCallbackImpl();
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest request = builder.build();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(request, networkCallback);

        //開啟邀請好友通知service(websocket)
        Intent inviteFriendIntent = new Intent(this, InviteFriendService.class);
        this.startService(inviteFriendIntent);
        //開啟好友加入通知service(websocket)
        Intent addFriendIntent = new Intent(this, AddFriendService.class);
        this.startService(addFriendIntent);
        //開啟檢舉玩家通知service(websocket)
        Intent reportPlayerIntent = new Intent(this, ReportPlayerService.class);
        this.startService(reportPlayerIntent);
        //開啟檢舉店家通知service(websocket)
        Intent reportShopIntent = new Intent(this, ReportShopService.class);
        this.startService(reportShopIntent);
        //開啟檢舉團通知service(websocket)
        Intent groupcheckIntent = new Intent(this, GroupCheckService.class);
        this.startService(groupcheckIntent);
        /* 開啟檢舉團通知service(websocket) */
        Intent reportGroupIntent = new Intent(this, ReportGroupService.class);
        this.startService(reportGroupIntent);
        //開起廣告通知service(websocket)
        Intent adIntent = new Intent(this, AdvertisementService.class);
        this.startService(adIntent);
        //開啟系統推播service(websocket)
        Intent systemIntent = new Intent(this, SystemService.class);
        this.startService(systemIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }


    //透過繼承ConnectivityManager實作各種回調狀態
    private class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {//當客戶連線時將未上線而沒收到的通知發給客戶
            super.onAvailable(network);
            Log.e("networkTest", "onAvailable");

            List<Notification> notifications = getPlayerNotification();
            if (player_id != null) {
                if (notifications != null) {
                    for (Notification nos : notifications) {
                        title = nos.getPnote_title();
                        content = nos.getPnote_info();
                        sendNotification();
                        Log.e("FriendMainActivity-title&content:", title + "," + content);
                    }
                    updatePlayerNosState();
                }
            } else {
                Log.d(TAG, "登入者非玩家");
            }
            if (shop_id != 0) {
                List<ShopNotification> shopnotifications = getShopNotifications();
                if (shopnotifications != null) {
                    for (ShopNotification nos : shopnotifications) {
                        title = nos.getSnote_title();
                        content = nos.getSnote_info();
                        sendNotification();
                        Log.e("FriendMainActivity-title&content:", title + "," + content);
                    }
                    updateShopNosStates();
                }
            } else {
                Log.d(TAG, "登入者非店家");
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

    //取得player因未連線而沒收到的所有通知
    private List<Notification> getPlayerNotification() {
        List<Notification> notifications = null;
        if (Common.networkConnected(this)) {
            String url = Common.URL_SERVER + "BoardGameServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getUnnotifiedNos");
            jsonObject.addProperty("player_id", player_id);
            String jsonOut = jsonObject.toString();
            getNosTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getNosTask.execute().get();
                Type listType = new TypeToken<List<Notification>>() {
                }.getType();
                notifications = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(this, R.string.textNoNetwork);
        }
        return notifications;
    }

    //將DB中notification_player的pnote_state通知狀態由0(未通知)改為1(已通知)
    private void updatePlayerNosState() {
        int playerCount = 0;
        if (Common.networkConnected(this)) {
            String url = Common.URL_SERVER + "BoardGameServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "updatePlayerNosStates");
            jsonObject.addProperty("player_id", player_id);

            try {
                String playerResult = new CommonTask(url, jsonObject.toString()).execute().get();
                playerCount = Integer.valueOf(playerResult);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (playerCount == 0) {
                Log.e("FriendMainActivity-updatePlayerNosState：", "更新通知狀態失敗/無資料更新");
            } else {
                Log.e("FriendMainActivity-updatePlayerNosState：", "更新通知狀態成功");
            }
        } else {
            Log.e("FriendMainActivity-updatePlayerNosState：", "連線server失敗");
        }
    }

    //取得shop因未連線而沒收到的所有通知
    private List<ShopNotification> getShopNotifications() {
        List<ShopNotification> notifications = null;
        if (Common.networkConnected(this)) {
            String url = com.example.boardgame.notification.CommonShop.URL_SERVER + "ShopServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getUnnotifiedNos");
            jsonObject.addProperty("shop_id", shop_id);
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
            com.example.boardgame.notification.CommonShop.showToast(this, R.string.textNoNetwork);
        }
        return notifications;
    }

    //將DB中notification_shop的snote_state通知狀態由0(未通知)改為1(已通知)
    private void updateShopNosStates() {
        int shopCount = 0;
        if (Common.networkConnected(this)) {
            String url = CommonShop.URL_SERVER + "ShopServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "updateShopNosState");
            jsonObject.addProperty("shop_id", shop_id);
            try {
                String shopResult = new CommonTask(url, jsonObject.toString()).execute().get();
                shopCount = Integer.valueOf(shopResult);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (shopCount == 0) {
                Log.e("FriendMainActivity-updateShopNosState：", "更新通知狀態失敗/無資料更新");
            } else {
                Log.e("FriendMainActivity-updateShopNosState：", "更新通知狀態成功");
            }
        } else {
            Log.e("FriendMainActivity-updateShopNosState：", "連線server失敗");
        }
    }

    //發送通知
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
                .setGroupSummary(true)
                .setGroup(GROUP_KEY)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
        NOTIFICATION_ID++;  //可能會有多則通知同時發送,以此設置不同id才能發送
    }
}
