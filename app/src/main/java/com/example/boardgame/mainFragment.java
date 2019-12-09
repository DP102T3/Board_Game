package com.example.boardgame;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import static com.example.boardgame.MainActivity.ADMIN;
import static com.example.boardgame.MainActivity.PLAYER;
import static com.example.boardgame.MainActivity.SHOP;
import static com.example.boardgame.MainActivity.loginId;
import static com.example.boardgame.chat.Common.savePlayerId;


public class mainFragment extends Fragment {
    private Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("Test List");
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ＊測試：玩家
        view.findViewById(R.id.btUserNameRyan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 紀錄登入身份
                loginId = PLAYER;
                // 將假的 玩家id 存入 偏好設定
                savePlayerId(activity,"chengchi1223");
                // 置換 BottomBar 的 menu
                MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);
                Navigation.findNavController(v)
                        .navigate(R.id.action_mainFragment_to_listFriendsFragment);
            }
        });
        view.findViewById(R.id.btUserNameMay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 紀錄登入身份
                loginId = PLAYER;
                // 將假的 玩家id 存入 偏好設定
                savePlayerId(activity,"gerfarn0523");
                Navigation.findNavController(v)
                        .navigate(R.id.action_mainFragment_to_listFriendsFragment);
            }
        });
        view.findViewById(R.id.btUserNameJerry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 紀錄登入身份
                loginId = PLAYER;
                // 將假的 玩家id 存入 偏好設定
                savePlayerId(activity,"jerry1124");
                Navigation.findNavController(v)
                        .navigate(R.id.action_mainFragment_to_listFriendsFragment);
            }
        });

        // ＊測試：後台管理員
        view.findViewById(R.id.btNotification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginId = ADMIN;
                Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_testFragment);
            }
        });

        view.findViewById(R.id.btShopList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 紀錄登入身份
                loginId = PLAYER;
                // 將假的 玩家id 存入 偏好設定
                savePlayerId(activity,"chengchi1223");
                Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_shopListFragment);
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
