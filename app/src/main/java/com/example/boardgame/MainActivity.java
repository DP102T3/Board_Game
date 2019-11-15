package com.example.boardgame;

import android.content.Context;
import android.content.Intent;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.boardgame.notification.Common;
import com.example.boardgame.notification.CommonShop;
import com.example.boardgame.notification.Websocket.NetWorkService;
import com.example.boardgame.shop.Shop;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//chengchi1223
//gerfarn0523
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "TAG_MainActivity";

    private static BottomNavigationView tabNavigationView;
    private static BottomNavigationView bottomNavigationView;
    public static final int ONLY_BOTTOM = 0;
    public static final int BOTH_TAB_AND_BOTTOM = 1;
    public static final int NEITHER_TAB_AND_BOTTOM = 2;
    // TabBar紀錄的頁面(由 setTabBar()方法 更改)
    public static  int onTabMenu = 0;
    // BottomBar紀錄的身份(由 setBottomBar()方法 更改)
    public static  int onBottomId = 0;
    // TabBar 使用的 menu
    public static  int TAB_CHAT = R.menu.tab_menu_chat;
    public static  int TAB_FRIEND = R.menu.tab_menu_friend;
    // BottomBar 使用的 menu
    public static  int BOTTOM_PLAYER = R.menu.bottom_menu_player;
    public static  int BOTTOM_SHOP = R.menu.bottom_menu_shop;

    // 目前登入身份（登入身份變更 或 登出 時，寫入程式內）
    public static  int loginId = 0;
    public static  int PLAYER = 91;
    public static  int SHOP = 92;
    public static  int ADMIN = 93;

    private int width;
    private int height;


    //   從infoFragment打包shop到editinfo
    public static Shop shop = new Shop();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Common.savePlayer_id(this, "chengchi1223");
        //CommonShop.saveShop_id(this,123456);

        // 設置 TabBar
        tabNavigationView = findViewById(R.id.tabNavigation);
        tabNavigationView.setVisibility(View.GONE);
        NavController tabNavController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(tabNavigationView, tabNavController);

        // 設置 BottomBar
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        NavController bottomNavController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, bottomNavController);

        // 取得螢幕長寬（像素）（用於定義 「點擊空白處隱藏鍵盤」方法 的點擊範圍）
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        height = metric.heightPixels;

        //開啟網路偵測服務及Websocket服務(notification)
        Intent networkIntent = new Intent(this, NetWorkService.class);
        this.startService(networkIntent);
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


    // 變更 TabBar 或 BottomBar 的狀態
    /*使用方式：
    * 在fragment的onStart內
    * 執行MainActivity.changeBarsStatus(參數：ONLY_BOTTOM、BOTH_TAB_AND_BOTTOM 或 NEITHER_TAB_AND_BOTTOM 其一)
    * 參考下列註解說明
    * */
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


    // 設置 TabBar
    /*使用方式：
    * 建立 R.menu.tab_menu.XXXXX.xml  (XXXXX 為自訂名稱)
    * 呼叫setTabBar(參數：指定 menu內 要替換的 xml)
    * */
    public static void setTabBar(int tabMenu){
        if (onTabMenu == tabNavigationView.getMenu().hashCode()){return;}

        tabNavigationView.getMenu().clear();
        tabNavigationView.inflateMenu(tabMenu);

        onTabMenu = tabNavigationView.getMenu().hashCode();
    }
    // 設置 BottomBar
    /*使用方式：
     * 建立 R.menu.tab_menu.XXXXX.xml  (XXXXX 為自訂名稱)
     * 呼叫setBottomBar(參數：指定 menu內 要替換的 xml)
     * */
    public static void setBottomBar(int bottomMenu){
        if (onBottomId == loginId){return;}

        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(bottomMenu);

        onBottomId = loginId;
    }
}
