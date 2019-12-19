package com.example.boardgame.login;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.notification.Common;
import com.example.boardgame.notification.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.boardgame.MainActivity.ADMIN;
import static com.example.boardgame.MainActivity.PLAYER;
import static com.example.boardgame.MainActivity.SHOP;
import static com.example.boardgame.MainActivity.loginId;
import static com.example.boardgame.MainActivity.onBottomId;
import static com.example.boardgame.MainActivity.onTabMenu;
import static com.example.boardgame.chat.Common.loadPlayerId;
import static com.example.boardgame.chat.Common.savePlayerId;

public class LoginFragment extends Fragment {
    private final static String TAG = "TAG_LoginFragment";
    private EditText edAccount, edPassword;
    private TextView tvForgetPassword;
    private Button btLogin, btSingUp;
    private Activity activity;
    private Gson gson = new Gson();
    View tempView;

    String userId;

    Timer timer = new Timer(true);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        MainActivity.actionBar.hide();
        timer.schedule(new MyTimerTask(), 3000);
    }

    // 實作 TimerTask類別
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Looper.prepare();
            autoLogin(tempView);
            Looper.loop();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        onTabMenu = 0;
        onBottomId = 0;

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onCreateView()");
        userId = loadPlayerId(activity);
        Log.d(TAG, "userId = " + loadPlayerId(activity));
        tempView = view;
        edAccount = view.findViewById(R.id.edAccount);
        edPassword = view.findViewById(R.id.edPassword);
        tvForgetPassword = view.findViewById(R.id.tvForgetPassword);
        btLogin = view.findViewById(R.id.btLogin);
        btSingUp = view.findViewById(R.id.btSingUp);

        // 登入按鈕
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取得使用者輸入的帳號密碼
                String account = edAccount.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                if (account.length() <= 0 & password.length() <= 0) {
                    edAccount.setError("請輸入帳號");
                    edPassword.setError("請輸入密碼");
                    return;
                } else if (account.length() <= 0) {
                    edAccount.setError("請輸入帳號");
                    return;
                } else if (password.length() <= 0) {
                    edPassword.setError("請輸入密碼");
                    return;
                }

                //判斷使用者類型
                String type = null;
                //店家帳號正規表達式判斷
                String shopPattern = "^[0-9]{8}";
                //後台帳號判斷
                boolean status = account.contains("boardgame.com");
                Log.d(TAG, "account = " + account);
                Log.d(TAG, "status = " + status);
                if (account.matches(shopPattern)) {
                    type = "shop";
                } else if (status) {
                    type = "administrator";
                } else {
                    type = "player";
                }

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "LoginServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("type", type);
                    jsonObject.addProperty("account", account);
                    jsonObject.addProperty("password", password);
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        if (result.equals("correct")) {
                            // 儲存目前登入帳號
                            savePlayerId(activity, account);

                            // 依據不同身份指派相對應的參數值
                            switch (type) {
                                case "player":
                                    // 紀錄登入身份
                                    loginId = PLAYER;
                                    // 跳轉到玩家首頁（暫時設定跳轉到聊天頁面）
                                    Navigation.findNavController(v).navigate(R.id.groupsFragment);
                                    break;
                                case "shop":
                                    // 紀錄登入身份
                                    loginId = SHOP;
                                    // 跳轉到店家首頁
                                    Navigation.findNavController(v).navigate(R.id.shop_infoFragment);
                                    break;
                                case "administrator":
                                    // 紀錄登入身份
                                    loginId = ADMIN;
                                    Navigation.findNavController(v).navigate(R.id.systemNotificationFragment);
                                    Log.d(TAG, "type = " + type);
                                    Log.d(TAG, "loginId = " + loginId);
                                    break;
                                default:
                                    loginId = 0;
                            }

                            Toast.makeText(activity, "登入成功", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(activity, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
                            type = null;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Toast.makeText(activity, "未連接伺服器", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 註冊按鈕
        btSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 新使用者註冊
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signUpSelect);
            }
        });

        final Button button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(button).navigate(R.id.action_loginFragment_to_mainFragment);
            }
        });
    }

    private void autoLogin(View view) {
        try {
            if (!userId.equals("")) {
                //判斷使用者類型
                String type = null;
                //店家帳號正規表達式判斷
                String shopPattern = "^[0-9]{8}";
                //後台帳號判斷
                boolean status = userId.contains("boardgame.com");
                Log.d(TAG, "userId = " + userId);
                Log.d(TAG, "auto_status = " + status);

                if (userId.matches(shopPattern)) {
                    type = "shop";
                } else if (status) {
                    type = "administrator";
                } else {
                    type = "player";
                }

                switch (type) {
                    case "player":
                        // 紀錄登入身份
                        loginId = PLAYER;
                        // 跳轉到玩家首頁（暫時設定跳轉到聊天頁面）
                        Navigation.findNavController(view).navigate(R.id.groupsFragment);
                        break;
                    case "shop":
                        // 紀錄登入身份
                        loginId = SHOP;
                        // 跳轉到店家首頁
                        Navigation.findNavController(view).navigate(R.id.shop_infoFragment);
                        break;
                    case "administrator":
                        // 紀錄登入身份
                        loginId = ADMIN;
                        Navigation.findNavController(view).navigate(R.id.systemNotificationFragment);
                        Log.d(TAG, "type = " + type);
                        Log.d(TAG, "loginId = " + loginId);
                        break;
                    default:
                        loginId = 0;
                }

                Toast.makeText(activity, "登入成功", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.actionBar.hide();
    }
}
