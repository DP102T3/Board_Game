package com.example.boardgame.chat.chat_group;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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


// 顯示已加入的揪團清單
public class ListGroupFragment extends Fragment {
    private static final String TAG = "TAG_List_Group_Fragment";
    private Activity activity;
    private String playerId;
    RecyclerView rvGroups;
    List<Group> joinedGroups;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("Groups List");
        return inflater.inflate(R.layout.fragment_list_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvGroups = view.findViewById(R.id.rvGroups);

    }

    @Override
    public void onStart() {
        super.onStart();
        // 顯示 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);

        // 取得 偏好設定的 playerId
        playerId = loadPlayerId(activity);
        Log.d(TAG, "playerId = " + playerId);

        // 連線Servlet取得playerId所屬Groups資料
        if(Common.networkConnected(activity)){
            String url = Common.SERVLET_URI;
            JsonObject jsonOut = new JsonObject();
            jsonOut.addProperty("action", "searchJoinedGroups");
            jsonOut.addProperty("playerId", playerId);

            joinedGroups = new ArrayList<>();
            try {
                String inStr = new CommonTask(url, jsonOut.toString()).execute().get();
                joinedGroups = new Gson().fromJson(inStr, new TypeToken<List<Group>>(){}.getType());
            }catch (Exception e){
                Log.e(TAG, e.toString());
            }
            if(joinedGroups == null){
                Common.showToast(activity, R.string.txNoJoinedGroups);
            }else {
                Log.d(TAG, "Get joinedGroups success !");
            }
        }else {
            Common.showToast(getActivity(), R.string.tx_NoNetwork);
        }

        // 設置 RecyclerView
        rvGroups.setLayoutManager(new LinearLayoutManager(activity));
        rvGroups.getRecycledViewPool().setMaxRecycledViews(0, 0);

        rvGroups.setAdapter(new GroupAdapter(activity, joinedGroups));
        rvGroups.getAdapter().notifyDataSetChanged();
    }
}
