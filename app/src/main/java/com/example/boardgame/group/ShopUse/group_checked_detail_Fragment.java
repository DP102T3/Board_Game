package com.example.boardgame.group.ShopUse;


import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.group.Common;
import com.example.boardgame.group.CommonTask;
import com.example.boardgame.group.ImageTask;
import com.example.boardgame.group.playerUse.Group;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class group_checked_detail_Fragment extends Fragment {
    private static final String TAG = "TAG_GroupCheckDetailFragment";
    private Activity activity;
    private CommonTask groupCheckedDetailTask;
    private ImageTask groupCheckedDetailImageTask;
    private TextView tvPersonName,  tvGroupDate, tvGroupStarTime, tvGroupStopTime, tvPeople, tvPeopleJoin, tvGameClass, tvGameName, tvBuget, tvStopJoinTime, tvOther,tvPersonRate;
    private int groupNo;
    private String groupName;
    private ImageView ivGroupPicture;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        groupNo = getArguments() != null ? getArguments().getInt("groupNo") : -1;
        groupName = getArguments() != null ? getArguments().getString("groupName") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity.setTitle(groupName);
        return inflater.inflate(R.layout.fragment_group_checked_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvPersonName = view.findViewById(R.id.tvPersonName);
        tvGroupDate = view.findViewById(R.id.tvGroupDate);
        tvGroupStarTime = view.findViewById(R.id.tvGroupStarTime);
        tvGroupStopTime = view.findViewById(R.id.tvGroupStopTime);
        tvPeople = view.findViewById(R.id.tvPeople);
        tvPeopleJoin = view.findViewById(R.id.tvPeopleJoin);
        tvGameClass = view.findViewById(R.id.tvGameClass);
        tvGameName = view.findViewById(R.id.tvGameName);
        tvBuget = view.findViewById(R.id.tvBuget);
        tvStopJoinTime = view.findViewById(R.id.tvStopJoinTime);
        tvOther = view.findViewById(R.id.tvOther);
        tvPersonRate=view.findViewById(R.id.tvPersonRate);

        ivGroupPicture=view.findViewById(R.id.ivGroupPicture);

        showGroupDetail(groupNo);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }

    @SuppressLint("LongLogTag")
    public void showGroupDetail(int groupNo) {
        if (activity != null) {
            if (Common.networkConnected(activity)) {
                String url = Common.URL_SERVER + "Group_Servlet";
                Group groupDetail = null;
                try{
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getGroupDetail");
                    jsonObject.addProperty("groupNo", groupNo);
                    String jsonOut = jsonObject.toString();
                    groupCheckedDetailTask= new CommonTask(url, jsonOut);
                    String jsonIn = groupCheckedDetailTask.execute().get();
                    Log.d(TAG, "showGroupCheckedDetail() , jsonIn = " + jsonIn);

                    Gson gson = new Gson();
                    JsonObject jsonInObject = gson.fromJson(jsonIn, JsonObject.class);

                    String objectStr = jsonInObject.get("groupDetail").getAsString();
                    groupDetail = gson.fromJson(objectStr, Group.class);

                    groupCheckedDetailImageTask=new ImageTask(url,groupNo,ivGroupPicture);
                    groupCheckedDetailImageTask.execute();

                }catch (Exception e) {
                    Log.e(TAG, "showGroupDetailButton() " + e.toString());
                }

                if (groupDetail == null) {
                    Common.showToast(activity, R.string.textNoAreasFound);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//java.text
                    Date date = new Date(groupDetail.getGroupStartDateTime());
                    String str = sdf.format(date);
                    Log.e(TAG, "getGroupStartDateTime " + str);
                    tvGroupDate.setText(str.substring(0, 10));
                    Date date2 = new Date(groupDetail.getGroupStartDateTime());
                    String str2 = sdf.format(date2);
                    tvGroupStarTime.setText(str2.substring(11, 16));
                    Date date3 = new Date(groupDetail.getGroupStopDateTime());
                    String str3 = sdf.format(date3);
                    tvGroupStopTime.setText(str3.substring(11, 16));
                    tvPeople.setText(groupDetail.getPeopleLeast() + "~" + groupDetail.getPeopleMax());
                    tvPeopleJoin.setText("目前參加人數：" + groupDetail.getPeopleJoin());
                    tvGameClass.setText(groupDetail.getGameClass());
                    tvGameName.setText(groupDetail.getGameName());
                    tvPersonRate.setText("評分"+(groupDetail.getPeopleCondition())+"分以上");
                    tvBuget.setText(groupDetail.getBudget());
                    Date date4 = new Date(groupDetail.getJoinStopDateTime());
                    String str4 = sdf.format(date4);
                    tvStopJoinTime.setText(str4.substring(0, 16));
                    tvOther.setText(groupDetail.getOtherDescription());
                    tvPersonName.setText(groupDetail.getGroupPlayerId());
                }
            }else {
                Common.showToast(activity, R.string.textNoNetwork);
            }
        }
    }
}
