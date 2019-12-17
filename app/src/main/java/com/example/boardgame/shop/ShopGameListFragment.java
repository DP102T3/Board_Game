package com.example.boardgame.shop;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ShopGameListFragment extends Fragment {
    private static final String TAG = "TAG_ShopGameListFragment";
    private Activity activity;
    private CommonTask getShopGamesTask;
    private GameImageTask gameImageTask;

    private int shopId;

    private SearchView searchView;
    private RecyclerView rvGames;

    private List<Game> gamesDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_game_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        shopId = bundle.getInt("shopId");

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showGame(gamesDB);
                } else {
                    List<Game> searchShopgame = new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (Game game : gamesDB) {
                        if (game.getGameName().toUpperCase().contains(newText.toUpperCase())) {
                            searchShopgame.add(game);
                        }
                    }
                    showGame(searchShopgame);
                }
                return true;
            }
        });
        // 從 Server 取得遊戲清單，並更新 RecyclerView
        gamesDB = getGames();
        // 設置 RecyclerView 和 Adapter
        rvGames = view.findViewById(R.id.rvGames);
        rvGames.setLayoutManager(new LinearLayoutManager(activity));
        rvGames.getRecycledViewPool().setMaxRecycledViews(0, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 從 Server 取得遊戲清單，並更新 RecyclerView
        gamesDB = getGames();
        showGame(gamesDB);

        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }

    // 從 Server 取得店家遊戲資料，並存入games（List<Game>）
    private List<Game> getGames() {
        List<Game> games = new ArrayList<>();
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getShopGameDetail");
            jsonObject.addProperty("shopId", shopId);
            String jsonOut = jsonObject.toString();
            getShopGamesTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = getShopGamesTask.execute().get();
                Type listType = new TypeToken<List<Game>>() {
                }.getType();
                games = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return games;
    }

    // 自定義方法，刷新 RecyclerView 畫面
    private void showGame(List<Game> gamesDB) {
        if (gamesDB == null || gamesDB.isEmpty()) {
            Common.showToast(activity, R.string.textNoShopGameFound);
            return;
        }

        GameAdapter gameAdapter = (GameAdapter) rvGames.getAdapter();
        if (gameAdapter == null) {
            rvGames.setAdapter(new GameAdapter(activity, gamesDB));
        } else {
            gameAdapter.setGames(gamesDB);
            gameAdapter.notifyDataSetChanged();
        }
    }


    // 定義 Adapter類別
    private class GameAdapter extends RecyclerView.Adapter<GameAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Game> games;
        private int imageSize;

        GameAdapter(Context context, List<Game> games) {
            layoutInflater = LayoutInflater.from(context);
            this.games = games;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setGames(List<Game> games) {
            this.games = games;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivGame, ivChecked;
            TextView tvGameName, tvGameKind;

            MyViewHolder(View itemView) {
                super(itemView);
                ivGame = itemView.findViewById(R.id.ivGame);
                tvGameName = itemView.findViewById(R.id.tvGameName);
                tvGameKind = itemView.findViewById(R.id.tvGameKind);
                ivChecked = itemView.findViewById(R.id.ivChecked);
            }
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "games.size() = " + games.size());
            return games.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.shop_view_game, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
            final Game game = games.get(position);

            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getGameImage");
            jsonObject.addProperty("gameno", game.getGameNo());

            int gameNo = game.getGameNo();

            gameImageTask = new GameImageTask(url, gameNo, imageSize, myViewHolder.ivGame);
            gameImageTask.execute();

            myViewHolder.tvGameName.setText(String.valueOf(game.getGameName()));
            myViewHolder.tvGameKind.setText(game.getGameType());

            // 點擊遊戲
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("game", game);
                    Navigation.findNavController(v).navigate(R.id.action_shopGameListFragment_to_gameDetailFragment, bundle);
                }
            });
        }
    }
}
