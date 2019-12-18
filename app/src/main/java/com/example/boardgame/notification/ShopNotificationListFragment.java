package com.example.boardgame.notification;


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

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class ShopNotificationListFragment extends Fragment {
    private static final String TAG = "TAG_ShopNotificationListFragment";
    private RecyclerView shopRecyclerView;
    private Activity activity;
    private CommonTask getNosTask;
    private CommonTask userDeleteTask;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int shop_id;
    private String shopId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_shop_notification_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);

        shop_id=CommonShop.loadShop_id(activity);
        shopId = String.valueOf(shop_id);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        shopRecyclerView = view.findViewById(R.id.shopRecyclerView);
        shopRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        final List<ShopNotification> shopNotifications = getNotifications();
        showNotifications(shopNotifications);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                final List<ShopNotification> shopNotifications = getNotifications();
                showNotifications(shopNotifications);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private List<ShopNotification> getNotifications() {
        List<ShopNotification> shopNotifications = null;
        if (CommonShop.networkConnected(activity)) {
            String url = CommonShop.URL_SERVER + "ShopServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getShopNotificationList");
            jsonObject.addProperty("shop_id",shopId);
            String jsonOut = jsonObject.toString();
            getNosTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getNosTask.execute().get();
                Type listType = new TypeToken<List<ShopNotification>>() {
                }.getType();
                shopNotifications = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            CommonShop.showToast(activity, R.string.textNoNetwork);
        }
        return shopNotifications;
    }

    private void showNotifications(List<ShopNotification> shopNotifications) {
        if (shopNotifications == null || shopNotifications.isEmpty()) {
            CommonShop.showToast(activity, R.string.textNoUsersFound);
            return;
        }
        NotificationAdapter notificationAdapter = (NotificationAdapter) shopRecyclerView.getAdapter();
        if (notificationAdapter == null) {
            shopRecyclerView.setAdapter(new NotificationAdapter(activity, shopNotifications));
        } else {
            notificationAdapter.setShopNotifications(shopNotifications);
            notificationAdapter.notifyDataSetChanged();
        }
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<ShopNotification> shopNotifications;

        NotificationAdapter(Context context, List<ShopNotification> shopNotifications) {
            layoutInflater = LayoutInflater.from(context);
            this.shopNotifications = shopNotifications;
        }

        void setShopNotifications(List<ShopNotification> shopNotifications) {
            this.shopNotifications = shopNotifications;
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
            return shopNotifications.size();
        }

        @NonNull
        @Override
        public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_notifination, parent, false);
            return new NotificationAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder myViewHolder, int position) {
            final ShopNotification shopNotification = shopNotifications.get(position);
            myViewHolder.tvTitle.setText(shopNotification.getSnote_title());
            myViewHolder.tvContent.setText(shopNotification.getSnote_info());
            myViewHolder.tvTime.setText(shopNotification.getSetup_time());
            Log.e(TAG, shopNotification.getSetup_time());
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (getNosTask != null) {
            getNosTask.cancel(true);
            getNosTask = null;
        }
        if (userDeleteTask != null) {
            userDeleteTask.cancel(true);
            userDeleteTask = null;
        }
    }
}
