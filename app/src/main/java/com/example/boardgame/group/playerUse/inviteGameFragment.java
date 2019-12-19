package com.example.boardgame.group.playerUse;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.Common;
import com.example.boardgame.chat.CommonTask;
import com.example.boardgame.chat.ImageTask;
import com.example.boardgame.chat.chat_friend.Friend;
import com.example.boardgame.notification.Websocket.InviteFriendService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.example.boardgame.chat.Common.loadPlayerId;


public class inviteGameFragment extends Fragment {
    private static final String TAG = "TAG_invite_Game_Frag";
    Activity activity;
    private String playerId;
    RecyclerView rvFriends;
    List<Friend> friends;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("揪好友");
        return inflater.inflate(R.layout.fragment_invite_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 設置 RecyclerView
        rvFriends = view.findViewById(R.id.rvGroupInvite);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);

        // 取得 偏好設定的 playerId
        playerId = loadPlayerId(activity);
        Log.d(TAG, "playerId = " + playerId);

        // 連線Servlet取得playerId所有Friends資料
        if (Common.networkConnected(activity)) {
            String url = Common.SERVLET_URI;
            JsonObject jsonOut = new JsonObject();
            jsonOut.addProperty("action", "searchFriends");
            jsonOut.addProperty("playerId", playerId);

            friends = new ArrayList<>();
            try {
                String inStr = new CommonTask(url, jsonOut.toString()).execute().get();
                friends = new Gson().fromJson(inStr, new TypeToken<List<Friend>>() {
                }.getType());

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (friends == null) {
                Common.showToast(activity, R.string.txNoFriends);
            } else {
                Log.d(TAG, "Get friends success !");
            }
        } else {
            Common.showToast(activity, R.string.tx_NoNetwork);
        }
        rvFriends.setLayoutManager(new LinearLayoutManager(activity));
        rvFriends.getRecycledViewPool().setMaxRecycledViews(0, 0);
        FriendAdapter adapter = new FriendAdapter(activity, friends);
        rvFriends.setAdapter(adapter);
        rvFriends.getAdapter().notifyDataSetChanged();
    }

    public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {
        private Activity activity;
        private List<Friend> friends;
        private ImageTask imageTask;

        public FriendAdapter(Activity activity, List<Friend> friends) {
            this.activity = activity;
            this.friends = friends;
        }

        @Override
        public int getItemCount() {
            if (friends == null) {
                return 0;
            }
            return friends.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivPortrait;
            TextView tvFriendNkName;
            Button btInvite;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivPortrait = itemView.findViewById(R.id.ivPortrait);
                tvFriendNkName = itemView.findViewById(R.id.tvListName);
                btInvite = itemView.findViewById(R.id.btInvite);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(activity).inflate(R.layout.invite_game_friend, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Friend friend = friends.get(position);
            // 以 playerId 到 Servlet 取圖
            String url = Common.SERVLET_URI;
            String imageId = friend.getFriendId();
            int imageSize = activity.getResources().getDisplayMetrics().widthPixels / 100 * 68;
            imageTask = new ImageTask(url, imageId, imageSize, holder.ivPortrait);
            imageTask.execute();
            holder.tvFriendNkName.setText(friend.getFriendNkName());
            holder.btInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JsonObject inviteGroupJsonObject = new JsonObject();
                    inviteGroupJsonObject.addProperty("type", "inviteGroup");
                    inviteGroupJsonObject.addProperty("player2_id", friend.getFriendId());
                    inviteGroupJsonObject.addProperty("group_name", "中大桌遊");
                    String inviteGroupIdJson = new Gson().toJson(inviteGroupJsonObject);
                    if(!inviteGroupIdJson.isEmpty()){
                        InviteFriendService.inviteFriendWebSocketClient.send(inviteGroupIdJson);}
                }
            });
        }
    }
}
