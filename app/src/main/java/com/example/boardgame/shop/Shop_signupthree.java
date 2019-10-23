package com.example.boardgame.shop;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static com.example.boardgame.shop.Common.showToast;


public class Shop_signupthree extends Fragment {

    private final static String TAG = "TAG_Shop_signupThreeFragment";
//    private final static String PREFERENCES_NAME = "preferences";
    private Activity activity;
    private EditText shopName, shopTel, shopAddress, shopMail, shopOwner;
//    private SharedPreferences preferences;
    private Button confirm;
    private TextView tvResult;
//    private Shop shop;
    private Gson gson;
    static Shop shop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        gson = new Gson();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle("註冊");
        return inflater.inflate(R.layout.fragment_shop_signupthree, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shopName = view.findViewById(R.id.shop_Name);
        shopTel = view.findViewById(R.id.shop_tel);
        shopAddress = view.findViewById(R.id.shop_address);
        shopMail = view.findViewById(R.id.shop_Email);
        shopOwner = view.findViewById(R.id.shop_Owner);
        confirm = view.findViewById(R.id.confirm);



// =============================================偏好設定=====================================================================================

        //給偏好設定檔名 會自動幫見一個檔案
//        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                savePreferences();


                String name = shopName.getText().toString().trim();
                String tel = shopTel.getText().toString().trim();
                String address = shopAddress.getText().toString().trim();
                String mail = shopMail.getText().toString().trim();
                String owner = shopOwner.getText().toString().trim();


//                Shop shop = new Shop(name, tel, address, mail, owner);

                Bundle bundle = getArguments();

                shop=(Shop)bundle.getSerializable("shop");

                int shopId = shop.getShopId();
                Log.d("testshopId3", String.valueOf(shopId));

                Bundle bundles = new Bundle();
                bundles.putSerializable("shop", shop);

                shop.setShopName(name);
                shop.setShopTel(Integer.valueOf(tel));
                shop.setShopAddress(address);
                shop.setShopMail(mail);
                shop.setShopOwner(owner);


                boolean invalid = false;
                if (name.isEmpty()) {
                    shopName.setError("請輸入店名");
                    invalid = true;

                }
                if (!mail.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                    shopMail.setError("請填入正確E-MAIL格式");
                    invalid = true;
                }
                if (tel.isEmpty()) {
                    shopTel.setError("請輸入電話");
                    invalid = true;

                }
                if (address.isEmpty()) {
                    shopAddress.setError("請輸入地址");
                    invalid = true;

                }
                if (mail.isEmpty()) {
                    shopMail.setError("請輸入信箱");
                    invalid = true;

                }
                if (owner.isEmpty()) {
                    shopOwner.setError("請輸入負責人");
                    invalid = true;
                } else {
                    if (invalid) {
                        return;
                    }
                }

                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "SignupServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "insert");
                    jsonObject.addProperty("shop", new Gson().toJson(shop));
                    String outStr = jsonObject.toString();
                    CommonTask shopTask = new CommonTask(url, outStr);

                    Log.d(TAG, "outStr : " + outStr);

                    boolean result = true;
                    try {
                        String jsonIn = shopTask.execute().get();

                        Log.d(TAG, "jsonIn : " + jsonIn);

                        JsonObject jsonObjectBack = gson.fromJson(jsonIn, JsonObject.class);
                        result = jsonObjectBack.get("result").getAsBoolean();


                        if (result != true) {
                            showToast(activity, "新增失敗，請重新輸入");
                        } else {
                            Navigation.findNavController(view)
                                    .navigate(R.id.action_shop_signupthree_to_shop_infoFragment, bundles);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }

                }


            }

//            private void savePreferences() {

//=================================================先解bundle?================================================================================
//                Bundle bundle = getArguments();
//
//                if (bundle != null) {
//
//                    //解鎖 getSerializable
//                    shop = (Shop) bundle.getSerializable("shop");
//                    //取得edName
//                    int Id = shop.getShopId();
//                    shipId.setText(String.valueOf(Id));

//==================================================儲存偏好設定===============================================================================
//
//                String shopId = shipId.getText().toString();
//                    preferences.edit()
//                            .putInt("shopId", Integer.parseInt(shopId))
//                            .apply();
//                    Common.showToast(activity, "textPreferencesSaved");



//            }
//            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_AND_BOTTOM);
    }
}


