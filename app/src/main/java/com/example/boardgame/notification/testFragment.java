package com.example.boardgame.notification;


import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.MainActivity;
import com.example.boardgame.notification.Websocket.AddFriendService;
import com.example.boardgame.notification.Websocket.AdvertisementService;
import com.example.boardgame.notification.Websocket.GroupCheckService;
import com.example.boardgame.notification.Websocket.InviteFriendService;
import com.example.boardgame.notification.Websocket.ReportGroupService;
import com.example.boardgame.notification.Websocket.ReportPlayerService;
import com.example.boardgame.notification.Websocket.ReportShopService;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class testFragment extends Fragment implements View.OnClickListener {
    public Button inviteFriendButton,addFriendButton,reportPlayerButton,reportShopButton,
            inviteGroupButton,groupCheckButton,refuseGroupButton,reportGroupButton,adButton,
            refuseAdButton,playerNosList,shopNosList,sysbutton;
    private Activity activity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_AND_BOTTOM);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inviteFriendButton=view.findViewById(R.id.inviteFriendButton);
        addFriendButton=view.findViewById(R.id.addFriendButton);
        reportPlayerButton=view.findViewById(R.id.reportPlayerButton);
        reportShopButton=view.findViewById(R.id.reportShopButton);
        inviteGroupButton=view.findViewById(R.id.inviteGroupButton);
        groupCheckButton=view.findViewById(R.id.groupCheckButton);
        refuseGroupButton=view.findViewById(R.id.refuseGroupButton);
        reportGroupButton=view.findViewById(R.id.reportGroupButton);
        adButton=view.findViewById(R.id.adButton);
        refuseAdButton=view.findViewById(R.id.refuseAdButton);
        playerNosList=view.findViewById(R.id.playerNosList);
        shopNosList=view.findViewById(R.id.shopNosList);
        sysbutton=view.findViewById(R.id.sysbutton);


        inviteFriendButton.setOnClickListener(this);
        addFriendButton.setOnClickListener(this);
        reportPlayerButton.setOnClickListener(this);
        reportShopButton.setOnClickListener(this);
        inviteGroupButton.setOnClickListener(this);
        groupCheckButton.setOnClickListener(this);
        refuseGroupButton.setOnClickListener(this);
        reportGroupButton.setOnClickListener(this);
        adButton.setOnClickListener(this);
        refuseAdButton.setOnClickListener(this);
        playerNosList.setOnClickListener(this);
        shopNosList.setOnClickListener(this);
        sysbutton.setOnClickListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.inviteFriendButton:  //玩家
                JsonObject inviteFriendJsonObject = new JsonObject();
                inviteFriendJsonObject.addProperty("type", "inviteFriend");
                inviteFriendJsonObject.addProperty("player2_id", "gerfarn0523");
                String inviteFriendJson = new Gson().toJson(inviteFriendJsonObject);
                Log.e("inviteFriendButtonOnclickJson送出值：", inviteFriendJson);
                if(!inviteFriendJson.isEmpty()){
                InviteFriendService.inviteFriendWebSocketClient.send(inviteFriendJson);}
                break;
            case R.id.addFriendButton:  //玩家
                JsonObject addFriendJsonObject = new JsonObject();
                addFriendJsonObject.addProperty("receiver", "chengchi1223");
                String addFriendJson = new Gson().toJson(addFriendJsonObject);
                Log.e("addFriendButtonOnclickJson送出值：", addFriendJson);
                if(!addFriendJson.isEmpty()){
                AddFriendService.addFriendnosWebSocketClient.send(addFriendJson);}
                break;
            case R.id.reportPlayerButton:  //後台
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("player_id", "chengchi1223");
                jsonObject.addProperty("player2_id", "gerfarn0523");
                String idJson = new Gson().toJson(jsonObject);
                Log.e("reportPlayerButtonOnclickJson送出值：", idJson);
                if(!idJson.isEmpty()){
                ReportPlayerService.reportWebSocketClient.send(idJson);}
                break;
            case R.id.reportShopButton:  //後台
                JsonObject reportShopJsonObject = new JsonObject();
                reportShopJsonObject.addProperty("player_id", "chengchi1223");
                reportShopJsonObject.addProperty("shop_id", CommonShop.loadShop_id(activity));
                String shop_idJson = new Gson().toJson(reportShopJsonObject);
                Log.e("reportShopButtonOnclickJson送出值：", shop_idJson);
                if(!shop_idJson.isEmpty()){
                ReportShopService.reportShopWebSocketClient.send(shop_idJson);}
                break;
            case R.id.inviteGroupButton: //玩家
                JsonObject inviteGroupJsonObject = new JsonObject();
                inviteGroupJsonObject.addProperty("type", "inviteGroup");
                inviteGroupJsonObject.addProperty("player2_id", "gerfarn0523");
                inviteGroupJsonObject.addProperty("group_name", "中大桌遊");
                String inviteGroupIdJson = new Gson().toJson(inviteGroupJsonObject);
                Log.e("inviteFriendGroupButtonOnclickJson送出值：", inviteGroupIdJson);
                if(!inviteGroupIdJson.isEmpty()){
                InviteFriendService.inviteFriendWebSocketClient.send(inviteGroupIdJson);}
                break;
            case R.id.groupCheckButton: //店家
                String group_no = String.valueOf(1);
                JsonObject groupCheckJsonObject = new JsonObject();
                groupCheckJsonObject.addProperty("group_no", group_no);
                groupCheckJsonObject.addProperty("group_name", "中大桌遊");
                groupCheckJsonObject.addProperty("type", "agree");
                String groupCheckIdJson = new Gson().toJson(groupCheckJsonObject);
                Log.e("groupCheckButtonOnclickJson送出值：", groupCheckIdJson);
                if(!groupCheckIdJson.isEmpty()){
                GroupCheckService.groupCheckNosWebSocketClient.send(groupCheckIdJson);}
                break;
            case R.id.refuseGroupButton: //店家
                String group_no_refuse = String.valueOf(1);
                JsonObject refuseGroupJsonObject = new JsonObject();
                refuseGroupJsonObject.addProperty("group_no", group_no_refuse);
                refuseGroupJsonObject.addProperty("group_name", "中大桌遊");
                refuseGroupJsonObject.addProperty("type", "當日訂位人數已滿");
                String refuseGroupIdJson = new Gson().toJson(refuseGroupJsonObject);
                Log.e("refuseGroupButtonOnclickJson送出值：", refuseGroupIdJson);
                if(!refuseGroupIdJson.isEmpty()){
                GroupCheckService.groupCheckNosWebSocketClient.send(refuseGroupIdJson);}
                break;
            case R.id.reportGroupButton: //後端
                String reportGroup_group_no = String.valueOf(1);
                JsonObject reportGroupJsonObject = new JsonObject();
                reportGroupJsonObject.addProperty("group_no", reportGroup_group_no);
                reportGroupJsonObject.addProperty("group_name", "中大桌遊");
                reportGroupJsonObject.addProperty("reporter", "gerfarn0523");
                String reportGroupIdJson = new Gson().toJson(reportGroupJsonObject);
                Log.e("reportGroupButtonOnclickJson送出值：", reportGroupIdJson);
                if(!reportGroupIdJson.isEmpty()){
                ReportGroupService.reportGroupNosWebSocketClient.send(reportGroupIdJson);}
                break;
            case R.id.adButton: //店家
                JsonObject adJsonObject = new JsonObject();
                adJsonObject.addProperty("ad_no", 1);
                adJsonObject.addProperty("adTitle", "週年慶");
                adJsonObject.addProperty("refuseReason", "內容有不當資訊請修改");
                adJsonObject.addProperty("type", "agree");
                String adJson = new Gson().toJson(adJsonObject);
                Log.e("adButtonOnclickJson送出值：", adJson);
                if(!adJson.isEmpty()){
                AdvertisementService.advertisementWebSocketClient.send(adJson);}
                break;
            case R.id.refuseAdButton: //店家
                //String ad_no = String.valueOf(1);
                JsonObject refuseAdJsonObject = new JsonObject();
                refuseAdJsonObject.addProperty("ad_no", 1);
                refuseAdJsonObject.addProperty("adTitle", "週年慶");
                refuseAdJsonObject.addProperty("refuseReason", "內容有不當資訊請修改");
                refuseAdJsonObject.addProperty("type", "refuse");
                String refuseAdJson = new Gson().toJson(refuseAdJsonObject);
                Log.e("refuseAdButtonOnclickJson送出值：", refuseAdJson);
                if(!refuseAdJson.isEmpty()){
                AdvertisementService.advertisementWebSocketClient.send(refuseAdJson);}
                break;
            case R.id.playerNosList:
                Navigation.findNavController(view).navigate(R.id.playerNosListFragment);
                break;
            case R.id.shopNosList:
                Navigation.findNavController(view).navigate(R.id.shopNotificationListFragment);
                break;
            case R.id.sysbutton:
                Navigation.findNavController(view).navigate(R.id.systemNotificationFragment);
                break;
        }
    }
}
