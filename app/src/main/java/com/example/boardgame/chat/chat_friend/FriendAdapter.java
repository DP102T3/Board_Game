package com.example.boardgame.chat.chat_friend;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;


import com.example.boardgame.R;

import java.util.List;

// The Adapter of Friends

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.MyViewHolder> {
    private static final String TAG = "TAG_FriendAdapter";
    private Activity activity;
    private List<Friend> friends;

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
        TextView tvFriendNkName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFriendNkName = itemView.findViewById(R.id.tvListName);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_item_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Friend friend = friends.get(position);
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
