package com.example.boardgame.group.ShopUse;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.R;
import com.example.boardgame.group.Common;
import com.example.boardgame.group.CommonTask;
import com.example.boardgame.group.ImageTask;
import com.example.boardgame.group.playerUse.Group;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class group_check_detail_Fragment extends Fragment {
    private static final String TAG = "TAG_GroupCheckDetailFragment";
    private Activity activity;
    private CommonTask groupCheckDetailTask;
    private ImageTask groupCheckDetailImageTask;
    private TextView tvPersonName,  tvGroupDate, tvGroupStarTime, tvGroupStopTime, tvPeople, tvPeopleJoin, tvGameClass, tvGameName, tvBuget, tvStopJoinTime, tvOther,tvPersonRate;
    private int groupNo;
    private String groupName;
    private ImageView ivGroupPicture;
    private Button btAccept,btReject;
    private Spinner spRejectReason;
    private String[] rejectReason={"","已客滿","當天店休"};
    private String reasonSelect;


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
        return inflater.inflate(R.layout.fragment_group_check_detail_, container, false);
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
        spRejectReason=view.findViewById(R.id.spRejectReason);
        btAccept=view.findViewById(R.id.btAccept);
        btReject=view.findViewById(R.id.btReject);

        showGroupDetail(groupNo);

        btAccept.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                //接受揪團
                if (activity != null) {
                    if (Common.networkConnected(activity)) {
                        String url = Common.URL_SERVER + "Group_Servlet";
                        try{
                            JsonObject jsonObject = new JsonObject();
                            jsonObject.addProperty("action", "acceptGroup");
                            jsonObject.addProperty("groupNo", groupNo);


                            int count = 0;
                            try {
                                String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                count = Integer.valueOf(result);

                            }catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                            if (count == 0) {
                                Common.showToast(getActivity(), R.string.textInsertFail);

                            }else {
                                Common.showToast(getActivity(), R.string.textInsertSuccess);
                                Navigation.findNavController(btAccept).popBackStack();
                            }
                            //Navigation.findNavController(btAccept).popBackStack();

                        }catch (Exception e) {
                            Log.e(TAG, "showGroupCheckDetail " + e.toString());
                        }
                    }else {
                        Common.showToast(getActivity(), R.string.textNoNetwork);

                    }

                }

            }
        });

        ArrayAdapter<String> arrayList = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, rejectReason);
        spRejectReason.setAdapter(arrayList);

        spRejectReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reasonSelect=rejectReason[i];
                if(reasonSelect.equals("")){
                    btReject.setEnabled(false);//停用按鈕
                    return;
                }else{
                    btReject.setEnabled(true);
                    //拒絕揪團
                    btReject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //拒絕揪團
                            if (activity != null) {
                                if (Common.networkConnected(activity)) {
                                    String url = Common.URL_SERVER + "Group_Servlet";
                                    try{
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("action", "rejectGroup");
                                        jsonObject.addProperty("groupNo", groupNo);


                                        int count = 0;
                                        try {
                                            String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                            count = Integer.valueOf(result);

                                        }catch (Exception e) {
                                            Log.e(TAG, e.toString());
                                        }
                                        if (count == 0) {
                                            Common.showToast(getActivity(), R.string.textInsertFail);

                                        }else {
                                            Common.showToast(getActivity(), R.string.textInsertSuccess);
                                            Navigation.findNavController(btAccept).popBackStack();
                                        }
                                        //Navigation.findNavController(btReject).popBackStack();

                                    }catch (Exception e) {
                                        Log.e(TAG, "showGroupCheckDetail " + e.toString());
                                    }

                                }else {
                                    Common.showToast(getActivity(), R.string.textNoNetwork);
                                }
                            }

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                btReject.setEnabled(false);//停用按鈕
            }
        });

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
                    groupCheckDetailTask= new CommonTask(url, jsonOut);
                    String jsonIn = groupCheckDetailTask.execute().get();
                    Log.d(TAG, "showGroupCheckDetail() , jsonIn = " + jsonIn);

                    Gson gson = new Gson();
                    JsonObject jsonInObject = gson.fromJson(jsonIn, JsonObject.class);

                    String objectStr = jsonInObject.get("groupDetail").getAsString();
                    groupDetail = gson.fromJson(objectStr, Group.class);

                    groupCheckDetailImageTask=new ImageTask(url,groupNo,ivGroupPicture);
                    groupCheckDetailImageTask.execute();



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
