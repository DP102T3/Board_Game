package com.example.boardgame.player;


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

import com.example.boardgame.R;
import com.example.boardgame.chat.Common;
import com.example.boardgame.chat.CommonTask;
import com.example.boardgame.chat.Msg;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import static com.example.boardgame.chat.Common.showToast;

public class PlayerSignUp_3 extends Fragment {
    private static final String TAG = "TAG_PlayerSignUp_3";
    private Activity activity;
    private String account;
    private String password;
    EditText etFullName, etNickName, etGender, etBirthday;
    Button btSubmit;
    Gson gson = new Gson();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("玩家註冊");
        Bundle bundle = getArguments();
        account = bundle.getString("account");
        password = bundle.getString("password");
        return inflater.inflate(R.layout.fragment_player_sign_up_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etFullName = view.findViewById(R.id.etFullName);
        etNickName = view.findViewById(R.id.etNickName);
        etGender = view.findViewById(R.id.etGender);
        etBirthday = view.findViewById(R.id.etBirthday);

        btSubmit = view.findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = etFullName.getText().toString().trim();
                String nickName = etNickName.getText().toString().trim();
                String gender = etGender.getText().toString().trim();
                String birthday = etBirthday.getText().toString().trim();

                Boolean valid = true;
                if (fullName.isEmpty()) {
                    valid = false;
                    etFullName.setError(getString(R.string.txCanNotBeEmpty));
                }
                if (nickName.isEmpty()) {
                    valid = false;
                    etNickName.setError(getString(R.string.txCanNotBeEmpty));
                }
                if (gender.isEmpty()) {
                    valid = false;
                    etGender.setError(getString(R.string.txCanNotBeEmpty));
                }
                if (birthday.isEmpty()) {
                    valid = false;
                    etBirthday.setError(getString(R.string.txCanNotBeEmpty));
                }
                if (!valid) {
                    return;
                } else {
                    JsonObject jsonOut = new JsonObject();
                    jsonOut.addProperty("account", account);
                    jsonOut.addProperty("password", password);
                    jsonOut.addProperty("fullName", fullName);
                    jsonOut.addProperty("nickName", nickName);
                    jsonOut.addProperty("gender", gender);
                    jsonOut.addProperty("birthday", birthday);

                    String jsonIn;
                    int count = 0;
                    if (Common.networkConnected(activity)) {
                        String url = Common.SERVLET_URI;
                        try {
                            jsonIn = new CommonTask(url, jsonOut.toString()).execute().get();
                            JsonObject jsonObject = gson.fromJson(jsonIn, JsonObject.class);
                            count = jsonObject.get("count").getAsInt();
                            Log.d(TAG, "count = " + count);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }

                        if (count > 0) {
//                            Navigation.findNavController(btSubmit).navigate(R.id.從playerSignUp_3到揪團首頁);
//                            同時更新logIn數值
                        }

                    } else {
                        showToast(activity, R.string.tx_NoNetwork);
                    }
                }
            }
        });
    }
}
