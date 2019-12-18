package com.example.boardgame.group.ShopUse;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.group.Common;
import com.example.boardgame.group.CommonTask;
import com.example.boardgame.group.ImageTask;
import com.example.boardgame.group.playerUse.Group;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class groups_have_Fragment extends Fragment {
    private static final String TAG = "TAG_GroupsHaveFragment";
    private Activity activity;
    private CommonTask groupsHaveTask;
    private ImageTask groupsHaveImageTask;
    private RecyclerView rvGroupsHave;
    private List<Group> groupsHave;
    Gson gson = new Gson();

    private int shopId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_groups_have_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shopId = Integer.valueOf(Common.loadPlayerId(activity));
        rvGroupsHave = view.findViewById(R.id.rvGroupsHave);
        rvGroupsHave.setLayoutManager(new LinearLayoutManager(activity));
        groupsHave = getGroupsHave();
        showGroupsHave(groupsHave);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 顯示 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
        // 置換 TabBar 的 menu
        MainActivity.setTabBar(MainActivity.TAB_CHECK_GROUP);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_SHOP);
    }

    private List<Group> getGroupsHave(){
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/Group_Servlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getGroupsHave");
            jsonObject.addProperty("shopId", shopId);
            String jsonOut = jsonObject.toString();
            groupsHaveTask= new CommonTask(url, jsonOut);
            try {
                String jsonIn = groupsHaveTask.execute().get();
                Type listType = new TypeToken<List<Group>>() {
                }.getType();
                groupsHave= gson.fromJson(jsonIn, listType);

            }catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return groupsHave;
    }

    private void showGroupsHave( List<Group> groupsHave){
        if (groupsHave == null || groupsHave.isEmpty()) {
            Common.showToast(activity, R.string.textNogroupsHaveFound);
            return;
        }
        groups_have_Fragment.GroupsHaveAdapter groupsHaveAdapter=(groups_have_Fragment.GroupsHaveAdapter)rvGroupsHave.getAdapter();
        if (groupsHaveAdapter == null) {
            rvGroupsHave.setAdapter(new groups_have_Fragment.GroupsHaveAdapter(activity, groupsHave));
        }else {
            groupsHaveAdapter.setGroupsHave(groupsHave);
            groupsHaveAdapter.notifyDataSetChanged();//重刷
        }
    }

    class GroupsHaveAdapter extends RecyclerView.Adapter<groups_have_Fragment.GroupsHaveAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Group> groupsHave;

        GroupsHaveAdapter(Context context, List<Group> groupsHave) {
            layoutInflater = LayoutInflater.from(context);
            this.groupsHave = groupsHave;
        }

        void setGroupsHave(List<Group> groupsHave) {
            this.groupsHave = groupsHave;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivGroup;
            TextView tvGroupName,tvGroupTime,tvGroupPeopleN;

            MyViewHolder(View itemView) {
                super(itemView);
                ivGroup=itemView.findViewById(R.id.ivGroup);
                tvGroupName=itemView.findViewById(R.id.tvGroupName);
                tvGroupTime=itemView.findViewById(R.id.tvGroupTime);
                tvGroupPeopleN=itemView.findViewById(R.id.tvGroupPeopleN);
            }
        }

        @NonNull
        @Override
        public groups_have_Fragment.GroupsHaveAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.groupcheck_view, parent, false);
            return new groups_have_Fragment.GroupsHaveAdapter.MyViewHolder(itemView);
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onBindViewHolder(@NonNull groups_have_Fragment.GroupsHaveAdapter.MyViewHolder holder, int position) {
            final Group group=groupsHave.get(position);
            String url = Common.URL_SERVER + "GroupListServlet";
            int id = group.getId();

            groupsHaveImageTask=new ImageTask(url, id, holder.ivGroup);
            groupsHaveImageTask.execute();


            holder.tvGroupName.setText(group.getGroupName());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//java.text
            Date date = new Date(group.getGroupStartDateTime());
            String str = sdf.format(date);
            holder.tvGroupTime.setText(str.substring(0, 10));
            holder.tvGroupPeopleN.setText("參加人數："+String.valueOf(group.getPeopleJoin()));

            Log.d(TAG, "holder");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("groupNo", group.getId());
                    bundle.putString("groupName", group.getGroupName());

                    Navigation.findNavController(view)
                            .navigate(R.id.action_groups_have_Fragment_to_group_checked_detail, bundle);
                }
            });

        }

        @SuppressLint("LongLogTag")
        @Override
        public int getItemCount() {
            Log.d(TAG,"getItemCount()="+groupsHave.size());
            return groupsHave.size();
        }


    }
}
