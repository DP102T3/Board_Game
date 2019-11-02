package com.example.boardgame.login;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

public class LoginFragment extends Fragment {
    private final static String TAG = "LoginFragment";
    private EditText edAccount, edPassword;
    private TextView tvForgetPassword;
    private Button btLogin, btSingUp;
    private Activity activity;
    private Gson gson = new Gson();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edAccount = view.findViewById(R.id.edAccount);
        edPassword = view.findViewById(R.id.edPassword);
        tvForgetPassword = view.findViewById(R.id.tvForgetPassword);
        btLogin = view.findViewById(R.id.btLogin);
        btSingUp = view.findViewById(R.id.btSingUp);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取得使用者輸入的帳號密碼
                String account = edAccount.getText().toString();
                String password = edPassword.getText().toString();
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

                String type = null;
                //店家帳號正規表達式判斷
                String shopPattern = "^[0-9]{8}";
                //後台帳號判斷
                boolean status = account.contains("＠boardGame.com");
                //判斷使用者類型
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
                            Toast.makeText(activity, "登入成功", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(v)
                                    .navigate(R.id.mainFragment);
                        } else {
                            Toast.makeText(activity, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Toast.makeText(activity, "未連接伺服器", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_AND_BOTTOM);
    }
}
