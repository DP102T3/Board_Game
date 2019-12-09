package com.example.boardgame.chat.chat_friend;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import com.example.boardgame.R;
import com.example.boardgame.chat.Common;
import com.example.boardgame.chat.ImageTask;

import java.util.List;

// The Adapter of Friends

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {
    private static final String TAG = "TAG_FriendAdapter";
    private Activity activity;
    private List<Friend> friends;
    private ImageTask imageTask;

    public FriendAdapter(Activity activity, List<Friend> friends) {
        this.activity = activity;
        this.friends = friends;
    }

    @Override
    public int getItemCount() {
        if(friends == null){
            return 0;
        }
        return friends.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPortrait;
        TextView tvFriendNkName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPortrait = itemView.findViewById(R.id.ivPortrait);
            tvFriendNkName = itemView.findViewById(R.id.tvListName);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.item_view_chat_list, parent, false);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("from", "ListFriends");
                bundle.putSerializable("friend", friend);
                Log.d(TAG, String.format("friendId = %s, friendNkName = %s",friend.getFriendId(), friend.getFriendNkName()));
                Navigation.findNavController(view).navigate(R.id.action_listFriendsFragment_to_chatGroupFragment2, bundle);
            }
        });
    }
}
