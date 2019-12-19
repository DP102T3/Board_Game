package com.example.boardgame.player;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

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

public class Profile2_Fragment extends Fragment {
    private static final String TAG = "TAG_Profile1_Fragmen2";
    private Activity activity;
    private String playerId;
    private Gson gson = new Gson();
    private CommonTask getGroupsJoinTask;
    private ImageTask getGroupsJoinImageTask;
    private RecyclerView rvGroupsJoin;
    private List<Group> groupsJoin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("我的揪團");
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_profile2, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 顯示 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
        // 置換 TabBar 的 menu
        MainActivity.setTabBar(MainActivity.TAB_PROFILE);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerId=String.valueOf(Common.loadPlayerId(activity));
        rvGroupsJoin=view.findViewById(R.id.rvFriends);
        rvGroupsJoin.setLayoutManager(new LinearLayoutManager(activity));
        groupsJoin= getGroupsJoin();
        showGroupsJoin(groupsJoin);

    }

    private List<Group> getGroupsJoin(){
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/Group_Servlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getGroupsJoin");
            jsonObject.addProperty("playerId", playerId);
            String jsonOut = jsonObject.toString();
            getGroupsJoinTask= new CommonTask(url, jsonOut);
            try {

                String jsonIn = getGroupsJoinTask.execute().get();
                Type listType = new TypeToken<List<Group>>() {
                }.getType();
                groupsJoin= gson.fromJson(jsonIn, listType);

            }catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return groupsJoin;
    }

    private void showGroupsJoin( List<Group> groupsJoin){
        if (groupsJoin == null || groupsJoin.isEmpty()) {
            Common.showToast(activity, R.string.textNogroupsCheckedFound);
            return;
        }
        Profile2_Fragment.GroupJoinAdapter groupJoinAdapter=(Profile2_Fragment.GroupJoinAdapter)rvGroupsJoin.getAdapter();
        if (groupJoinAdapter == null) {
            rvGroupsJoin.setAdapter(new Profile2_Fragment.GroupJoinAdapter(activity, groupsJoin));
        }else {
            groupJoinAdapter.setGroupJoin(groupsJoin);
            groupJoinAdapter.notifyDataSetChanged();//重刷
        }
    }

    class GroupJoinAdapter extends RecyclerView.Adapter<Profile2_Fragment.GroupJoinAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Group> groupsJoin;

        GroupJoinAdapter(Context context, List<Group> groupsJoin) {
            layoutInflater = LayoutInflater.from(context);
            this.groupsJoin = groupsJoin;
        }

        void setGroupJoin(List<Group> groupsJoin) {
            this.groupsJoin = groupsJoin;
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
        public Profile2_Fragment.GroupJoinAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.groupcheck_view, parent, false);
            return new Profile2_Fragment.GroupJoinAdapter.MyViewHolder(itemView);
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onBindViewHolder(@NonNull Profile2_Fragment.GroupJoinAdapter.MyViewHolder holder, int position) {
            final Group group= groupsJoin.get(position);
            String url = Common.URL_SERVER + "GroupListServlet";
            int id = group.getId();

            getGroupsJoinImageTask=new ImageTask(url, id, holder.ivGroup);
            getGroupsJoinImageTask.execute();


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
                            .navigate(R.id.action_profile2_fragment_to_groupDetailFragment, bundle);
                }
            });

        }

        @SuppressLint("LongLogTag")
        @Override
        public int getItemCount() {
            Log.d(TAG,"getItemCount()="+ groupsJoin.size());
            return groupsJoin.size();
        }


    }
}
