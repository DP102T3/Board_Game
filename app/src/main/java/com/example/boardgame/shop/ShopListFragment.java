package com.example.boardgame.shop;

// created by Ryan 2019.12.08

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.boardgame.chat.chat_friend.FriendAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.example.boardgame.chat.Common.loadPlayerId;

public class ShopListFragment extends Fragment {
    private static final String TAG = "TAG_ShopListFragment";

    public Activity activity;
    public String playerId;
    public Gson gson;

    public RecyclerView rvShopList;
    public List<Shop> shops;

    //  變更acition bar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_note, menu);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        // 顯示出上層的optionmenu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvShopList = view.findViewById(R.id.rvShopList);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 設定標題
        activity.setTitle("店家列表");
        // 取得 偏好設定的 playerId
        playerId = loadPlayerId(activity);

        // 顯示 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.ONLY_BOTTOM);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);

        // 取得 shops （from Servlet）
        getShops();

        // 設置 RecyclerView
        rvShopList.setLayoutManager(new LinearLayoutManager(activity));
        rvShopList.getRecycledViewPool().setMaxRecycledViews(0, 0);
        rvShopList.setAdapter(new ShopListAdapter(activity, shops));
        rvShopList.getAdapter().notifyDataSetChanged();
    }

    // 到 Servlet 取得所有 Shop 資料
    private void getShops() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonOut = new JsonObject();
            jsonOut.addProperty("action", "getShopAll");

            shops = new ArrayList<>();
            try {
                String inStr = new CommonTask(url, jsonOut.toString()).execute().get();
                shops = new Gson().fromJson(inStr, new TypeToken<List<Shop>>() {
                }.getType());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (shops == null) {
                Common.showToast(activity, R.string.txNoShop);
            } else {
                Log.d(TAG, "Get shops success !");
            }
        } else {
            Common.showToast(activity, R.string.tx_NoNetwork);
        }
    }

    // 實作 Adapter
    private class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.MyViewHolder> {
        private Activity activity;
        private List<Shop> shops;

        public ShopListAdapter(Activity activity, List<Shop> shops) {
            this.activity = activity;
            this.shops = shops;
        }

        @Override
        public int getItemCount() {
            if (shops == null) {
                return 0;
            }
            return shops.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView ivShop;
            public TextView tvShopName, tvShopRate, tvShopAddress;
            public ImageView ivStarIcon;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.ivShop = itemView.findViewById(R.id.ivShop);
                this.tvShopName = itemView.findViewById(R.id.tvShopName);
                this.tvShopRate = itemView.findViewById(R.id.tvShopRate);
                this.tvShopAddress = itemView.findViewById(R.id.tvShopAddress);
                this.ivStarIcon = itemView.findViewById(R.id.ivStarIcon);
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(activity).inflate(R.layout.item_view_shop_list, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Shop shop = shops.get(position);
            int rateTotal = shop.getRateTotal();
            int rateCount = shop.getRateCount();

            double rate = 0;
            if (rateCount != 0) {
                rate = (double) rateTotal / (double) rateCount;
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
                    // 待加上挑轉次頁 action id
                    Navigation.findNavController(v).navigate(R.id.action_shopListFragment_to_shop_infoFragment, bundle);
                }
            });
        }
    }
}
