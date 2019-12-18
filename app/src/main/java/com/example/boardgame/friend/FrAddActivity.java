package com.example.boardgame.friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.Common;
import com.example.boardgame.notification.Websocket.InviteFriendService;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class FrAddActivity extends AppCompatActivity {
    private static final String TAG = "TAG_FrAddActivity";

    //可搜尋好友ID及選擇掃描QR Code頁面

    private RecyclerView rvAddFriend;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    private TextView tvIndicate;
    List<FriendViewModel> friends = new ArrayList<>();
    FriendSearchAdapter friendSearchAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fr_add);
        tvIndicate = findViewById(R.id.tvIndicate);
        rvAddFriend = findViewById(R.id.rvAddFriend);
        rvAddFriend.setLayoutManager(new LinearLayoutManager(this));
        friends = new ArrayList<>();
        friendSearchAdapter = new FriendSearchAdapter(this, friends);
        rvAddFriend.setAdapter(friendSearchAdapter);
    }

    private class FriendSearchAdapter extends RecyclerView.Adapter<MyViewHolder> {
        Context context;
        List<FriendViewModel> friendViewModelList;

        public FriendSearchAdapter(FrAddActivity frAddActivity, List<FriendViewModel> friendViewModel) {
            this.context = frAddActivity;
            this.friendViewModelList = friendViewModel;
        }

        public void setFriends(List<FriendViewModel> searchFriends) {
            this.friendViewModelList = searchFriends;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.itemview_scan_result, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int index) {
            final FriendViewModel friendViewModel = friendViewModelList.get(index);
            holder.tvName.setText(friendViewModel.getFrNkName());
            if (friendViewModel.getFrPic() != null) {
                byte[] image = Base64.decode(friendViewModel.getFrPic(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                holder.ivFriend.setImageBitmap(bitmap);
            }

            holder.btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//        ===== 有修改 =====
                    Gson gson = new Gson();

                    Friend friend = new Friend(Common.loadPlayerId(FrAddActivity.this), friendViewModel.getFrID(), friendViewModel.getPointCount(), 1);
                    String jsonOut = gson.toJson(friend);

                    MyTask task = new MyTask("http://10.0.2.2:8080/Advertisement_Server/CreateFriend", jsonOut);
//        ===============
                    try {
                        int result = Integer.valueOf(task.execute().get());
                        if (result != 0) {
                            Common.showToast(FrAddActivity.this, "已送出邀請");
                            FriendSearchAdapter adapter = (FriendSearchAdapter) rvAddFriend.getAdapter();
                            if (adapter != null) {
                                friends.clear();
                                adapter.setFriends(friends);
                                adapter.notifyDataSetChanged();
                                rvAddFriend.setVisibility(View.GONE);
                                tvIndicate.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d(TAG, "新增好友失敗，請重新開啟 App");
                        }
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }

                    JsonObject inviteFriendJsonObject = new JsonObject();
                    inviteFriendJsonObject.addProperty("type", "inviteFriend");
                    inviteFriendJsonObject.addProperty("player2_id", "gerfarn0523");
                    String inviteFriendJson = new Gson().toJson(inviteFriendJsonObject);
                    if(!inviteFriendJson.isEmpty()){
                        InviteFriendService.inviteFriendWebSocketClient.send(inviteFriendJson);}
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendViewModelList.size();
        }

    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFriend;
        TextView tvName;
        Button btnInvite;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFriend = itemView.findViewById(R.id.ivFriend);
            tvName = itemView.findViewById(R.id.tvName);
            btnInvite = itemView.findViewById(R.id.btnInvite);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_qrcode, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

            // 監聽 searchView 文字輸入事件
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    FriendSearchAdapter adapter = (FriendSearchAdapter) rvAddFriend.getAdapter();
                    if (adapter != null) {
                        friends.clear();
                        adapter.setFriends(friends);
                        adapter.notifyDataSetChanged();
                        rvAddFriend.setVisibility(View.GONE);
                        tvIndicate.setVisibility(View.VISIBLE);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String newText) {
                    FriendSearchAdapter adapter = (FriendSearchAdapter) rvAddFriend.getAdapter();
                    if (adapter != null) {
                        friends = searchNew(newText);
                        adapter.setFriends(friends);
                        adapter.notifyDataSetChanged();
                        if (friends.size() != 0) {
                            rvAddFriend.setVisibility(View.VISIBLE);
                            tvIndicate.setVisibility(View.GONE);
                        } else {
                            rvAddFriend.setVisibility(View.GONE);
                            tvIndicate.setVisibility(View.VISIBLE);
                            Common.showToast(FrAddActivity.this, "查無此 ID ，請重新輸入！");
                        }
                    }
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);

            // 監聽 searchView 點擊事件
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (friends.size() == 0) {
                        rvAddFriend.setVisibility(View.GONE);
                        tvIndicate.setVisibility(View.VISIBLE);
                    } else {
                        rvAddFriend.setVisibility(View.VISIBLE);
                        tvIndicate.setVisibility(View.GONE);
                    }
                }
            });

            // 監聽 searchView 關閉事件
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    if (friends.size() == 0) {
                        rvAddFriend.setVisibility(View.GONE);
                        tvIndicate.setVisibility(View.VISIBLE);
                    } else {
                        rvAddFriend.setVisibility(View.VISIBLE);
                        tvIndicate.setVisibility(View.GONE);
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Log.i("InF", String.valueOf(id));

        switch (id) {
            case R.id.action_search:
                // Not implemented here
                return false;

            case R.id.action_qrcode:
                Intent intent = new Intent(this, QRChooseActivity.class);
                startActivity(intent);
                break;

            case R.id.action_note:
//          TODO 設定 action!!!!!!!!!!!!!!!!!!!!!!!
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }

    public List<FriendViewModel> searchNew(String searchId) {
        List<Friend> newPlayers;
        Friend newPlayer;
        List<FriendViewModel> friendViewModel = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerId", Common.loadPlayerId(FrAddActivity.this));
        jsonObject.addProperty("action", "searchNew");
        jsonObject.addProperty("searchId", searchId);
        String jsonOut = jsonObject.toString();
        MyTask task = new MyTask("http://10.0.2.2:8080/Advertisement_Server/GetFriendList", jsonOut);

        try {
            Gson gson = new Gson();
            String result = task.execute().get();
            Log.i("POST_RESULT", result);
            newPlayers = gson.fromJson(result, new TypeToken<List<Friend>>() {
            }.getType());
            newPlayer = newPlayers.get(0);
            friendViewModel.add(new FriendViewModel(newPlayer.getPlayer2Name(), newPlayer.getPlayer2Pic(), "", newPlayer.getPlayer2Id()));
            Log.d(TAG, "FriendViewModel = " + gson.toJson(new FriendViewModel(newPlayer.getPlayer2Name(), newPlayer.getPlayer2Pic(), "", newPlayer.getPlayer2Id())));

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }

        return friendViewModel;
    }
}
