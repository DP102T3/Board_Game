package com.example.boardgame.shop;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.example.boardgame.R.id;
import static com.example.boardgame.R.layout;


public class shop_infoFragment extends Fragment {

    private static final String TAG = "TAG_shop_infoFragment";
    private Activity activity;
    private Gson gson = new Gson();
    private TextView tvShopId, shopTel, shopAddress, shopIntro, shopCharge, shopOpen, shopClose;
    private ConstraintLayout infoConstrainLayout;
    private ImageView shopFristpic;
    private CommonTask shopGetTask;
    private Shop shopDB;
    private Button btMap, btPhoneCall;

    private ImageView shopfristpic;
    int Id;
    private RecyclerView shoppic;
    private SharedPreferences preferences;
    private final static String PREFERENCES_NAME = "preferences";
    private final static String DEFAULT_FILE_NAME = "Id";
    private List<Shop> shopList;
    private ShopImageTask shopImageTask;
    private Bundle bundle;

    private int shopId;
    private String shopName;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MainActivity.loginId == MainActivity.SHOP){
            // 顯示出上層的optionmenu
            setHasOptionsMenu(true);
        }
        activity = getActivity();
    }

    //  跟抓acitionbar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle("店家資訊");

        return inflater.inflate(layout.fragment_shop_info, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bundle = getArguments();

        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        tvShopId = view.findViewById(R.id.shop_id);
        shopTel = view.findViewById(R.id.shop_tel);
        shopAddress = view.findViewById(R.id.shop_address);
        shopIntro = view.findViewById(R.id.shop_intro);
        shopOpen = view.findViewById(R.id.shop_open);
        shopClose = view.findViewById(id.shop_close);
        shopFristpic = view.findViewById(id.shop_fristpic);
        infoConstrainLayout = view.findViewById(R.id.infoConstrainLayout);
        shopCharge = view.findViewById(R.id.shop_charge);
        btMap = view.findViewById(R.id.btmap);
        btPhoneCall = view.findViewById(R.id.btphonecall);
        shopfristpic = view.findViewById(id.shop_fristpic);


//==========================================================Map監聽器========================================================
        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationName = shopAddress.getText().toString().trim();

                Address address = getAddress(locationName);
                if (address == null) {
                    Toast.makeText(activity, R.string.shopAddressLocationNotFound, Toast.LENGTH_SHORT).show(); //如果是空值會說找不到
                    return;
                }
//=======================================抓到Address對googlemap server========================================================
                String uriStr = String.format(Locale.US,
                        "google.streetview:cbll=%f,%f", //用format去抓經緯度 %是抓值(緯度,經度) f是轉成浮點數 (先給緯度 再給經度)
//                      getLatitude()抓第一個%f getLongitude()抓第二個%f
                        address.getLatitude(), address.getLongitude());
//                intent (給他一個動作 給他動作的內容)
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriStr)); //顯示(Action_view) uri(是位置、網址 顯示資訊)
                intent.setPackage("com.google.android.apps.maps"); //指定用哪個app開(手機內建的googlemap id)   intent (給他一個動作 給他動作的內容)
                startActivity(intent); //開啟手機內建的google map
            }
        });
//=======================================撥打電話=============================================================================
        btPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入的电话号码
                String phone = shopTel.getText().toString().trim();
                Intent myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(myIntentDial);

            }
        });




//=======================================取得shop物件裡面的東西================================================================

        showShop(getShop());




    }

    @Override
    public void onStart() {
        super.onStart();


//      從偏好設定黨取出shopid 連結server並顯示shop資料

    }

    private Address getAddress(String locationName) {
        Geocoder geocoder = new Geocoder(activity);
        List<Address> addressList = null;
//==================================利用geocoder.getFromLocationName轉成address物件=============================================
        try {
            addressList = geocoder.getFromLocationName(locationName, 1);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        //如果addresssList是 null or 空的話
        if (addressList == null || addressList.isEmpty()) {
            return null;
        } else {
            return addressList.get(0);
        }
    }


    //======================================跟資料庫連結取資料的資訊=============================================================
    private Shop getShop() {

        if(MainActivity.loginId == MainActivity.SHOP) {
            shopId = Integer.valueOf(com.example.boardgame.chat.Common.loadPlayerId(activity));
        }else if (MainActivity.loginId == MainActivity.PLAYER){
            shopId = bundle.getInt("shopId");
            shopName = bundle.getString("shopName");
        }

        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "get");
            jsonObject.addProperty("shop_id", String.valueOf(shopId));


//======================================抓所有資料跟秀資料？================================================================

            String outStr = jsonObject.toString();
            shopGetTask = new CommonTask(url, outStr);

            try {
                String jsonIn = shopGetTask.execute().get();
//======================================解析shop類別====================================================================
                shopDB = gson.fromJson(jsonIn, Shop.class);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, "doesn't work");
        }
//        }
        MainActivity.shop = shopDB;
        return shopDB;
    }

//========================================利用showShop 取出shopDB裡面的東西===============================================
    private void showShop(Shop shopDB) {

//======================================秀出圖片============================================================================


        String url = Common.URL + "SignupServlet";
        int imageId = shopDB.getShopId();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 100 * 68;
        Bitmap bitmap = null;
        try {
            bitmap = new ShopImageTask(url, imageId, imageSize).execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (bitmap != null) {
            shopFristpic.setImageBitmap(bitmap);
        } else {
            shopFristpic.setImageResource(R.drawable.no_image);
        }


//======================================shops是集合 用for each 解開 抓值==================================================
        int id = shopDB.getShopId() != 0 ? shopDB.getShopId() : 0;
        String address = shopDB.getShopAddress() != "" ? shopDB.getShopAddress() : "";
        int tel = shopDB.getShopTel() != 0 ? shopDB.getShopTel() : 0;
        String intro = shopDB.getShopIntro() != "" ? shopDB.getShopIntro() : "";
        String open = shopDB.getTimeOpen() != "" ? shopDB.getTimeOpen() : "";
        String close = shopDB.getTimeClose() != "" ? shopDB.getTimeClose() : "";
        int charge = shopDB.getShopCharge() != 0 ? shopDB.getShopCharge() : 0 ;


        tvShopId.setText(String.valueOf(id == 0 ? "" : id));
        shopAddress.setText(address);
        shopTel.setText(String.valueOf(tel));
        shopIntro.setText(intro);
        shopOpen.setText(String.valueOf(open));
        shopClose.setText(String.valueOf(close));
        shopCharge.setText(String.valueOf(charge));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasOptionsMenu(false);
    }


}