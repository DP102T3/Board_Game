package com.example.boardgame.shop;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
    private CommonTask gameGetAllTask;
    private GameImageTask gameImageTask;
    private List<Game> gameDB;
    private Gson gson;

    public static List<Integer> gameChecked;
    public static List<Integer> shopGameList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = getActivity();
        gson = new Gson();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.addbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("請選取欲加入項目");
        return inflater.inflate(R.layout.fragment_gameinfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.searchView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvGame = view.findViewById(R.id.rvShop);


        rvGame.setLayoutManager(new LinearLayoutManager(activity));
        new LinearLayoutManager(getActivity());

        shopGameList = new ArrayList<>();
        shopGameList = getShopGameList();
        gameChecked = new ArrayList<>(shopGameList);

        gameDB = getGames();
        showGameDB(gameDB);
//====================================往下滑刷新======================================================
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showGameDB(gameDB);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

//================================搜尋=====================================================================
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showGameDB(gameDB);
                } else {
                    List<Game> searchShopgame = new ArrayList<>();
                    // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                    for (Game game : gameDB) {
                        if (game.getGamName().toUpperCase().contains(newText.toUpperCase())) {
                            searchShopgame.add(game);
                        }
                    }
                    showGameDB(searchShopgame);
                }
                return true;
            }

        });
    }

    private List<Game> getGames() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAllGame");
            String jsonOut = jsonObject.toString();
            gameGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = gameGetAllTask.execute().get();
                Type listType = new TypeToken<List<Game>>() {
                }.getType();
                gameDB = new Gson().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return gameDB;
    }

    private List<Integer> getShopGameList() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getShopGameList");
            jsonObject.addProperty("shopId", Integer.valueOf(com.example.boardgame.chat.Common.loadPlayerId(activity)));
            String jsonOut = jsonObject.toString();
            gameGetAllTask = new CommonTask(url, jsonOut);
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
        return shopGameList;
    }

    private void showGameDB(List<Game> gameDB) {
        if (gameDB == null || gameDB.isEmpty()) {
            Common.showToast(activity, R.string.textNoShopGameFound);
            return;
        }
//      如果不是空值的話 就會取得Adapter
        ShopDBAdapter gameAdapter = (ShopDBAdapter) rvGame.getAdapter();
//      如果spotAdapter不存在就建立新的(第一次一定沒有Adapter)，否則續用舊有的
        if (gameAdapter == null) {
            rvGame.setAdapter(new ShopDBAdapter(activity, gameDB));
        } else {
//          有新的資料 setGameDB就會把新的傳進去 叫GameDBAdapter重刷
            gameAdapter.setGameDB(gameDB);
//           更新view
            gameAdapter.notifyDataSetChanged();
        }
    }

    private class ShopDBAdapter extends RecyclerView.Adapter<ShopDBAdapter.MyViewHolder> {

        private LayoutInflater layoutInflater;
        private List<Game> gameDB;
        private int imageSize;


        ShopDBAdapter(Context context, List<Game> gameDB) {
            layoutInflater = LayoutInflater.from(context);
            this.gameDB = gameDB;
            /* 螢幕寬度除以4當作將圖的尺寸 */
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setGameDB(List<Game> gameDB) {
            this.gameDB = gameDB;
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
            return gameDB.size();
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.shop_view_game, parent, false);
            return new MyViewHolder(itemView);
        }

        //======================================================抓圖=====================================================================
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
            final Game game = gameDB.get(position);
            if (gameChecked.contains(game.getGameNo())) {
                myViewHolder.ivChecked.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.ivChecked.setVisibility(View.INVISIBLE);
            }

//==================跟server端 要網址 抓id 跟server端說要(imageSize)圖的大小(用Adapter取得螢幕的寬度)=================================
            String url = Common.URL + "SignupServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getGameImage");
            jsonObject.addProperty("gameno", game.getGameNo());

            int gameNo = game.getGameNo();

            gameImageTask = new GameImageTask(url, gameNo, imageSize, myViewHolder.ivGame);
            gameImageTask.execute();

            myViewHolder.tvGameName.setText(String.valueOf(game.getGamName()));
            myViewHolder.tvGameKind.setText(game.getGameType());

//====================================玩家選取遊戲類型========================================================
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 0;
                    if ((index = gameChecked.indexOf(game.getGameNo())) != -1) {
                        gameChecked.remove(index);
                        Log.d(TAG, "gameChecked remove " + game.getGameNo());
                        myViewHolder.ivChecked.setVisibility(View.INVISIBLE);
                    } else {
                        gameChecked.add(game.getGameNo());
                        Log.d(TAG, "gameChecked add " + game.getGameNo());
                        myViewHolder.ivChecked.setVisibility(View.VISIBLE);
                    }
                    Log.d(TAG, "========== before save game ==========");
                    Log.d(TAG, "shopGameList = " + new Gson().toJson(shopGameList));
                    Log.d(TAG, "gameChecked = " + new Gson().toJson(gameChecked));
                }
            });


        }


    }

}