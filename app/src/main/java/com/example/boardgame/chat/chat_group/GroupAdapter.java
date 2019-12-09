package com.example.boardgame.chat.chat_group;

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

// The Adapter of Groups
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {
    private static final String TAG = "TAG_GroupAdapter";
    private Activity activity;
    private List<Group> joinedGroups;
    private ImageTask imageTask;

    public GroupAdapter(Activity activity, List<Group> joinedGroups) {
        this.activity = activity;
        this.joinedGroups = joinedGroups;
    }

    @Override
    public int getItemCount() {
        if (joinedGroups == null) {
            return 0;
        }
        return joinedGroups.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPortrait;
        TextView tvGroupName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPortrait = itemView.findViewById(R.id.ivPortrait);
            tvGroupName = itemView.findViewById(R.id.tvListName);
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
        final Group group = joinedGroups.get(position);
        // 以 playerId 到 Servlet 取圖
        String url = Common.SERVLET_URI;
        int imageId = group.getGroupNo();
        int imageSize = activity.getResources().getDisplayMetrics().widthPixels / 100 * 68;
        imageTask = new ImageTask(url, imageId, imageSize, holder.ivPortrait);
        imageTask.execute();
        holder.tvGroupName.setText(group.getGroupName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("from", "ListGroups");
                bundle.putInt("groupNo", group.getGroupNo());
                bundle.putString("groupName", String.valueOf(group.getGroupName()));
                Log.d(TAG, String.format("groupNo = %s, groupName = %s", String.valueOf(group.getGroupNo()), group.getGroupName()));
                Navigation.findNavController(view).navigate(R.id.action_groupsFragment_to_chatGroupFragment2, bundle);
            }
        });
    }
}
