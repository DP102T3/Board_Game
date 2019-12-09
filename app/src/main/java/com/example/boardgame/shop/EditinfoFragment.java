package com.example.boardgame.shop;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.boardgame.shop.Common.bitmap;


public class EditinfoFragment extends Fragment {

    private Activity activity;
    private TextView shopId, shopPassword, shopName;
    private EditText shopTel, shopAddress, shopCharge, shopOpen, shopOwner, shopIntro, shopClose;
    private ImageButton shopFristpic;
    private Button btedGame, btSave , btpwchange;
    private CommonTask shopGetTask;
    private Shop shopDB;
//    private Shop shop;
    private Gson gson;
    private static int hourOpen, minuteOpen, hourClose, minuteClose;
    int Id;
    TimePickerDialog timePickerDialogOpen;
    TimePickerDialog timePickerDialogClose;
    private byte[] image;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle("資料修改");
        return inflater.inflate(R.layout.fragment_editinfo, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shopId = view.findViewById(R.id.shop_id);
        shopPassword = view.findViewById(R.id.shop_password);
        shopName = view.findViewById(R.id.shop_name);
        shopTel = view.findViewById(R.id.shop_tel);
        shopAddress = view.findViewById(R.id.shop_address);
        shopCharge = view.findViewById(R.id.shop_charge);
        shopOpen = view.findViewById(R.id.shop_open);
        shopClose = view.findViewById(R.id.shop_close);
        shopOwner = view.findViewById(R.id.shop_Owner);
        shopIntro = view.findViewById(R.id.shop_intro);
        shopFristpic = view.findViewById(R.id.bt_fpic);
        btedGame = view.findViewById(R.id.btedgame);
        btSave = view.findViewById(R.id.btSave);
        btpwchange = view.findViewById(R.id.btpwChange);


        showOpen();

        shopOpen.setInputType(InputType.TYPE_NULL);
        shopOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialogOpen = new TimePickerDialog(
                        getActivity(),
                        timeSetListenerOpen,
                        EditinfoFragment.hourOpen, EditinfoFragment.minuteOpen, false);
                timePickerDialogOpen.show();

            }
        });
        showClose();
        shopClose.setInputType(InputType.TYPE_NULL);
        shopClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialogClose = new TimePickerDialog(
                        getActivity(),
                        timeSetListenerClose,
                        EditinfoFragment.hourClose, EditinfoFragment.minuteClose, false);
                timePickerDialogClose.show();

            }
        });

        //===================================按頭像按鈕跳轉到拍照頁面====================================================================================================

        shopFristpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);

                Navigation.findNavController(view)
                        .navigate(R.id.action_editinfoFragment_to_pictureFragment);
            }
        });

        //===================================按編輯跳轉到選擇遊戲類型頁面====================================================================================================

        btedGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_editinfoFragment_to_gameinfoFragment);
            }
        });
        //===================================取得拍照後的圖片====================================================================================================

        if (bitmap != null) {
            Log.d(TAG, "have");
            shopFristpic.setImageBitmap(bitmap);
        }
        //===================================長按刪除====================================================================================================
        shopFristpic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                bitmap = null;
                shopFristpic.setImageResource(R.drawable.cycle);
                return false;

            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });






        showShop(getShop());

//=================================================資料上傳=====================================================================

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                String address = shopAddress.getText().toString();


                int Tel = Integer.valueOf(shopTel.getText().toString());
                int Charge = Integer.valueOf(shopCharge.getText().toString());
                String Open = shopOpen.getText().toString();
                String Close = shopClose.getText().toString();
                String Owner = shopOwner.getText().toString();
                String Intro = shopIntro.getText().toString();





                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "SignupServlet";

                    int shopId = MainActivity.shop.getShopId();



                    shopDB.setFields(shopId, address, Owner, Tel, Charge ,Open, Close, Intro);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "shopUpdate");
                    jsonObject.addProperty("shop", new Gson().toJson(shopDB));
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    image = out.toByteArray();
                    // 有圖才上傳
                    jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
                    int count = 0;
                    try {
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    if (count == 0) {
                        Common.showToast(activity, R.string.textUpdateFail);
                    } else {
                        Common.showToast(activity, R.string.textUpdateSuccess);
                    }

                }else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
                /* 回前一個Fragment */
               Navigation.findNavController(view)
                       .navigate(R.id.action_editinfoFragment_to_shop_infoFragment);

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_AND_BOTTOM);
    }

    //==================================================選擇時間=====================================================================





    private void showOpen() {
        Calendar calendar = Calendar.getInstance();
        hourOpen = calendar.get(Calendar.HOUR_OF_DAY);
        minuteOpen = calendar.get(Calendar.MINUTE);
        shopOpen.setText(new StringBuilder().append("-")
                .append(" ").append(pad(hourOpen)).append(":")
                .append(pad(minuteOpen)));
    }
    private void showClose() {
        Calendar calendar = Calendar.getInstance();
        hourClose = calendar.get(Calendar.HOUR_OF_DAY);
        minuteClose = calendar.get(Calendar.MINUTE);
        shopClose.setText(new StringBuilder().append("-")
                .append(" ").append(pad(hourClose)).append(":")
                .append(pad(minuteClose)));
    }


    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + String.valueOf(number);
        }
    }

    //===================================連接資料庫==============================================================================================
    private Shop getShop() {
        int id = MainActivity.shop.getShopId();

        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "editget");
            jsonObject.addProperty("shop_id", id);


            String outStr = jsonObject.toString();
            shopGetTask = new CommonTask(url, outStr);

            try {
                String jsonIn = shopGetTask.execute().get();
                shopDB = gson.fromJson(jsonIn, Shop.class);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, "doesn't work");
        }
        //        MainActivity.shop = shopDB;
        return shopDB;

    }


    //===================================取資料庫的資料====================================================================================================

    private void showShop(Shop shopDB) {

        //        int id = shopDB.getShopId() != 0  ? shopDB.getShopId() :  0;
        int id = shopDB.getShopId() != 0 ? shopDB.getShopId() : 0;
        String password = shopDB.getShopPassword() != "" ? shopDB.getShopPassword() : "";
        String shopname = shopDB.getShopName() != "" ? shopDB.getShopName() : "";
        int tel = shopDB.getShopTel() != 0 ? shopDB.getShopTel() : 0;
        String address = shopDB.getShopAddress() != "" ? shopDB.getShopAddress() : "";
        int charge = shopDB.getShopCharge() != 0 ? shopDB.getShopCharge() : 0;
        String open = shopDB.getTimeOpen() != null ? shopDB.getTimeOpen() : "";
        String close = shopDB.getTimeClose() != null ? shopDB.getTimeClose() : "";
        String owner = shopDB.getShopOwner() != "" ? shopDB.getShopOwner() : "";
        String intro = shopDB.getShopIntro() != null ? shopDB.getShopIntro() : "";


        shopPassword.setText(password);
        shopName.setText(shopname);
        shopAddress.setText(address);
        shopTel.setText(String.valueOf(tel));
        shopIntro.setText(String.valueOf(intro == "" ? "" : intro));
        shopOpen.setText(String.valueOf(open));
        shopClose.setText(String.valueOf(close));
        shopCharge.setText(String.valueOf(charge));
        shopOwner.setTag(String.valueOf(owner == "" ? "" : owner));
    }



    TimePickerDialog.OnTimeSetListener timeSetListenerOpen = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            hourOpen = hour;
            minuteOpen = minute;
            shopOpen.setText(hour + ":" + minute);

        }

    };

    TimePickerDialog.OnTimeSetListener timeSetListenerClose = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            hourClose = hour;
            minuteClose = minute;
            shopClose.setText(hour + ":" + minute);

        }
    };

}
