package com.example.boardgame.shop;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.boardgame.shop.Common.bitmap;


public class editinfoFragment extends Fragment {

    private Activity activity;
    private TextView shopId, shopPassword, shopName;
    private EditText shopTel, shopAddress, shopCharge, shopOpen, shopOwner, shopIntro;
    private ImageButton shopFristpic;
    private Button btedGame, btSave;
    private CommonTask shopGetTask;
    private Shop shopDB;
    private Shop shop;
    private Gson gson;
    int Id;

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
        shopOpen = view.findViewById(R.id.shop_Open);
        shopOwner = view.findViewById(R.id.shop_Owner);
        shopIntro = view.findViewById(R.id.shop_intro);
        shopFristpic = view.findViewById(R.id.bt_fpic);
        btedGame = view.findViewById(R.id.btedgame);
        btSave = view.findViewById(R.id.btSave);




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

        showShop(getShop());

}

//===================================連接資料庫====================================================================================================
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
                Log.e("yyyy", jsonIn);
                shopDB = gson.fromJson(jsonIn, Shop.class);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, "doesn't work");
        }
        Log.d("我要看",String.valueOf(shopDB.getShopId()));
        return shopDB;

    }

//===================================取資料庫的資料====================================================================================================

    private void showShop(Shop shopDB) {

//        int id = shopDB.getShopId() != 0  ? shopDB.getShopId() :  0;
        int id =shopDB.getShopId() != 0 ? shopDB.getShopId() : 0;
        String password = shopDB.getShopPassword() != "" ? shopDB.getShopPassword() : "";
        String shopname = shopDB.getShopName() != "" ? shopDB.getShopName() : "";
        int tel = shopDB.getShopTel() != 0 ? shopDB.getShopTel() :  0;
        String address = shopDB.getShopAddress() != "" ? shopDB.getShopAddress() : "";
        int charge = shopDB.getShopCharge()!= 0 ? shopDB.getShopCharge() :  0;
        int open = shopDB.getShopOpen() != 0 ? shopDB.getShopOpen() : 0;
        String owner = shopDB.getShopOwner();
        String intro = shopDB.getShopIntro();

        shopId.setText(String.valueOf(id==0?"":id));
        shopPassword.setText(password);
        shopName.setText(shopname);
        shopAddress.setText(address);
        shopTel.setText(String.valueOf(tel));
        shopIntro.setText(String.valueOf(intro));
        shopOpen.setText(String.valueOf(open));
        shopCharge.setText(String.valueOf(charge));
        shopOwner.setTag(String.valueOf(owner));
    }

    @Override
    public void onStart() {
        super.onStart();

        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_AND_BOTTOM);
    }

}
