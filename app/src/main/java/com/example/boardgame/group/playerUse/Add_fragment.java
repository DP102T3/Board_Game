package com.example.boardgame.group.playerUse;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.boardgame.R;
import com.example.boardgame.group.Common;
import com.example.boardgame.group.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.boardgame.group.Common.bitmap;


public class Add_fragment extends Fragment implements DatePickerDialog.OnDateSetListener ,TimePickerDialog.OnTimeSetListener{
    private FragmentActivity activity;
    private TextView tvGroupName,tvArea,tvShopName,tvDate;
    private EditText etGroupName,etAreaLv2,etAreaLv3,etShopName,etGroupDate,etGroupStartime,etGroupStoptime,etStopDate,etStoptime,etGame,etBudget,etDescription;
    private Button btInsert;
    private ImageButton ibGroupPicture;
    private Uri contentUri;
    private Spinner spPeopleLeast,spPeopleMax,spCondition,spGameClass,spAreaLv2,spAreaLv3,spShopName;
    private String[] peopleLeast={"1","2","3"};
    private String[] peopleMax={"1","2","3"};
    private String[] condition={"不限","評分4分以上","評分3分以上","評分2分以上","評分1分以上"};
    private String[] gameGlass={"派對遊戲","策略遊戲","情境遊戲","戰爭遊戲","抽象遊戲","交換卡片遊戲","兒童遊戲","家庭遊戲"};
    private String[] areaslv2;
    private String[] areaslv3;
    private String[] shopName;
    private int j;
    private byte[] image;

    private int pL,pM,timeSelect,dateSelect,con;
    private static int year, month, day, hour, minute;
    private int gameStartYear,gameStartMonth,gameStartDay,gameStartHour,gameStartMinute;
    private int gameStopHour,gameStopMinute;
    private int stopYear,stopMonth,stopDay,stopHour,stopMinute;
    private long gameStartOfMs,gameStopOfMs,stopOfMs;


    private CommonTask findlv2Task,findlv3Task;
    private String lv2Select,lv3Select,shopNameSelect,gameSelect;

    private final String TAG = "TAG_Add_Fragment";
    private List<Shop> shopList = null;


    private Date dateForGroupStartDateTime;
    private Date dateForGroupStopDateTime;
    private Date dateForJoinStopDateTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.fragment_add_fragment, container, false);
    }






    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        etGroupName=view.findViewById(R.id.etGroupName);
        spAreaLv2=view.findViewById(R.id.spAreaLv2);
        spAreaLv3=view.findViewById(R.id.spAreaLv3);
        spShopName=view.findViewById(R.id.spShopName);
        etGroupDate=view.findViewById(R.id.etGroupDate);
        etGame=view.findViewById(R.id.etGame);
        btInsert=view.findViewById(R.id.btInsert);
        ibGroupPicture=view.findViewById(R.id.ibGroupPicture);
        etGroupStartime=view.findViewById(R.id.etGroupStartime);
        etGroupStoptime=view.findViewById(R.id.etGroupStoptime);
        etStopDate=view.findViewById(R.id.etStopDate);
        etStoptime=view.findViewById(R.id.etStoptime);
        spCondition=view.findViewById(R.id.spCondition);
        spGameClass=view.findViewById(R.id.spGameClass);
        spAreaLv2=view.findViewById(R.id.spAreaLv2);
        etBudget=view.findViewById(R.id.etBudget);
        etDescription=view.findViewById(R.id.etDescription);


        spPeopleLeast=view.findViewById(R.id.spPeopelLeast);
        spPeopleMax=view.findViewById(R.id.spPeopleMax);
        spShopName=view.findViewById(R.id.spShopName);

        findlv2();

        ArrayAdapter<String> arrayList = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, peopleLeast);
        spPeopleLeast.setAdapter(arrayList);


        ArrayAdapter<String> arrayList1 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, peopleMax);
        spPeopleMax.setAdapter(arrayList1);

        ArrayAdapter<String> arrayList2 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, condition);
        spCondition.setAdapter(arrayList2);

        ArrayAdapter<String> arrayList3 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, gameGlass);
        spGameClass.setAdapter(arrayList3);



        spGameClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gameSelect=gameGlass[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        spCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (condition[i]){
                    case "不限":
                        con=0;
                        break;
                    case "評分4分以上":
                        con=4;
                        break;
                    case "評分3分以上":
                        con=3;
                        break;
                    case "評分2分以上":
                        con=2;
                        break;
                    case "評分1分以上":
                        con=1;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spPeopleLeast.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pL=Integer.valueOf(peopleLeast[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Common.showToast(getActivity(), R.string.NotOK);
            }
        });

        spPeopleMax.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pM=Integer.valueOf(peopleMax[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Common.showToast(getActivity(), R.string.NotOK);
            }
        });

        etGroupDate.setInputType(InputType.TYPE_NULL);
        etGroupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c =  Calendar.getInstance();
//                GD=new DatePickerDialog(activity,Add_fragment.this,Add_fragment.year,Add_fragment.month,Add_fragment.day);
                new DatePickerDialog(
                        activity,
                        Add_fragment.this,

                        //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                        // 須設置為系統當下時間
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)).show();
                        //
                        //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//                GD.getDatePicker().setMinDate(c.getTimeInMillis());
                dateSelect=1;
            }
        });

        etGroupStartime.setInputType(InputType.TYPE_NULL);
        etGroupStartime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new TimePickerDialog(
                        getActivity(),
                        Add_fragment.this,
                        Add_fragment.hour, Add_fragment.minute, false)
                        .show();
                timeSelect=1;

            }
        });

        etGroupStoptime.setInputType(InputType.TYPE_NULL);
        etGroupStoptime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(
                        getActivity(),
                        Add_fragment.this,
                        Add_fragment.hour, Add_fragment.minute, false)
                        .show();
                timeSelect=2;
            }
        });

        etStopDate.setInputType(InputType.TYPE_NULL);
        etStopDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c =  Calendar.getInstance();
                new DatePickerDialog(
                        activity,
                        Add_fragment.this,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE))
                        .show();
                dateSelect=2;
            }
        });

        etStoptime.setInputType(InputType.TYPE_NULL);
        etStoptime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(
                        getActivity(),
                        Add_fragment.this,
                        Add_fragment.hour, Add_fragment.minute, false)
                        .show();
                timeSelect=3;
            }
        });


        ibGroupPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_add_fragment_to_pictureFragment);
            }
        });

        if(bitmap !=null) {
            Log.d(TAG,"have");
            ibGroupPicture.setImageBitmap(bitmap);
        }




        spAreaLv2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "AreasServlet";

                    lv2Select=areaslv2[i];
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "findlv3");
                    jsonObject.addProperty("lv2select", lv2Select);

                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        findlv3(result);
                    } catch (Exception e) {
                        Log.e(TAG,"spAreaLv2onItemSelected " + e.toString());
                    }

                } else {
                    Common.showToast(getActivity(), R.string.textNoNetwork);

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spAreaLv3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ShopServlet";
                    lv3Select=areaslv3[i];
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "findShopName");
                    jsonObject.addProperty("lv2select", lv2Select);
                    jsonObject.addProperty("lv3select", lv3Select);

                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        findShopName(result);
                    } catch (Exception e) {
                        Log.e(TAG,"spAreaLv3onItemSelected " + e.toString());
                    }

                }else {
                    Common.showToast(getActivity(), R.string.textNoNetwork);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spShopName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //shopNameSelect=shopName[i];
                j=i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });







        btInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String groupName = etGroupName.getText().toString().trim();



                if (groupName.length() <= 0) {
                    Common.showToast(getActivity(), R.string.NotOK);
                    return;
                }
                String areaLv2 = lv2Select;
                String areaLv3 = lv3Select;
                String shopName=shopNameSelect;
                String gameClass=gameSelect;
                String gameName=etGame.getText().toString().trim();
                //String shopName = etShopName.getText().toString().trim();
                String groupDate=etGroupDate.getText().toString().trim();
                //String groupStartDateTime = etGroupDate.getText().toString().trim()+" "+etGroupStartime.getText().toString().trim();
                //Log.d(TAG, "groupStartDateTime = " + groupStartDateTime);
                long groupStartDateTime= gameStartOfMs;
                long groupStopDateTime=gameStopOfMs;
                //String groupStopDateTime = etGroupDate.getText().toString().trim()+" "+etGroupStoptime.getText().toString().trim();
                int peopleLeast=pL;
                int peopleMax=pM;
                int peopleCondition=con;
                String budget=etBudget.getText().toString().trim();
                long joinStopDateTime=stopOfMs;
                //String joinStopDateTime=etStopDate.getText().toString().trim()+" "+etStoptime.getText().toString().trim();
                String otherDescription=etDescription.getText().toString().trim();

                int shopIdSelect=shopList.get(j).getShopId();
                int peopleJoin=1;
                String playerId=Common.playerId;



//                //字串轉Date
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                try{
//                    dateForGroupStartDateTime = sdf.parse(groupStartDateTime);
//                    dateForGroupStopDateTime=sdf.parse(groupStopDateTime);
//                    dateForJoinStopDateTime=sdf.parse(joinStopDateTime);
//                    Log.d(TAG, " dateForGroupStartDateTime = " + dateForGroupStartDateTime);
//                    Log.d(TAG, " dateForGroupStopDateTime = " + dateForGroupStopDateTime);
//                    Log.d(TAG, " dateForJoinStopDateTime = " + dateForJoinStopDateTime);
//                }catch (Exception e){
//                    Log.e(TAG,  "Date "+e.toString());
//                }





                if (areaLv2.length() <= 0||areaLv3.length() <= 0||groupDate.length() <= 0) {
                    Common.showToast(getActivity(), R.string.NotOK);
                    return;
                }

                if(pM<pL){
                    Common.showToast(getActivity(), R.string.textpMpL);
                    return;
                }





                if(groupStartDateTime<System.currentTimeMillis()){
                    Common.showToast(getActivity(), R.string.timeToEarly);
                    return;
                }

                if(joinStopDateTime<System.currentTimeMillis()){
                    Common.showToast(getActivity(), R.string.joinTimeToEarly);
                    return;
                }

                if(groupStartDateTime<joinStopDateTime){
                    Common.showToast(getActivity(), R.string.joinTimeError);
                    return;
                }




                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "Group_Servlet";

                    Group group=new Group( groupName,  shopIdSelect,  gameClass,  gameName,  groupStartDateTime,  groupStopDateTime,  peopleLeast,  peopleMax,  peopleCondition,  budget,  joinStopDateTime,  otherDescription, peopleJoin, playerId);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "addGroup");
                    jsonObject.addProperty("group", new Gson().toJson(group));


                    if (ibGroupPicture != null) {
                        BitmapToBytes(bitmap);
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
                        //////////////////////////待確認
                        //bitmap=null;
                    }

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
                    }
                } else {
                    Common.showToast(getActivity(), R.string.textNoNetwork);

                }


            }
        });



    }

    public byte[] BitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        image=baos.toByteArray();
        return image;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Add_fragment.year = year;
        Add_fragment.month = month;
        Add_fragment.day = day;
        if(dateSelect==1){
            etGroupDate.setText(new StringBuilder().append(year).append("-")
                    .append(pad(month + 1)).append("-").append(pad(day)));

            gameStartYear=year;
            gameStartMonth=month;
            gameStartDay=day;

            getTimeFromPickerOfGameStart();
        }
        if(dateSelect==2){
            etStopDate.setText(new StringBuilder().append(year).append("-")
                    .append(pad(month + 1)).append("-").append(pad(day)));
            stopYear=year;
            stopMonth=month;
            stopDay=day;

            getTimeFromPickerOfStop();
        }
    }


    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + String.valueOf(number);
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Add_fragment.hour = hour;
        Add_fragment.minute = minute;
        if(timeSelect==1){
            etGroupStartime.setText(new StringBuilder().append(pad(hour)).append(":")
                    .append(pad(minute)));
            gameStartHour=hour;
            gameStartMinute=minute;

            getTimeFromPickerOfGameStart();

        }
        if(timeSelect==2){
            etGroupStoptime.setText(new StringBuilder().append(pad(hour)).append(":")
                    .append(pad(minute)));
            gameStopHour=hour;
            gameStopMinute=minute;
            getTimeFromPickerOfGameStop();
        }
        if(timeSelect==3){
            etStoptime.setText(new StringBuilder().append(pad(hour)).append(":")
                    .append(pad(minute)));
            stopHour=hour;
            stopMinute=minute;
            getTimeFromPickerOfStop();
        }

    }


    private long getTimeFromPickerOfGameStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(gameStartYear,gameStartMonth,gameStartDay,gameStartHour,gameStartMinute);
        gameStartOfMs=calendar.getTimeInMillis();
        return gameStartOfMs;
    }

    private long getTimeFromPickerOfGameStop() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(gameStartYear,gameStartMonth,gameStartDay,gameStopHour,gameStopMinute);
        gameStopOfMs=calendar.getTimeInMillis();
        return gameStopOfMs;
    }

    private long getTimeFromPickerOfStop() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(stopYear,stopMonth,stopDay,stopHour,stopMinute);
        stopOfMs=calendar.getTimeInMillis();
        return stopOfMs;
    }



//    // 將選好的日期時間轉成設定alarm所需的時間格式-毫秒
//    private long getTimeFromPicker() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, month, day, hour, minute);
//        return calendar.getTimeInMillis();
//    }

    private void findlv2(){
        if (activity != null) {
            if (Common.networkConnected(activity)) {
                String url = Common.URL_SERVER + "AreasServlet";
                List<String> lv2List = null;
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "findlv2");
                    String jsonOut = jsonObject.toString();
                    findlv2Task = new CommonTask(url, jsonOut);
                    String jsonIn = findlv2Task.execute().get();
                    Log.d(TAG, "findlv2() , jsonIn = " + jsonIn);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List>() {}.getType();
                    lv2List = gson.fromJson(jsonIn, listType);
                } catch (Exception e) {
                    Log.e(TAG,"findli2() " + e.toString());
                }
                if (lv2List == null || lv2List.isEmpty()) {
                    Common.showToast(activity, R.string.textNoAreasFound);
                } else {
                    areaslv2=new String[lv2List.size()];
                    lv2List.toArray(areaslv2);
                    ArrayAdapter<String> arrayListArealv2 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, areaslv2);
                    spAreaLv2.setAdapter(arrayListArealv2);

                }
            } else {
                Common.showToast(activity, R.string.textNoNetwork);
            }
        }
    }

    private void findlv3(String jsonIn){
        if (activity != null) {
            if (Common.networkConnected(activity)) {
                String url = Common.URL_SERVER + "AreasServlet";
                List<String> lv3List = null;
                try {

                    Log.d(TAG, "findlv3() , jsonIn = " + jsonIn);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List>() {}.getType();
                    lv3List = gson.fromJson(jsonIn, listType);
                } catch (Exception e) {
                    Log.e(TAG,"findli3() " + e.toString());
                }
                if (lv3List == null || lv3List.isEmpty()) {
                    Common.showToast(activity, R.string.textNoAreasFound);
                } else {
                    areaslv3=new String[lv3List.size()];
                    lv3List.toArray(areaslv3);
                    ArrayAdapter<String> arrayListArealv3 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, areaslv3);
                    spAreaLv3.setAdapter(arrayListArealv3);

                }
            } else {
                Common.showToast(activity, R.string.textNoNetwork);
            }
        }
    }

    private void findShopName(String jsonIn){
        if (activity != null) {
            if (Common.networkConnected(activity)) {
                String url = Common.URL_SERVER + "ShopServlet";

                try{
                    Log.d(TAG, "findshopName() , jsonIn = " + jsonIn);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Shop>>() {}.getType();
                    shopList = gson.fromJson(jsonIn, listType);
                }catch (Exception e) {
                    Log.e(TAG,"findli3() " + e.toString());
                }
                if(shopList==null||shopList.isEmpty()){
                    Common.showToast(activity, R.string.textNoAreasFound);
                }else {

                    shopName=new String[shopList.size()];


                    for(int i=0;i<(shopName.length);i++) {
                        shopName[i] = shopList.get(i).getShopName();
                    }
                    Log.d(TAG, "shopName[i] = " + shopName);


                    ArrayAdapter<String> arrayListArealv4 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, shopName);
                    spShopName.setAdapter(arrayListArealv4);
                }
            }
        }

    }
}
