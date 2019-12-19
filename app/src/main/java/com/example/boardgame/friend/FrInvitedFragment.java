package com.example.boardgame.friend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.Common;
import com.example.boardgame.notification.Websocket.AddFriendService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class FrInvitedFragment extends Fragment {
    private static final String TAG = "TAG_FrInvitedFragment";

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fr_invited, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        // 顯示 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
        // 置換 TabBar 的 menu
        MainActivity.setTabBar(MainActivity.TAB_FRIEND);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriend();
        FrInvitedAdapter adapter =  (FrInvitedAdapter)recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new FrInvitedAdapter(FrInvitedFragment.this, getFriend());
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setFriends(getFriend());
            adapter.notifyDataSetChanged();
        }
    }

    private List<FriendViewModel> getFriend() {
        List<FriendViewModel> friendViewModelList = new ArrayList<>();

//        ===== 有修改 =====
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("playerId", Common.loadPlayerId(getActivity()));
        jsonObject.addProperty("action", "invited");
        String jsonOut = jsonObject.toString();

        MyTask task = new MyTask("http://10.0.2.2:8080/Advertisement_Server/GetFriendList", jsonOut);
//        ===============
        try {
            String result = task.execute().get();
            Log.i("POST_RESULT", result);

            List<Friend> friends = convertJSONstringToFriendList(result);

            for (Friend friend : friends) {

                FriendViewModel friendViewModel = new FriendViewModel(friend.getPlayer2Name(),
                        friend.getPlayer2Pic(), friend.getPlayer2Mood(), friend.getPlayer2Id(), friend.getPointCount());

                if (friend.getInviteStatus() == 1) {
                    friendViewModelList.add(friendViewModel);
                }
            }

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return friendViewModelList;
    }

    private List<Friend> convertJSONstringToFriendList(String json) {
        Gson gson = new Gson();
        FriendListResult friendListResult = gson.fromJson(json, FriendListResult.class);
        return friendListResult.getResult();
    }


    private class FrInvitedAdapter extends RecyclerView.Adapter<MyViewHolder> {
        Context context;
        List<FriendViewModel> friends;
        public FrInvitedAdapter(FrInvitedFragment frInvitedFragment, List<FriendViewModel> friends) {
            context = frInvitedFragment.getContext();
            this.friends = friends;
        }

        public void setFriends(List<FriendViewModel> friends){
            this.friends = friends;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.itemview_invited, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int index) {
            final FriendViewModel friendViewModel = friends.get(index);
            holder.tvName.setText(friendViewModel.getFrNkName());
            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//        ===== 有修改 =====
                    Gson gson = new Gson();

                    Friend friend = new Friend(friendViewModel.getFrID(), Common.loadPlayerId(getActivity()), friendViewModel.getPointCount(), 2);
                    String jsonOut = gson.toJson(friend);

                    MyTask task = new MyTask("http://10.0.2.2:8080/Advertisement_Server/CreateFriend", jsonOut);
//        ===============
                    try {
                        int result = Integer.valueOf(task.execute().get());
                        if (result!=0) {
                            friends.remove(index);
                            Log.d(TAG, "result = " + result);
                        }
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }

                    JsonObject addFriendJsonObject = new JsonObject();
                    addFriendJsonObject.addProperty("receiver", friendViewModel.getFrID());
                    String addFriendJson = new Gson().toJson(addFriendJsonObject);
                    if(!addFriendJson.isEmpty()){
                        AddFriendService.addFriendnosWebSocketClient.send(addFriendJson);}
                }
            });
            holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//        ===== 有修改 =====
                    Gson gson = new Gson();

                    Friend friend = new Friend(friendViewModel.getFrID(), Common.loadPlayerId(getActivity()));
                    String jsonOut = gson.toJson(friend);

                    MyTask task = new MyTask("http://10.0.2.2:8080/Advertisement_Server/DeleteFriend", jsonOut);

                    try {
                        String result = task.execute().get();
                        Log.i("POST_RESULT", result);
                        if (!"0".equals(result)) {
                            friends.remove(index);
                            notifyDataSetChanged();
                        }else {
                            Log.d(TAG, "Delete invitation failed !");
                        }
                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }
//        ===============
                }
            });

            if (friendViewModel.getFrPic() != null) {
                byte[] image = Base64.decode(friendViewModel.getFrPic(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                holder.ivFriend.setImageBitmap(bitmap);
            }
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount = " + friends.size());
            return friends.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnDecline;
        Button btnAccept;
        ImageView ivFriend;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnAccept = itemView.findViewById(R.id.btnAccept);

            ivFriend = itemView.findViewById(R.id.ivFriend);

        }
    }

//======== OptionsMenu ========

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_noteadd, menu);
    }

}