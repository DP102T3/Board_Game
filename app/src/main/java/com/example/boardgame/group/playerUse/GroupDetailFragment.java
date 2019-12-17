package com.example.boardgame.group.playerUse;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.ChatFragment;
import com.example.boardgame.group.Common;
import com.example.boardgame.group.CommonTask;
import com.example.boardgame.group.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;


import static com.example.boardgame.group.Common.rate_count;
import static com.example.boardgame.group.Common.rate_total;


public class GroupDetailFragment extends Fragment {
    private static final String TAG = "TAG_GroupDetailFragment";
    private Activity activity;
    private CommonTask groupDetailTask,groupPeopleTask,getIfSignTask,getIfScoreTask;
    private ImageTask groupDetailImageTask;
    //private List<Group> group;
    private TextView tvShopName, tvArea2, tvArea3, tvGroupDate, tvGroupStarTime, tvGroupStopTime, tvPeople, tvPeopleJoin, tvGameClass, tvGameName, tvBuget, tvStopJoinTime, tvOther,tvPersonRate,tvPlayerId;
    private int groupNo;
    private String groupName;
    private ImageView ivGroupPicture;
    private Button btJoin0rInvite,btReportOrChat,btSignIn;
    int peopleJoin,peopleMax,personCondition;
    double personRate=rate_total/rate_count;
    long groupTime;
    private int shopId;
    private String playerId;


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
        return inflater.inflate(R.layout.fragment_group_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerId = Common.loadPlayerId(activity);

        tvShopName = view.findViewById(R.id.tvPersonName);
        tvArea2 = view.findViewById(R.id.tvArea2);
        tvArea3 = view.findViewById(R.id.tvArea3);
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
        tvPlayerId=view.findViewById(R.id.tvPlayerId);

        ivGroupPicture=view.findViewById(R.id.ivGroupPicture);

        btJoin0rInvite=view.findViewById(R.id.btJoin0rInvite);
        btReportOrChat=view.findViewById(R.id.btReportOrChat);
        btSignIn=view.findViewById(R.id.btSignIn);


        showGroupDetail(groupNo);

        btJoin0rInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btJoin0rInvite.getText().equals("申請加入")){
                    if(peopleJoin<peopleMax){
                        if(personRate<personCondition){
                            Common.showToast(getActivity(), R.string.joinConditionError);
                        }else {
                            if (Common.networkConnected(activity)) {
                                String url = Common.URL_SERVER + "JoinMemberServlet";
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("action", "addMember");
                                jsonObject.addProperty("groupNo", groupNo);
                                jsonObject.addProperty("playerId", playerId);

                                int count = 0;
                                try {
                                    String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                    count = Integer.valueOf(result);
                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }
                                if (count == 0) {
                                    Common.showToast(getActivity(), R.string.textInsertFail);
                                } else {
                                    Common.showToast(getActivity(), R.string.textInsertSuccess);
                                    //tvPeopleJoin.setText("目前參加人數：" + (peopleJoin+1));
                                    showGroupDetail(groupNo);
                                }

                            }else {
                                Common.showToast(getActivity(), R.string.textNoNetwork);

                            }
                        }
                    }else {
                        Common.showToast(getActivity(), R.string.joinMaxError);
                    }

                }else if(btJoin0rInvite.getText().equals("揪好友")){

                }
            }
        });

        btReportOrChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btReportOrChat.getText().equals("團聊")){
                    Bundle bundle = new Bundle();
                    bundle.putString("from", "ListGroups");
                    bundle.putInt("groupNo",groupNo);
                    bundle.putString("groupName", groupName);

                    //@@@@@@@@@@@@@@@@@@@
                    //@@@@要連到團聊頁面@@@
                    //@@@@@@@@@@@@@@@@@@@

//                    Navigation.findNavController(view).navigate(R.id., bundle);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }

    public void showGroupDetail(int groupNoIn) {
        if (activity != null) {
            if (Common.networkConnected(activity)) {
                String url = Common.URL_SERVER + "Group_Servlet";
                String url2 = Common.URL_SERVER + "JoinMemberServlet";
                boolean havePlayerJoin;
                int haveSignIn,haveScore;
                Group groupDetail = null;
                String shopName = "", areaLv2 = "", areaLv3 = "";


                try{
                    //查詢是否入團
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getGroupPeople");
                    jsonObject.addProperty("groupNo", groupNoIn);
                    jsonObject.addProperty("playerId", playerId);
                    Log.d(TAG, "showGroupDetailButton() , playerId = " + playerId);
                    String jsonOut = jsonObject.toString();
                    groupPeopleTask = new CommonTask(url, jsonOut);
                    String jsonIn = groupPeopleTask.execute().get();
                    Log.d(TAG, "showGroupDetailButton() , jsonIn = " + jsonIn);


                    Gson gson = new Gson();
                    JsonObject jsonObject3 = gson.fromJson(jsonIn, JsonObject.class);
                    havePlayerJoin = jsonObject3.get("ifJoin").getAsBoolean();

                    //查詢是否簽到
                    JsonObject jsonObject2 = new JsonObject();
                    jsonObject2.addProperty("action", "getIfSignIn");
                    jsonObject2.addProperty("groupNo", groupNoIn);
                    jsonObject2.addProperty("playerId", playerId);

                    String jsonOut2 = jsonObject2.toString();
                    getIfSignTask = new CommonTask(url2, jsonOut2);
                    String jsonIn2 = getIfSignTask.execute().get();
                    haveSignIn=Integer.valueOf(jsonIn2);

                    //查詢是否評分
                    JsonObject jsonObject4 = new JsonObject();
                    jsonObject4.addProperty("action", "getIfScore");
                    jsonObject4.addProperty("groupNo", groupNoIn);
                    jsonObject4.addProperty("playerId", playerId);
                    String jsonOut4 = jsonObject4.toString();
                    getIfScoreTask= new CommonTask(url2, jsonOut4);
                    String jsonIn4 = getIfScoreTask.execute().get();
                    haveScore=Integer.valueOf(jsonIn4);




                    if(havePlayerJoin==false){
                        Log.d(TAG, "havePlayerJoin==false");
                        btJoin0rInvite.setText("申請加入");
                        btReportOrChat.setText("檢舉");
                        btSignIn.setVisibility(View.GONE);//直接移除此按鈕

                    }else if(havePlayerJoin==true){
                        btJoin0rInvite.setText("揪好友");
                        btReportOrChat.setText("團聊");
                        if(haveSignIn==1){
                            if(haveScore==1){
                                btSignIn.setText("已評分");
                                btSignIn.setVisibility(View.VISIBLE);
                                btSignIn.setEnabled(false);//停用按鈕
                            }else if(haveScore==0){
                                btSignIn.setText("評分");
                                btSignIn.setVisibility(View.VISIBLE);
                                btSignIn.setEnabled(true);//啟用按鈕
                                if(btSignIn.getText().equals("評分")){
                                    btSignIn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("shopId", shopId);
                                            bundle.putInt("groupNo",groupNo);

                                            Navigation.findNavController(view).navigate(R.id.action_groupDetailFragment_to_scoreFragment, bundle);
                                        }
                                    });
                                }
                            }
                        }else {
                            //這裏要改成活動結束前
                            if((groupTime-System.currentTimeMillis())>=900000||System.currentTimeMillis()>=groupTime){
                                btSignIn.setText("簽到");
                                btSignIn.setVisibility(View.VISIBLE);
                                btSignIn.setEnabled(true);//啟用按鈕
                                if(btSignIn.getText().equals("簽到")){
                                    btSignIn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("shopId", shopId);
                                            bundle.putInt("groupNo",groupNo);

                                            Navigation.findNavController(view).navigate(R.id.action_groupDetailFragment_to_sign_in, bundle);
                                        }
                                    });
                                }

                            }else {
                                btSignIn.setText("簽到");
                                btSignIn.setVisibility(View.VISIBLE);
                                btSignIn.setEnabled(false);//停用按鈕
                            }
                        }

                    }


                }catch (Exception e) {
                    Log.e(TAG, "showGroupDetailButton() " + e.toString());
                }


                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "getGroupDetail");
                    jsonObject.addProperty("groupNo", groupNo);
                    String jsonOut = jsonObject.toString();
                    groupDetailTask = new CommonTask(url, jsonOut);
                    String jsonIn = groupDetailTask.execute().get();    // 回傳Group物件、shopName、areaLv2、areaLv3
                    Log.d(TAG, "showGroupDetail() , jsonIn = " + jsonIn);

                    Gson gson = new Gson();
                    JsonObject jsonInObject = gson.fromJson(jsonIn, JsonObject.class);

                    shopName = gson.fromJson(jsonInObject.get("shopName"), String.class);
                    areaLv2 = jsonInObject.get("areaLv2").getAsString();
                    areaLv3 = jsonInObject.get("areaLv3").getAsString();
                    String objectStr = jsonInObject.get("groupDetail").getAsString();

                    groupDetail = gson.fromJson(objectStr, Group.class);

                    groupDetailImageTask=new ImageTask(url,groupNo,ivGroupPicture);
                    groupDetailImageTask.execute();

                } catch (Exception e) {
                    Log.e(TAG, "showGroupDetail() " + e.toString());
                }


                if (groupDetail == null) {
                    Common.showToast(activity, R.string.textNoAreasFound);
                } else {
                    tvShopName.setText(shopName);
                    tvArea2.setText(areaLv2);
                    tvArea3.setText(areaLv3);

                    groupTime=groupDetail.getGroupStartDateTime();
                    Log.e(TAG, "groupTime= " + groupTime);

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
                    peopleMax=groupDetail.getPeopleMax();
                    tvPeopleJoin.setText("目前參加人數：" + groupDetail.getPeopleJoin());
                    peopleJoin=groupDetail.getPeopleJoin();
                    tvGameClass.setText(groupDetail.getGameClass());
                    tvGameName.setText(groupDetail.getGameName());
                    tvPersonRate.setText("評分"+(groupDetail.getPeopleCondition())+"分以上");
                    personCondition=groupDetail.getPeopleCondition();
                    tvBuget.setText(groupDetail.getBudget());
                    Date date4 = new Date(groupDetail.getJoinStopDateTime());
                    String str4 = sdf.format(date4);
                    tvStopJoinTime.setText(str4.substring(0, 16));
                    tvOther.setText(groupDetail.getOtherDescription());
                    tvPlayerId.setText(groupDetail.getGroupPlayerId());

                    //給簽到用
                    shopId=groupDetail.getShopIdSelect();
                    Log.d(TAG,"shopId="+shopId);





                }

            } else {
                Common.showToast(activity, R.string.textNoNetwork);
            }
        }
    }

}
