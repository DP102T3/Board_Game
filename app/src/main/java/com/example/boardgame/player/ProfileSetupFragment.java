package com.example.boardgame.player;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;

import static com.example.boardgame.MainActivity.loginId;
import static com.example.boardgame.chat.Common.disConnectSocket;


public class ProfileSetupFragment extends Fragment {
    private static final String TAG = "TAG_ProfileSetupFragment";
    private Activity activity;
    private String playerId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("設置");
        return inflater.inflate(R.layout.fragment_profile_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerId = Common.loadPlayerId(activity);

        // 個人資訊編輯
        view.findViewById(R.id.btPlayerEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // 點數紀錄
        view.findViewById(R.id.btPointRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // 登出
        view.findViewById(R.id.btLogOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 初始化登入身份
                loginId = 0;
                // 清空使用者帳號
                Common.savePlayerId(activity, "");
                // 關閉聊天的WebSocket
                disConnectSocket();
                Navigation.findNavController(v).navigate(R.id.action_profileSetupFragment_to_loginFragment);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }
}
