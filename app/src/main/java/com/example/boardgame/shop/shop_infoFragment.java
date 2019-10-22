package com.example.boardgame.shop;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static com.example.boardgame.R.id;
import static com.example.boardgame.R.layout;


public class shop_infoFragment extends Fragment {

    private static final String TAG = "TAG_shop_infoFragment";
    private Activity activity;
    private Gson gson;
    private TextView shopId, shopTel, shopAddress , shopIntro, shopCharge, shopOpen;
    private ConstraintLayout infoConstrainLayout;
    private ImageView shopFristpic;
    private CommonTask shopGetTask;
    private Shop shop;
    private Shop shopDB;



    @Override
    public void onCreate(Bundle savedInstanceState) {
//          顯示出上層的optionmenu
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        activity = getActivity();
        gson = new Gson();


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

        shopId = view.findViewById(R.id.shop_id);
        shopTel = view.findViewById(R.id.shop_tel);
        shopAddress = view.findViewById(R.id.shop_address);
        shopIntro = view.findViewById(R.id.shop_intro);
        shopOpen = view.findViewById(R.id.shop_open);
        shopFristpic = view.findViewById(id.shop_fristpic);
        infoConstrainLayout = view.findViewById(R.id.infoConstrainLayout);
        shopCharge = view.findViewById(R.id.shop_charge);


//=======================================取得shop物件裡面的東西===========================================================

        showShop(getShop());

    }
//======================================跟資料庫連結取資料的資訊=============================================================
    private Shop getShop() {
        if (Common.networkConnected(activity)) {
            String url = Common.URI + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "get");



            Bundle bundle = getArguments();
//============================================解鎖bundle================================================================
            if (bundle != null) {

                //解鎖 getSerializable
                shop = (Shop) bundle.getSerializable("shop");
                //取得edName
                int text = shop.getShopId();
                shopId.setText(String.valueOf(text));
//                int Tel = shop.getShopTel();
//                shopTel.setText(Tel);
//                String Address = shop.getShopAddress();
//                shopAddress.setText(Address);


//======================================抓所有資料跟秀資料？================================================================

                String outStr = jsonObject.toString();
                shopGetTask = new CommonTask(url, outStr);

                try {
                    String jsonIn = shopGetTask.execute().get();
//                    Type listType = new TypeToken<List<Shop>>() {
//                    }.getType();
//======================================解析shop類別====================================================================
//                    shops = new Gson().fromJson(jsonIn, listType);
                    shopDB = gson.fromJson(jsonIn, Shop.class);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            } else {
                Common.showToast(activity, "doesn't work");
            }
        }
        return shopDB;
    }
//========================================利用showShop 取出shopDB裡面的東西===============================================
    private void showShop(Shop shopDB) {
//        if (shops == null || shops.isEmpty()) {
//            Common.showToast(activity,R.string.textNoShopsFound);
//
//        }else{
//======================================shops是集合 用for each 解開 抓值==================================================
//        int id = shop.getShopId();
        String address = shop.getShopAddress();
        int tel = shop.getShopTel();
        String intro = shop.getShopIntro();
        int open = shop.getShopOpen();
        int charge = shop.getShopCharge();
        double fristpic = shop.getShopFristpic();

//             秀出值
//             shopId.setText(String.valueOf(id));
//             System.out.println("id : " + id );

        shopAddress.setText(address);
        shopTel.setText(String.valueOf(tel));
        shopIntro.setText(String.valueOf(intro));
        shopOpen.setText(String.valueOf(open));
        shopCharge.setText(String.valueOf(charge));
        shopFristpic.setTag(String.valueOf(fristpic));

        }

    @Override
    public void onStart() {
        super.onStart();
        // 隱藏 TabBar
        com.example.boardgame.MainActivity.changeBarsStatus(MainActivity.ONLY_BOTTOM);
    }
}








