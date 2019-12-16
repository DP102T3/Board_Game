package com.example.boardgame.player;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;

public class Profile2_Fragment extends Fragment {
    private static final String TAG = "TAG_Profile1_Fragmen2";
    private Activity activity;
    private String playerId;
    private Gson gson = new Gson();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("我的揪團");
        return inflater.inflate(R.layout.fragment_profile2, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 顯示 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
        // 置換 TabBar 的 menu
        MainActivity.setTabBar(MainActivity.TAB_PROFILE);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);
    }
}
