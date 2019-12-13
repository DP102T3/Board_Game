package com.example.boardgame.shop;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
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
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.boardgame.shop.Common.bitmap;


public class EditinfoFragment extends Fragment {
    private static final String TAG = "TAG_EditInfoFragment";
    private Activity activity;
    private TextView tvShopId, shopPassword, shopName;
    private EditText shopTel, shopAddress, shopCharge, shopOpen, shopOwner, shopIntro, shopClose;
    private ImageButton shopFristpic;
    private ImageView ivGame;
    private Button btpwchange;
    private Shop shopDB;
    //    private Shop shop;
    private Gson gson;
    private static int hourOpen, minuteOpen, hourClose, minuteClose;
    private TimePickerDialog timePickerDialogOpen;
    private TimePickerDialog timePickerDialogClose;
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

        tvShopId = view.findViewById(R.id.shop_id);
        shopPassword = view.findViewById(R.id.shop_password);
        shopName = view.findViewById(R.id.shop_name);
        shopTel = view.findViewById(R.id.shop_tel);
        shopAddress = view.findViewById(R.id.shop_address);
        shopCharge = view.findViewById(R.id.shop_charge);
        shopOpen = view.findViewById(R.id.shop_open);
        shopClose = view.findViewById(R.id.shop_close);

        showOpen();
        showClose();

        shopOwner = view.findViewById(R.id.shop_Owner);
        shopIntro = view.findViewById(R.id.shop_intro);
        shopFristpic = view.findViewById(R.id.bt_fpic);
        ivGame = view.findViewById(R.id.ivGame);

        Button btedGame = view.findViewById(R.id.btedgame);
        Button btSave = view.findViewById(R.id.btSave);
        btpwchange = view.findViewById(R.id.btpwChange);

        shopOpen.setInputType(InputType.TYPE_NULL);
        shopOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialogOpen = new TimePickerDialog(
                        activity,
                        timeSetListenerOpen,
                        EditinfoFragment.hourOpen, EditinfoFragment.minuteOpen, true);
                timePickerDialogOpen.show();
            }
        });

        shopClose.setInputType(InputType.TYPE_NULL);
        shopClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialogClose = new TimePickerDialog(
                        activity,
                        timeSetListenerClose,
                        EditinfoFragment.hourClose, EditinfoFragment.minuteClose, true);
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
            shopFristpic.setImageBitmap(bitmap);
        } else {
            String url = Common.URL + "SignupServlet";
            String imageId = com.example.boardgame.chat.Common.loadPlayerId(activity);
            int imageSize = getResources().getDisplayMetrics().widthPixels / 100 * 68;
            try {
                ShopImageTask shopImageTask = new ShopImageTask(url, Integer.valueOf(imageId), imageSize);
                bitmap = shopImageTask.execute().get();
                if (bitmap != null) {
                    shopFristpic.setImageBitmap(bitmap);
                } else {
                    shopFristpic.setImageResource(R.drawable.no_image);
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }


        //===================================長按刪除====================================================================================================
        shopFristpic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                bitmap = BitmapFactory.decodeResource(activity.getResources(),
                        R.drawable.no_image);
                ;
                shopFristpic.setImageResource(R.drawable.no_image);
                return true;
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

                    shopDB.setFields(shopId, address, Owner, Tel, Charge, Open, Close, Intro);

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "shopUpdate");
                    jsonObject.addProperty("shop", new Gson().toJson(shopDB));
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    // 有圖才上傳
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        image = out.toByteArray();
                        jsonObject.addProperty("imageBase64", Base64.encodeToString(image, Base64.DEFAULT));
                    }

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

                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
                /* 回前2個Fragment */
                Navigation.findNavController(view).navigate(R.id.action_editinfoFragment_to_shop_infoFragment);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
        getIvGame();
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
            CommonTask shopGetTask = new CommonTask(url, outStr);

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
        int id = shopDB.getShopId();
        String password = shopDB.getShopPassword();
        String shopname = shopDB.getShopName();
        int tel = shopDB.getShopTel();
        String address = shopDB.getShopAddress();
        int charge = shopDB.getShopCharge();
        String open = shopDB.getTimeOpen() != null ? shopDB.getTimeOpen() : "";
        String close = shopDB.getTimeClose() != null ? shopDB.getTimeClose() : "";
        String owner = shopDB.getShopOwner();
        String intro = shopDB.getShopIntro() != null ? shopDB.getShopIntro() : "";

        tvShopId.setText(com.example.boardgame.chat.Common.loadPlayerId(activity));
        shopPassword.setText(password);
        shopName.setText(shopname);
        shopAddress.setText(address);
        shopTel.setText(String.valueOf(tel));
        shopIntro.setText(String.valueOf(intro));
        shopOpen.setText(String.valueOf(open));
        shopClose.setText(String.valueOf(close));
        shopCharge.setText(String.valueOf(charge));
        shopOwner.setTag(String.valueOf(owner));
    }


    private TimePickerDialog.OnTimeSetListener timeSetListenerOpen = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            hourOpen = hour;
            minuteOpen = minute;
            shopOpen.setText(new StringBuilder().append(pad(hourOpen)).append(":").append(pad(minuteOpen)));
        }

    };

    private TimePickerDialog.OnTimeSetListener timeSetListenerClose = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            hourClose = hour;
            minuteClose = minute;
            shopClose.setText(new StringBuilder().append(pad(hourClose)).append(":").append(pad(minuteClose)));
        }
    };

    private void showOpen() {
        Calendar calendar = Calendar.getInstance();
        hourOpen = calendar.get(Calendar.HOUR_OF_DAY);
        minuteOpen = calendar.get(Calendar.MINUTE);
        shopOpen.setText(new StringBuilder().append(pad(hourOpen)).append(":").append(pad(minuteOpen)));
    }

    private void showClose() {
        Calendar calendar = Calendar.getInstance();
        hourClose = calendar.get(Calendar.HOUR_OF_DAY);
        minuteClose = calendar.get(Calendar.MINUTE);
        shopClose.setText(new StringBuilder().append(pad(hourClose)).append(":").append(pad(minuteClose)));
    }


    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + number;
        }
    }

    public void getIvGame() {
        List<Integer> shopGameList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getShopGameList");
            jsonObject.addProperty("shopId", Integer.valueOf(com.example.boardgame.chat.Common.loadPlayerId(activity)));
            String jsonOut = jsonObject.toString();
            CommonTask gameGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = gameGetAllTask.execute().get();
                Type listType = new TypeToken<List<Integer>>() {
                }.getType();
                shopGameList = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }

        String url = Common.URL + "SignupServlet";
        if(shopGameList.size()>0) {
            int gameNo = shopGameList.get(0);
            int imageSize = getResources().getDisplayMetrics().widthPixels / 100 * 68;
            try {
                GameImageTask gameImageTask = new GameImageTask(url, gameNo, imageSize, ivGame);
                gameImageTask.execute();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
