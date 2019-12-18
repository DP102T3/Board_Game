package com.example.boardgame.player;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.shop.Common;
import com.example.boardgame.shop.ShopImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.boardgame.chat.Common.showToast;

public class Profile3_Fragment extends Fragment {
    private static final String TAG = "TAG_Profile1_Fragment3";
    private Activity activity;
    private String playerId;
    private Gson gson = new Gson();

    private RecyclerView rvFavShop;

    private List<FavShop> shops = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("收藏店家");
        return inflater.inflate(R.layout.fragment_profile3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playerId = com.example.boardgame.player.Common.loadPlayerId(activity);
        rvFavShop = view.findViewById(R.id.rvFavShops);
        rvFavShop.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onStart() {
        super.onStart();
        // 顯示 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
        // 置換 TabBar 的 menu
        MainActivity.setTabBar(MainActivity.TAB_PROFILE);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);

        shops = getFavShop();
        rvFavShop.setAdapter(new FavShopAdapter(activity, shops));
    }

    private class FavShopAdapter extends RecyclerView.Adapter<FavShopAdapter.MyViewHolder> {
        Activity activity;
        List<FavShop> shops;

        public FavShopAdapter(Activity activity, List<FavShop> shops) {
            this.activity = activity;
            this.shops = shops;
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivShop;
            public TextView tvShopName, tvShopRate, tvShopAddress;
            public ImageView ivStarIcon;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                this.ivShop = itemView.findViewById(R.id.ivShop);
                this.tvShopName = itemView.findViewById(R.id.tvShopName);
                this.tvShopRate = itemView.findViewById(R.id.tvShopRate);
                this.tvShopAddress = itemView.findViewById(R.id.tvShopAddress);
                this.ivStarIcon = itemView.findViewById(R.id.ivStarIcon);
            }
        }

        @Override
        public int getItemCount() {
            if (shops.size() == 0) {
                com.example.boardgame.player.Common.showToast(activity, "您沒有收藏店家");
                return 0;
            }
            return shops.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(activity).inflate(R.layout.item_view_shop_list, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final FavShop shop = shops.get(position);
            int rateTotal = shop.getRateTotal();
            int rateCount = shop.getRateCount();

            double rate = 0;
            if (rateCount != 0) {
                rate = rateTotal / rateCount;
                holder.tvShopRate.setText(String.format("%.1f", rate));
            } else {
                holder.ivStarIcon.setVisibility(View.INVISIBLE);
                holder.tvShopRate.setText("暫無評價");
            }

            holder.tvShopName.setText(shop.getShopName());
            holder.tvShopAddress.setText(shop.getShopAddress());

            // 以 shopId 到 Servlet 取圖
            String url = Common.URL + "SignupServlet";
            int imageId = shop.getShopId();
            int imageSize = getResources().getDisplayMetrics().widthPixels / 100 * 68;
            Bitmap bitmap = null;
            try {
                bitmap = new ShopImageTask(url, imageId, imageSize).execute().get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (bitmap != null) {
                holder.ivShop.setImageBitmap(bitmap);
            } else {
                holder.ivShop.setImageResource(R.drawable.no_image);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("shopId", shop.getShopId());
                    Log.d(TAG, String.format("shopId = %d, tvShopName = %s", shop.getShopId(), shop.getShopName()));
                    Navigation.findNavController(v).navigate(R.id.action_profile3_fragment_to_shop_infoFragment, bundle);
                }
            });
        }
    }

    private List<FavShop> getFavShop() {
        List<FavShop> shopsDB = new ArrayList<>();

        // 從 Sevlet 取得要顯示的店家資料
        JsonObject jsonOut = new JsonObject();
        jsonOut.addProperty("action", "getFavShops");
        jsonOut.addProperty("playerId", playerId);

        if (com.example.boardgame.player.Common.networkConnected(activity)) {
            String url = com.example.boardgame.player.Common.SERVLET_URI;
            String jsonIn;
            try {
                jsonIn = new CommonTask(url, jsonOut.toString()).execute().get();
                Type listType = new TypeToken<List<FavShop>>() {
                }.getType();
                shopsDB = gson.fromJson(jsonIn, listType);
                Log.d(TAG, jsonIn);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            showToast(activity, R.string.tx_NoNetwork);
        }

        if (shopsDB == null) {
            shopsDB = new ArrayList<>();
        }
        return shopsDB;
    }
}