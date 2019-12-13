package com.example.boardgame.player;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;

import static com.example.boardgame.player.Common.loadPlayerId;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile1_Fragment extends Fragment {
    private static final String TAG = "TAG_Profile1_Fragment";
    Activity activity;
    private String playerId;

    public Profile1_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile1, container, false);
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

        // 取得 偏好設定的 playerId
        playerId = loadPlayerId(activity);
        Log.d(TAG, "playerId = " + playerId);
    }
}
