package com.example.boardgame.shop;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class BlackListStore extends Fragment {

    private static final String TAG = "TAG_GameinfoFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;
    private RecyclerView rvStore;
    private CommonTask ShopGetAllTask;
    private ShopImageTask ShopImageTask;
    private List<Shop> shopList;
    private Gson gson;
    private Button btNormal;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        gson = new Gson();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("黑名單");
        return inflater.inflate(R.layout.fragment_black_list_store, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvStore = view.findViewById(R.id.rvStore);
        btNormal = view.findViewById(R.id.btNormal);


        btNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_blackListStore_to_storeManagement);

            }
        });


        rvStore.setLayoutManager(new LinearLayoutManager(activity));
        new LinearLayoutManager(getActivity());
        shopList = getShops();
        showShopDB(shopList);


    }

//========shopDB是放文字的資訊 為了避免衝突 所以另外創了一個shopList來判斷shopStatus======================================
    private List<Shop> getShops() {
        List<Shop> shopDB = null;
        List<Shop> shopList = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllShop");
            String jsonOut = jsonObject.toString();
            ShopGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = ShopGetAllTask.execute().get();
                Type listType = new TypeToken<List<Shop>>() {
                }.getType();
                shopDB = new Gson().fromJson(jsonIn, listType);
                for(Shop shop : shopDB) {
                    if (shop.getShopStatus().equals("0")) {  //如果shopStatus=2的話 把該物件放入shopList
                        shopList.add(shop);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return shopList;
    }
//======================為了方便閱讀 把shopDB改成shopList=============================================
    private void showShopDB(List<Shop> shopList) {
        if (shopList == null || shopList.isEmpty()) {
            Common.showToast(activity, R.string.textNoShopFound);
            return;
        }
//      如果不是空值的話 就會取得Adapter
        StoreDBAdapter shopAdapter = (StoreDBAdapter) rvStore.getAdapter();
//      如果spotAdapter不存在就建立新的(第一次一定沒有Adapter)，否則續用舊有的
        if (shopAdapter == null) {
            rvStore.setAdapter(new StoreDBAdapter(activity, shopList));
        } else {

            shopAdapter.setShopList(shopList);

            shopAdapter.notifyDataSetChanged();
        }

    }


    public class StoreDBAdapter extends RecyclerView.Adapter<StoreDBAdapter.MyViewHolder> {

        private List<Shop> shopList;
        private LayoutInflater layoutInflater;
        private int imageSize;


        StoreDBAdapter(Context context, List<Shop> shopList) {
            layoutInflater = LayoutInflater.from(context);
            this.shopList = shopList;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setShopList(List<Shop> shopList) {
            this.shopList = shopList;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivShop, ivStarIcon, ivMapIcon;
            TextView tvShopName, tvShopAddress, tvShopRate;

            MyViewHolder(View itemView) {
                super(itemView);
                ivStarIcon = itemView.findViewById(R.id.ivStarIcon);
                ivMapIcon = itemView.findViewById(R.id.ivMapIcon);
                tvShopName = itemView.findViewById(R.id.tvShopName);
                tvShopAddress = itemView.findViewById(R.id.tvShopAddress);
                ivShop = itemView.findViewById(R.id.ivShop);
                tvShopRate = itemView.findViewById(R.id.tvShopRate);
            }
        }

        @Override
        public int getItemCount() {
            return shopList.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_shop_list, parent, false);
            return new MyViewHolder(itemView);
        }
//======================================================抓圖=====================================================================

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

                final Shop shop = shopList.get(position);

                String url = Common.URL + "SignupServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "getShopImage");

                jsonObject.addProperty("shopId", shop.getShopId());

                int shopId = shop.getShopId();


//==================跟server端 要網址 抓id 跟server端說要(imageSize)圖的大小(用Adapter取得螢幕的寬度)=================================
                ShopImageTask = new ShopImageTask(url, shopId, imageSize, myViewHolder.ivShop);
                ShopImageTask.execute();


                myViewHolder.tvShopName.setText(shop.getShopName());
                myViewHolder.tvShopAddress.setText(shop.getShopAddress());
                myViewHolder.tvShopRate.setText(String.valueOf(shop.getRateTotal()));



        }
    }
}












