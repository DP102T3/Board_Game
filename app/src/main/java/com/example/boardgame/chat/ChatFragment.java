package com.example.boardgame.chat;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.chat_friend.Friend;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.example.boardgame.chat.Common.chatWebSocketClient;
import static com.example.boardgame.chat.Common.loadPlayerId;
import static com.example.boardgame.chat.Common.showToast;
import static com.example.boardgame.chat.Msg.TYPE_PLAYER_SEND;
import static com.example.boardgame.chat.Msg.TYPE_RECEIVED;


public class ChatFragment extends Fragment {
    private static final String TAG = "TAG_ChatFragment";
    private Activity activity;
    private Button btSend;
    private EditText etMsg;

    private String playerId;
    private String from;
    private Friend friend;
    private String friendId;
    private String friendNkName;
    private int position;
    private int groupNo;
    private String groupName;

    private LocalBroadcastManager broadcastManager;
    private List<Msg> msgs = new ArrayList<>();
    private RecyclerView rvChat;
    private Gson gson = new Gson();
    private ImageTask imageTask;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 取得 偏好設定的 playerId 及 前一頁的 Group資訊
        playerId = loadPlayerId(activity);

        getMsgs();

        // 設定 RecyclerView（尚未將值放入）
        rvChat = view.findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(activity));
        // 避免 RecyclerView 頭尾itemView消失 的問題
        rvChat.getRecycledViewPool().setMaxRecycledViews(0, 0);

        // 延遲 50 毫秒，將 RecyclerView 拉到最底
        rvChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    rvChat.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rvChat.scrollToPosition(rvChat.getAdapter().getItemCount() - 1);
                        }
                    }, 50);
                }
            }
        });

        etMsg = view.findViewById(R.id.etMsg);

        // 設定 btSend按鈕 監聽器（點擊後將訊息傳送到 Socket）
        btSend = view.findViewById(R.id.btSend);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMsg.getText().toString();
                if (message.trim().isEmpty()) {
                    showToast(activity, R.string.textMessageEmpty);
                    return;
                } else {
                    // 將欲傳送訊息轉成JSON後送出
                    JsonObject msgJsonOut = new JsonObject();
                    switch (from) {
                        case "ListFriends":
                            msgJsonOut.addProperty("action", "sendChatFriendMsg");
                            msgJsonOut.addProperty("msg", message);
                            msgJsonOut.addProperty("playerId", playerId);
                            msgJsonOut.addProperty("friendId", friendId);
                            msgJsonOut.addProperty("position", position);
                            break;
                        case "ListGroups":
                            msgJsonOut.addProperty("action", "sendGroupChatMsg");
                            msgJsonOut.addProperty("msg", message);
                            msgJsonOut.addProperty("playerId", playerId);
                            msgJsonOut.addProperty("groupNo", groupNo);
                    }
                    chatWebSocketClient.send(msgJsonOut.toString());
                    Log.d(TAG, "ToSocket output: " + msgJsonOut);
                }
                etMsg.setText("");
            }
        });
    }

    private void getMsgs() {
        // 區別 私聊 及 團聊
        from = getArguments().getString("from");

        switch (from) {
            case "ListFriends":
                // 私聊頁面 所需資訊
                friend = (Friend) getArguments().getSerializable("friend");
                friendId = friend.getFriendId();
                friendNkName = friend.getFriendNkName();
                position = friend.getPosition();
                // 設定標題
                activity.setTitle(friendNkName);
                Log.d(TAG, String.format("playerId = %s, friendId = %s, friendNkName = %s", playerId, friendId, friendNkName));
                break;
            case "ListGroups":
                // 團聊頁面 所需資訊
                groupNo = getArguments().getInt("groupNo");
                groupName = getArguments().getString("groupName");
                // 設定標題
                activity.setTitle(groupName);
                Log.d(TAG, String.format("playerId = %s, groupNo = %s, groupName = %s", playerId, groupNo, groupName));
        }

        // 從 Servlet 取得聊天內容（利用上列取得的 playerId 及 groupNo）
        if (Common.networkConnected(activity)) {
            String url = Common.SERVLET_URI;
            JsonObject jsonOut = new JsonObject();

            // 將所需資訊存入 JsonObject
            switch (from) {
                case "ListFriends":
                    jsonOut.addProperty("action", "searchChatFriend");
                    jsonOut.addProperty("playerId", playerId);
                    jsonOut.addProperty("friendId", friendId);
                    jsonOut.addProperty("position", position);
                    Log.d(TAG, String.format("outStr : action = %s, playerId = %s, friendId = %s, position = %s", "searchChatFriend", playerId, friendId, position));
                    break;
                case "ListGroups":
                    jsonOut.addProperty("action", "searchChatGroup");
                    jsonOut.addProperty("playerId", playerId);
                    jsonOut.addProperty("groupNo", groupNo);
                    Log.d(TAG, String.format("outStr : action = %s, playerId = %s, groupNo = %d", "searchChatGroup", playerId, groupNo));
            }

            try {
                String inStr = new CommonTask(url, jsonOut.toString()).execute().get();
                msgs = new Gson().fromJson(inStr, new TypeToken<List<Msg>>() {
                }.getType());
                Log.d(TAG, "msgs = " + msgs);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (msgs == null) {
                showToast(activity, R.string.txNoChatMsg);
            } else {
                Log.d(TAG, "Get chatList success !");
            }
        } else {
            showToast(activity, R.string.tx_NoNetwork);
        }
    }

    // 測試用，註冊廣播 WebSocket 的 廣播接收器
    private void registerChatReceiver() {
        IntentFilter chatFilter;
        chatFilter = new IntentFilter("NewChatMsg");
        broadcastManager.registerReceiver(chatReceiver, chatFilter);
        Log.d(TAG, "registerChatReceiver()");
    }

    // 接收到指定廣播，傳送請求給 Servlet ，要求回應 訊息內容 ，並在 RecyclerView 中呈現
    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String jsonIn = intent.getStringExtra("jsonIn");
//            Msg msg = gson.fromJson(jsonIn, Msg.class);
            getMsgs();

            // 每次開啟畫面時，若 ChatGroupAdapter 未有實體，則 建立實體 -> 以 RecyclerView實體 呼叫 setAdapter()方法，以載入新的 msgs集合
            // 反之，則將新的 msgs集合 存入 -> 刷新畫面
            ChatGroupAdapter adapter = (ChatGroupAdapter) rvChat.getAdapter(); // 向下強制轉型
            if (adapter == null) {
                adapter = new ChatGroupAdapter(activity, msgs);
                rvChat.setAdapter(adapter);
            } else {
                adapter.setMsgs(msgs);
                Log.d(TAG, "msgs.size() = " + msgs.size());
                adapter.notifyDataSetChanged();
            }

            // 將捲軸 拉到最下方
            rvChat.scrollToPosition(adapter.getItemCount() - 1);
            Log.d(TAG, "on Receive : refresh RecyclerView");
        }
    };

    public class ChatGroupAdapter extends RecyclerView.Adapter<ChatGroupAdapter.MyViewHolder> {
        Activity activity;
        List<Msg> msgs;

        public ChatGroupAdapter(Activity activity, List<Msg> msgs) {
            this.activity = activity;
            this.msgs = msgs;
        }

        @Override
        public int getItemCount() {
            if (msgs == null) {
                return 0;
            }
            return msgs.size();
        }

        public void setMsgs(List<Msg> msgs) {
            this.msgs = msgs;   // 用於將 Servlet 取得的值存入
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvSender;
            ImageView ivPortrait;
            TextView tvMsg_recieved;
            TextView tvMsg_send;
            ConstraintLayout layoutRecieved;
            ConstraintLayout layoutSend;
            CardView cvRecieved;
            CardView cvSend;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                // 關聯 itemView 的 變數 及 UI元件
                ivPortrait = itemView.findViewById(R.id.ivPortrait);
                tvSender = itemView.findViewById(R.id.tvSender);
                tvMsg_recieved = itemView.findViewById(R.id.tvMsg_recieved);
                tvMsg_send = itemView.findViewById(R.id.tvMsg_playerSend);
                layoutRecieved = itemView.findViewById(R.id.layoutRecieved);
                layoutSend = itemView.findViewById(R.id.layoutPlayerSend);
                cvRecieved = itemView.findViewById(R.id.cvRecieve);
                cvSend = itemView.findViewById(R.id.cvPlayer);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 載入 itemView 的 Layout
            View itemView = LayoutInflater.from(activity).inflate(R.layout.item_view_msg, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatGroupAdapter.MyViewHolder holder, int position) {
            Msg msg = msgs.get(position);
            // 發訊者
            String playerName = msg.getPlayerName();
            // 內容
            String content = msg.getContent();
            // 內容的資料型態（文字、圖片 等）
            int contentType = msg.getContentType();
            // 發送類型（發送者（0） 或 接收者（1））
            int type = msg.getType();

            if (type == TYPE_RECEIVED) {
                // 以 playerId 到 Servlet 取圖
                String url = Common.SERVLET_URI;
                String imageId = msg.getPlayerId();
                int imageSize = getResources().getDisplayMetrics().widthPixels / 100 * 68;
                try {
                    imageTask = new ImageTask(url, imageId, imageSize, holder.ivPortrait);
                    imageTask.execute();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                if ("ListGroups".equals(from)) {
                    holder.tvSender.setText(playerName);
                } else if ("ListFriends".equals(from)) {
                    holder.tvSender.setVisibility(View.GONE);
                }
                holder.ivPortrait.setImageResource(R.drawable.portrait_default);
                holder.tvMsg_recieved.setText(content);
                holder.tvMsg_send.setVisibility(View.GONE);
                holder.layoutSend.setVisibility(View.GONE);
                holder.cvSend.setVisibility(View.GONE);
            } else if (type == TYPE_PLAYER_SEND) {
                holder.tvMsg_send.setText(content);
                holder.tvMsg_recieved.setVisibility(View.GONE);
                holder.ivPortrait.setVisibility(View.GONE);
                holder.layoutRecieved.setVisibility(View.GONE);
                holder.cvRecieved.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_AND_BOTTOM);

        // 宣告 broadcastManager實體
        broadcastManager = LocalBroadcastManager.getInstance(activity);
        // 設置廣播接收器
        registerChatReceiver();
        // 與 Socket 連線
        Common.connectSocket(activity, loadPlayerId(activity));
        Log.d(TAG, "loadPlayerId(activity) = " + loadPlayerId(activity));

        // 每次開啟畫面時，若 ChatGroupAdapter 未有實體，則 建立實體 -> 以 RecyclerView實體 呼叫 setAdapter()方法，以載入新的 msgs集合
        // 反之，則將新的 msgs集合 存入 -> 刷新畫面
        ChatGroupAdapter adapter = (ChatGroupAdapter) rvChat.getAdapter(); // 向下強制轉型
        if (adapter == null) {
            adapter = new ChatGroupAdapter(activity, msgs);
            rvChat.setAdapter(adapter);
        } else {
            adapter.setMsgs(msgs);
            adapter.notifyDataSetChanged();
        }
        // 將捲軸 拉到最下方
        rvChat.scrollToPosition(adapter.getItemCount() - 1);

        Log.d(TAG, "onStart : refresh RecyclerView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Fragment頁面切換時解除註冊，但不需要關閉WebSocket，
        broadcastManager.unregisterReceiver(chatReceiver);
    }
}
