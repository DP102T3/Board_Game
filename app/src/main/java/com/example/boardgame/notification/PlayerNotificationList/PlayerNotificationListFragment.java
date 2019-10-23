package com.example.boardgame.notification.PlayerNotificationList;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.boardgame.notification.Common;
import com.example.boardgame.notification.Task.CommonTask;
import com.example.boardgame.notification.Task.ImageTask;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class PlayerNotificationListFragment extends Fragment {
    private String player_id;
    private static final String TAG = "TAG_MainFragment";
    private RecyclerView recyclerView;
    private Activity activity;
    private CommonTask getNosTask;
    private CommonTask userDeleteTask;
    private ImageTask userImageTask;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        player_id= Common.loadPlayer_id(activity);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        final List<Notification> notifications = getNotifications();
        showNotifications(notifications);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                final List<Notification> notifications = getNotifications();
                showNotifications(notifications);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private List<Notification> getNotifications() {
        List<Notification> notifications = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "BoardGameServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            jsonObject.addProperty("player_id",player_id);
            String jsonOut = jsonObject.toString();
            getNosTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getNosTask.execute().get();
                Type listType = new TypeToken<List<Notification>>() {
                }.getType();
                notifications = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return notifications;
    }

    private void showNotifications(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            Common.showToast(activity, R.string.textNoUsersFound);
            return;
        }
        NotificationAdapter notificationAdapter = (NotificationAdapter) recyclerView.getAdapter();
        if (notificationAdapter == null) {
            recyclerView.setAdapter(new NotificationAdapter(activity, notifications));
        } else {
            notificationAdapter.setNotifications(notifications);
            notificationAdapter.notifyDataSetChanged();
        }
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Notification> notifications;

        NotificationAdapter(Context context, List<Notification> notifications) {
            layoutInflater = LayoutInflater.from(context);
            this.notifications = notifications;
        }

        void setNotifications(List<Notification> notifications) {
            this.notifications = notifications;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvContent, tvTime;

            MyViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvContent = itemView.findViewById(R.id.tvContent);
                tvTime = itemView.findViewById(R.id.tvTime);
            }
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_notifination, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
            final Notification notification = notifications.get(position);
            myViewHolder.tvTitle.setText(notification.getPnote_title());
            myViewHolder.tvContent.setText(notification.getPnote_info());
            myViewHolder.tvTime.setText(notification.getSetup_time());
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 預留
                }
            });
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (getNosTask != null) {
            getNosTask.cancel(true);
            getNosTask = null;
        }

        if (userImageTask != null) {
            userImageTask.cancel(true);
            userImageTask = null;
        }

        if (userDeleteTask != null) {
            userDeleteTask.cancel(true);
            userDeleteTask = null;
        }
    }
}
