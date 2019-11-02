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

import static com.example.boardgame.MainActivity.SHOP;
import static com.example.boardgame.R.id;
import static com.example.boardgame.R.layout;


public class shop_infoFragment extends Fragment {

    private static final String TAG = "TAG_shop_infoFragment";
    private Activity activity;
    private Gson gson;
    private TextView shopId, shopTel, shopAddress, shopIntro, shopCharge, shopOpen, shopClose;
    private ConstraintLayout infoConstrainLayout;
    private ImageView shopFristpic;
    private CommonTask shopGetTask;
    private Shop shop;
    private Shop shopDB;
    int Id;


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
        shopClose = view.findViewById(id.shop_close);
        shopFristpic = view.findViewById(id.shop_fristpic);
        infoConstrainLayout = view.findViewById(R.id.infoConstrainLayout);
        shopCharge = view.findViewById(R.id.shop_charge);

        Bundle bundle1 = getArguments();
        Shop shop = (Shop)bundle1.getSerializable("shop");
        Id = shop.getShopId();
        Log.d("我要看b1",String.valueOf(Id));
        MainActivity.shop.setShopId(Id);

        Bundle bundles = new Bundle();
        bundles.putSerializable("shop", shop);

//=======================================取得shop物件裡面的東西===========================================================

        showShop(getShop());

    }

    //======================================跟資料庫連結取資料的資訊=============================================================
    private Shop getShop() {
        int id = MainActivity.shop.getShopId();

        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "get");
            jsonObject.addProperty("shop_id", Id);

//            Bundle bundle = getArguments();
//============================================解鎖bundle================================================================
//            if (bundle != null) {
//
//                //解鎖 getSerializable
//                shop = (Shop) bundle.getSerializable("shop");
//                //取得edName
//                Id = shop.getShopId();
//                shopId.setText(String.valueOf(Id));
//                FriendMainActivity.shop.setShopId(Id);
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
//        }
        MainActivity.shop = shopDB;
//        Log.e("123木頭人", FriendMainActivity.shop.getShopAddress());
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
        String address = shopDB.getShopAddress() != "" ? shopDB.getShopAddress() : "";
        int tel = shopDB.getShopTel() != 0 ? shopDB.getShopTel() : 0;
        String intro = shopDB.getShopIntro() != "" ? shopDB.getShopIntro() : "";
        int open = shopDB.getShopOpen() != 0 ? shopDB.getShopOpen() : 0;
        int close = shopDB.getShopClose() != 0 ? shopDB.getShopClose() : 0;
        int charge = shopDB.getShopCharge() != 0 ? shopDB.getShopCharge() : 0 ;


//             秀出值
             shopId.setText(String.valueOf(Id));
//             System.out.println("id : " + id );

        shopAddress.setText(address);
        shopTel.setText(String.valueOf(tel));
        shopIntro.setText(intro);
        shopOpen.setText(String.valueOf(open));
        shopClose.setText(String.valueOf(close));
        shopCharge.setText(String.valueOf(charge));

    }

    @Override
    public void onStart() {
        super.onStart();

        // 隱藏 TabBar
        MainActivity.changeBarsStatus(MainActivity.ONLY_BOTTOM);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_SHOP);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasOptionsMenu(false);
    }
}