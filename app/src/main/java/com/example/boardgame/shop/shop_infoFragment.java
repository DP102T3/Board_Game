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
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.boardgame.R.id;
import static com.example.boardgame.R.layout;


public class shop_infoFragment extends Fragment {
    private static final String TAG = "TAG_shop_infoFragment";
    private Activity activity;
    private Gson gson = new Gson();
    private TextView textView10, tvFav, tvShopId, shopTel, shopAddress, shopIntro, shopCharge, shopOpen, shopClose, textView26;
    private ConstraintLayout infoConstrainLayout;
    private ImageView shopFirstpic, ivGame;
    private CommonTask shopGetTask;
    private Shop shopDB;
    private Button btFav, btMap, btPhoneCall, btmore;

    private SharedPreferences preferences;
    private final static String PREFERENCES_NAME = "preferences";
    private ShopImageTask shopImageTask;
    private Bundle bundle;

    private int shopId;
    private String shopName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MainActivity.loginId == MainActivity.SHOP) {
            MainActivity.actionBar.show();
            // 顯示出上層的optionmenu
            setHasOptionsMenu(true);
        }
        activity = getActivity();
    }

    //  跟抓acitionbar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(layout.fragment_shop_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bundle = getArguments();
        textView10 = view.findViewById(R.id.textView10);
        tvFav = view.findViewById(R.id.tvFav);
        btFav = view.findViewById(R.id.btFav);
        btFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObject jsonObject = new JsonObject();
                String btFavPic = (String) btFav.getTag();
                if (btFavPic.equals("heart027")) {
                    btFav.setBackgroundResource(R.drawable.heart026);
                    btFav.setTag("heart026");
                    // delete
                    jsonObject.addProperty("action", "deleteFavShop");
                } else {
                    btFav.setBackgroundResource(R.drawable.heart027);
                    btFav.setTag("heart027");
                    // insert
                    jsonObject.addProperty("action", "insertFavShop");
                }
                int result=0;
                if (Common.networkConnected(activity)) {
                    String url = Common.URL + "SignupServlet";
                    jsonObject.addProperty("playerId", com.example.boardgame.chat.Common.loadPlayerId(activity));
                    jsonObject.addProperty("shopId", shopId);
                    CommonTask gameGetAllTask = new CommonTask(url, jsonObject.toString());
                    try {
                        String jsonIn = gameGetAllTask.execute().get();
                        if("".equals(jsonIn)){
                            jsonIn ="0";
                        }
                        result = Integer.valueOf(jsonIn);
                        if(result==0){
                            Common.showToast(activity, "Insert / Delete ERROR !");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                } else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }

            }
        });

        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        tvShopId = view.findViewById(R.id.shop_id);
        shopTel = view.findViewById(R.id.shop_tel);
        shopAddress = view.findViewById(R.id.shop_address);
        shopIntro = view.findViewById(R.id.shop_intro);
        shopOpen = view.findViewById(R.id.shop_open);
        shopClose = view.findViewById(id.shop_close);
        shopFirstpic = view.findViewById(id.shopFirstpic);
        ivGame = view.findViewById(R.id.ivGame);
        infoConstrainLayout = view.findViewById(R.id.infoConstrainLayout);
        shopCharge = view.findViewById(R.id.shop_charge);
        btMap = view.findViewById(R.id.btmap);
        btPhoneCall = view.findViewById(R.id.btphonecall);
        textView26 = view.findViewById(R.id.textView26);

        btmore = view.findViewById(id.btmore);
        btmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("shopId", shopId);
                Navigation.findNavController(v).navigate(R.id.action_shop_infoFragment_to_shopGameListFragment, bundle);
            }
        });

//==========================================================Map監聽器========================================================
        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationName = shopAddress.getText().toString().trim();

                Log.d(TAG, "locationName = " + locationName);

                Address address = getAddress(locationName);
                if (address == null) {
                    Toast.makeText(activity, R.string.shopAddressLocationNotFound, Toast.LENGTH_SHORT).show(); //如果是空值會說找不到
                    return;
                }

                Log.d(TAG, "address = " + address);

//=======================================抓到Address對googlemap server========================================================
                // 用format去抓經緯度 %是抓值(緯度,經度) f是轉成浮點數 (先給緯度 再給經度)
                // getLatitude()抓第一個%f getLongitude()抓第二個%f
//                String uriStr = String.format(Locale.getDefault(), "google.streetview:cbll=%f,%f", address.getLatitude(), address.getLongitude());
                String uriStr = String.format("geo:0,0?q=%f,%f(%s)", address.getLatitude(), address.getLongitude(), shopDB.getShopName());

                Log.d(TAG, "Latitude = " + address.getLatitude());
                Log.d(TAG, "Longitude = " + address.getLongitude());

                // intent (給他一個動作 給他動作的內容)
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriStr)); //顯示(Action_view) uri(是位置、網址 顯示資訊)
                intent.setPackage("com.google.android.apps.maps"); //指定用哪個app開(手機內建的googlemap id)   intent (給他一個動作 給他動作的內容)
                startActivity(intent); //開啟手機內建的google map
            }
        });
//=======================================撥打電話=============================================================================
        btPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 獲取輸入的電話號碼
                String phone = shopTel.getText().toString().trim();
                Intent myIntentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(myIntentDial);
            }
        });

//=======================================取得shop物件裡面的東西================================================================

        showShop(getShop());
        getIvGame();
        activity.setTitle(shopDB.getShopName());
    }

    @Override
    public void onStart() {
        super.onStart();
        ConstraintSet constraintSet = new ConstraintSet();
        if (MainActivity.loginId == MainActivity.PLAYER) {
            // 顯示 TabBar 及 BottomBar
            MainActivity.changeBarsStatus(MainActivity.ONLY_BOTTOM);
            // 置換 BottomBar 的 menu
            MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);

            int result = checkFavShop();
            if (result != 0) {
                // 將btFav改成實心愛心
                btFav.setBackgroundResource(R.drawable.heart027);
                btFav.setTag("heart027");
            } else {
                // 將btFav改成空心愛心
                btFav.setBackgroundResource(R.drawable.heart026);
                btFav.setTag("heart026");
            }
        } else if (MainActivity.loginId == MainActivity.SHOP) {
            // 顯示 TabBar 及 BottomBar
            MainActivity.changeBarsStatus(MainActivity.ONLY_BOTTOM);
            // 置換 BottomBar 的 menu
            MainActivity.setBottomBar(MainActivity.BOTTOM_SHOP);

            // 移除收藏按鈕
            tvFav.setVisibility(View.GONE);
            btFav.setVisibility(View.GONE);

            // 使 ScrollView 的 Constraint 符合沒有 TabBar 和 BottomBar 的畫面
            constraintSet.clone(infoConstrainLayout);
            constraintSet.connect(R.id.textView10, ConstraintSet.TOP, R.id.shopFirstpic, ConstraintSet.BOTTOM, 32);
            constraintSet.applyTo(infoConstrainLayout);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.actionBar.show();
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
        Log.d(TAG, "getShop()");

        if (MainActivity.loginId == MainActivity.SHOP) {
            shopId = Integer.valueOf(com.example.boardgame.chat.Common.loadPlayerId(activity));

        } else if (MainActivity.loginId == MainActivity.PLAYER) {
            shopId = bundle.getInt("shopId");
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
        Log.d(TAG, "showShop()");

//======================================秀出圖片============================================================================


        String url = Common.URL + "SignupServlet";
        int imageId = shopDB.getShopId();
        int imageSize = getResources().getDisplayMetrics().widthPixels / 100 * 68;
        Bitmap bitmap = null;
        try {
            shopImageTask = new ShopImageTask(url, imageId, imageSize, shopFirstpic);
            shopImageTask.execute();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }


//======================================shops是集合 用for each 解開 抓值==================================================
        int id = shopDB.getShopId();
        String address = shopDB.getShopAddress();
        int tel = shopDB.getShopTel();
        String intro = shopDB.getShopIntro();
        String open = shopDB.getTimeOpen();
        String close = shopDB.getTimeClose();
        String charge = shopDB.getShopCharge();

        tvShopId.setText(String.valueOf(id == 0 ? "" : id));
        shopAddress.setText(address);
        shopTel.setText(tel!=0?"0"+tel:"");
        shopIntro.setText(intro);
        shopOpen.setText(open != null ? open : "");
        shopClose.setText(close != null ? close : "");
        shopCharge.setText(charge);

        if (open == null && close == null) {
            textView26.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasOptionsMenu(false);
    }

    public void getIvGame() {
        List<Integer> shopGameList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getShopGameList");
            jsonObject.addProperty("shopId", shopId);
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
        if (shopGameList.size() > 0) {
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

    private int checkFavShop() {
        int result = 0;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "checkFavShop");
            jsonObject.addProperty("playerId", com.example.boardgame.chat.Common.loadPlayerId(activity));
            jsonObject.addProperty("shopId", shopId);
            String jsonOut = jsonObject.toString();
            CommonTask gameGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = gameGetAllTask.execute().get();
                if("".equals(jsonIn)){
                    jsonIn ="0";
                }
                result = Integer.valueOf(jsonIn);
                return result;
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return 0;
    }
}