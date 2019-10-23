package com.example.boardgame.shop;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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


public class GameinfoFragment extends Fragment {

    private static final String TAG = "TAG_GameinfoFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;
    private RecyclerView rvGame;
    private CommonTask GameGetAllTask;
    private CommonTask spotGetAllTask;
    private GameImageTask gameImageTask;
    private List<Shop> shopGDB;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gameinfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.searchView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvGame = view.findViewById(R.id.rvGame);

        rvGame.setLayoutManager(new LinearLayoutManager(activity));
        shopGDB = getShops();
        showShopGDB(shopGDB);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showShopGDB(shopGDB);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

//================================搜尋=====================================================================
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                if (newText.isEmpty()) {
                    showShopGDB(shopGDB);
                } else {
                    List<Shop> searchShopgame = new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (Shop shop : shopGDB) {
                        if (shop.getGameName().toUpperCase().contains(newText.toUpperCase())) {
                            searchShopgame.add(shop);
                        }
                    }
                    showShopGDB(searchShopgame);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });
    }

    private List<Shop> getShops() {
        List<Shop> shopGDB = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllGame");
            String jsonOut = jsonObject.toString();
            GameGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = GameGetAllTask.execute().get();
                Type listType = new TypeToken<List<Shop>>() {
                }.getType();
                shopGDB = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return shopGDB;
    }

    private void showShopGDB(List<Shop> shopGDB) {
        if (shopGDB == null || shopGDB.isEmpty()) {
            Common.showToast(activity, R.string.textNoShopGameFound);
            return;
        }
        ShopGDBAdapter spotAdapter = (ShopGDBAdapter) rvGame.getAdapter();
        // 如果spotAdapter不存在就建立新的，否則續用舊有的
        if (spotAdapter == null) {
            rvGame.setAdapter(new ShopGDBAdapter(activity, shopGDB));
        } else {
            spotAdapter.setShopGDB(shopGDB);
//           更新view
            spotAdapter.notifyDataSetChanged();
        }
    }

    private class ShopGDBAdapter extends RecyclerView.Adapter<ShopGDBAdapter.MyViewHolder> {

        private LayoutInflater layoutInflater;
        private List<Shop> shopGDB;
        private int imageSize;

        ShopGDBAdapter(Context context, List<Shop> shopGDB) {
            layoutInflater = LayoutInflater.from(context);
            this.shopGDB = shopGDB;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setShopGDB(List<Shop> shopGDB) {
            this.shopGDB = shopGDB;
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvGame, tvkind;

            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivGame);
                tvGame = itemView.findViewById(R.id.tvGame);
                tvkind = itemView.findViewById(R.id.tvkind);

            }
        }
        @Override
        public int getItemCount(){return shopGDB.size();}


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.shop_view_game, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Shop shop = shopGDB.get(position);
            String url = Common.URL + "ShopServlet";
            int id = shop.getShopId();


        }


    }
}