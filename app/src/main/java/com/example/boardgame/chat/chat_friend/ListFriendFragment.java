package com.example.boardgame.chat.chat_friend;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.Common;
import com.example.boardgame.chat.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.example.boardgame.chat.Common.loadPlayerId;

public class ListFriendFragment extends Fragment {
    private static final String TAG = "TAG_List_Friend_Frag";
    Activity activity;
    private String playerId;
    private RecyclerView rvFriends;
    List<Friend> friends;

    //  變更acition bar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_note, menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        // 顯示出上層的optionmenu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("私聊");
        return inflater.inflate(R.layout.fragment_list_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFriends = view.findViewById(R.id.rvFriends);
    }

    @Override
    public void onStart() {
        super.onStart();

        // 顯示 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
        // 置換 TabBar 的 menu
        MainActivity.setTabBar(MainActivity.TAB_CHAT);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);

        // 取得 偏好設定的 playerId
        playerId = loadPlayerId(activity);
        Log.d(TAG, "playerId = " + playerId);


        // 連線Servlet取得playerId所有Friends資料
        if(Common.networkConnected(activity)){
            String url = Common.SERVLET_URI;
            JsonObject jsonOut = new JsonObject();
            jsonOut.addProperty("action", "searchFriends");
            jsonOut.addProperty("playerId", playerId);

            friends = new ArrayList<>();
            try {
                String inStr = new CommonTask(url, jsonOut.toString()).execute().get();
                friends = new Gson().fromJson(inStr, new TypeToken<List<Friend>>(){}.getType());

            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            if(friends == null){
                Common.showToast(activity, R.string.txNoFriends);
            }else {
                Log.d(TAG, "Get friends success !");
            }
        }else {
            Common.showToast(activity, R.string.tx_NoNetwork);
        }

        // 設置 RecyclerView
        rvFriends.setLayoutManager(new LinearLayoutManager(activity));
        rvFriends.getRecycledViewPool().setMaxRecycledViews(0, 0);

        rvFriends.setAdapter(new FriendAdapter(activity, friends));
        rvFriends.getAdapter().notifyDataSetChanged();
    }
}
