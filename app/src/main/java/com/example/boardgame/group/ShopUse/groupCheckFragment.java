package com.example.boardgame.group.ShopUse;


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

import static com.example.boardgame.group.Common.shopId;


public class groupCheckFragment extends Fragment {
    private static final String TAG = "TAG_GroupCheckFragment";
    private Activity activity;
    private CommonTask groupCheckTask;
    private ImageTask groupCheckImageTask;
    private RecyclerView rvGroupCheck;
    private List<Group> groupsCheck;
    Gson gson = new Gson();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView( inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_group_check, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvGroupCheck=view.findViewById(R.id.rvGroupsHave);
        rvGroupCheck.setLayoutManager(new LinearLayoutManager(activity));
        groupsCheck=getGroupCheck();
        showGroupCheck(groupsCheck);
    }

    private List<Group> getGroupCheck(){

        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/Group_Servlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getGroupCheck");
            jsonObject.addProperty("shopId", shopId);
            String jsonOut = jsonObject.toString();
            groupCheckTask= new CommonTask(url, jsonOut);
            try {
                String jsonIn = groupCheckTask.execute().get();
                Type listType = new TypeToken<List<Group>>() {
                }.getType();
                groupsCheck= gson.fromJson(jsonIn, listType);

            }catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        Log.d(TAG, "getGroupCheck() return " + gson.toJson(groupsCheck));
        return groupsCheck;
    }


    private void showGroupCheck( List<Group> groupsCheck){
        if (groupsCheck == null || groupsCheck.isEmpty()) {
            Common.showToast(activity, R.string.textNogroupsCheckFound);
            return;
        }
        GroupCheckAdapter groupCheckAdapter=(GroupCheckAdapter)rvGroupCheck.getAdapter();
        if (groupCheckAdapter == null) {
            rvGroupCheck.setAdapter(new GroupCheckAdapter(activity, groupsCheck));
        }else {
            groupCheckAdapter.setGroupCheck(groupsCheck);
            groupCheckAdapter.notifyDataSetChanged();//重刷
        }
    }

    // 定義 Adapter類別
    class GroupCheckAdapter extends RecyclerView.Adapter<GroupCheckAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Group> groupsCheck;
        // Adapter建構式
        GroupCheckAdapter(Context context, List<Group> groupsCheck) {
            layoutInflater = LayoutInflater.from(context);
            this.groupsCheck = groupsCheck;
        }

        // 定義 set方法（放入資料來源）
        void setGroupCheck(List<Group> groupsCheck) {
            this.groupsCheck = groupsCheck;
        }

        // 定義 MyViewHolder類別
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



        // 實作 Adapter類別 的三個方法
        // 方法1
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.groupcheck_view, parent, false);
            return new MyViewHolder(itemView);
        }

        // 方法2
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Group group=groupsCheck.get(position);
            String url = Common.URL_SERVER + "GroupListServlet";
            int id = group.getId();

            groupCheckImageTask=new ImageTask(url, id, holder.ivGroup);
            groupCheckImageTask.execute();


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
                            .navigate(R.id.action_groupCheckFragment_to_group_check_detail_Fragment, bundle);
                }
            });

        }

        // 方法3
        @Override
        public int getItemCount() {
            Log.d(TAG,"getItemCount()="+groupsCheck.size());
            return groupsCheck.size();
        }
    }
}
