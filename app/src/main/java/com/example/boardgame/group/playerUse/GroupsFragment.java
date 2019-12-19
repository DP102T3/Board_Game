package com.example.boardgame.group.playerUse;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.group.Common;
import com.example.boardgame.group.CommonTask;
import com.example.boardgame.group.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GroupsFragment extends Fragment {
    private static final String TAG = "TAG_GroupsFragment";
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommonTask groupGetAllTask;
    private ImageTask groupImageTask;
    private AdImageTask adImageTask;
    private RecyclerView rvAdShow, rvGroups;
    private List<GroupList> groupLists;
    private List<Advertisement> ads;

    private Timer timer = new Timer(true);
    private int currentIndex;
    private int adsSize;

    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentIndex == adsSize - 1) {
                        currentIndex = 0;
                        rvAdShow.smoothScrollToPosition(currentIndex);
                        rvAdShow.getAdapter().notifyDataSetChanged();
                    } else {
                        rvAdShow.smoothScrollToPosition(++currentIndex);
                        rvAdShow.getAdapter().notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvAdShow = view.findViewById(R.id.rvAdPic);
        rvAdShow.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));

        ads = getAds();
        showAds(ads);

        if (ads!=null&&ads.size() > 1) {
            timer.schedule(new MyTimerTask(), 4000, 4000);
        }

        /* 不處理捲動事件所以監聽器設為null */
        rvAdShow.setOnFlingListener(null);  //若無此行，執行會出錯
        /* 如果希望一次滑動一頁資料，要加上PagerSnapHelper物件 */
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(rvAdShow);

        rvGroups = view.findViewById(R.id.rvGroups);
        rvGroups.setLayoutManager(new GridLayoutManager(activity, 2));
        groupLists = getGroups();
        showGroups(groupLists);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showGroups(groupLists);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 顯示 BottomBar
        MainActivity.changeBarsStatus(MainActivity.ONLY_BOTTOM);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);

    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.actionBar.show();
    }

    private List<Advertisement> getAds() {
        List<Advertisement> adsDB = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/GroupListServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAds");
            String jsonOut = jsonObject.toString();
            groupGetAllTask = new CommonTask(url, jsonOut);//開新的執行緒
            try {
                String jsonIn = groupGetAllTask.execute().get();
                Type listType = new TypeToken<List<Advertisement>>() {
                }.getType();
                adsDB = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return adsDB;
    }

    private List<GroupList> getGroups() {
        List<GroupList> groups = null;

        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/GroupListServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllGroups");
            String jsonOut = jsonObject.toString();
            groupGetAllTask = new CommonTask(url, jsonOut);//開新的執行緒
            try {
                String jsonIn = groupGetAllTask.execute().get();
                Type listType = new TypeToken<List<GroupList>>() {
                }.getType();
                groups = new Gson().fromJson(jsonIn, listType);

                // 保留 index = 0 的位置，作為開團按鈕
                groups.add(0, new GroupList(0, "", "", ""));

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return groups;
    }

    private void showAds(List<Advertisement> ads) {
        if (ads == null || ads.isEmpty()) {
            Common.showToast(activity, R.string.textNoAdsFound);
            return;
        }
        AdAdapter adAdapter = (AdAdapter) rvAdShow.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (adAdapter == null) {
            rvAdShow.setAdapter(new AdAdapter(activity, ads));
        } else {
            adAdapter.setAds(ads);
            adAdapter.notifyDataSetChanged();//重刷
        }
    }

    private void showGroups(List<GroupList> groups) {
        if (groups == null || groups.isEmpty()) {
            Common.showToast(activity, R.string.textNoGroupsFound);
            return;
        }
        GroupAdapter groupAdapter = (GroupAdapter) rvGroups.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (groupAdapter == null) {
            rvGroups.setAdapter(new GroupAdapter(activity, groupLists));
        } else {
            groupAdapter.setGroups(groupLists);
            groupAdapter.notifyDataSetChanged();//重刷
        }
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<GroupList> groups;

        GroupAdapter(Context context, List<GroupList> groups) {
            layoutInflater = LayoutInflater.from(context);
            this.groups = groups;

        }

        void setGroups(List<GroupList> groups) {
            this.groups = groups;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivGroup;
            TextView tvGroupName, tvGroupTime, tvGroupArea;

            MyViewHolder(View itemView) {
                super(itemView);
                ivGroup = itemView.findViewById(R.id.ivGroup);
                tvGroupName = itemView.findViewById(R.id.tvGroupName);
                tvGroupTime = itemView.findViewById(R.id.tvGroupStarTime);
                tvGroupArea = itemView.findViewById(R.id.tvGroupArea);

            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.groups_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            final GroupList group = groups.get(position);
            String url = Common.URL_SERVER + "GroupListServlet";
            int id = group.getId();

            //if(groups.size()>=1 && position == 0) {
            if (position == 0) {
                holder.ivGroup.setImageResource(R.drawable.add);

                holder.tvGroupName.setText("");
                holder.tvGroupTime.setText("");
                holder.tvGroupArea.setText("");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 開團頁面從這連出去
                        Navigation.findNavController(view).navigate(R.id.action_groupsFragment_to_add_fragment);
                        Log.d(TAG, "New Group !!!!!!!!!!");
                    }
                });
            } else {
                groupImageTask = new ImageTask(url, id, holder.ivGroup);
                groupImageTask.execute();

                holder.tvGroupName.setText(group.getGroupName());
                holder.tvGroupTime.setText(group.getGroupDate().substring(0, 10));
                holder.tvGroupArea.setText(group.getGroupArea());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("groupNo", group.getId());
                        bundle.putString("groupName", group.getGroupName());

                        Navigation.findNavController(view)
                                .navigate(R.id.action_groupsFragment_to_groupDetailFragment, bundle);
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return groups.size();
        }
    }

    private class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Advertisement> ads;

        public AdAdapter(Activity activity, List<Advertisement> ads) {
            this.layoutInflater = LayoutInflater.from(activity);
            this.ads = ads;
        }

        private class AdViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivAdPic;

            public AdViewHolder(View itemView) {
                super(itemView);
                ivAdPic = itemView.findViewById(R.id.ivAdPic);
            }
        }

        public void setAds(List<Advertisement> ads) {
            this.ads = ads;
        }

        @Override
        public int getItemCount() {
            if (ads == null) {
                return 0;
            }
            adsSize = ads.size();
            return ads.size();
        }

        @NonNull
        @Override
        public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_ad_show, parent, false);
            return new AdViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
            final Advertisement ad = ads.get(position);
            String url = Common.URL_SERVER + "GroupListServlet";
            int adNo = ad.getAdNo();

            adImageTask = new AdImageTask(url, adNo, holder.ivAdPic);
            adImageTask.execute();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("shopId", ad.getShopId());
                    Navigation.findNavController(view)
                            .navigate(R.id.action_groupsFragment_to_shop_infoFragment, bundle);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (groupGetAllTask != null) {
            groupGetAllTask.cancel(true);
            groupGetAllTask = null;
        }
        if (groupImageTask != null) {
            groupImageTask.cancel(true);
            groupImageTask = null;
        }
    }
}
