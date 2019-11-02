package com.example.boardgame.friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
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

import com.example.boardgame.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class FrAddActivity extends AppCompatActivity {

    //可搜尋好友ID及選擇掃描QR Code頁面

    private RecyclerView recyclerView;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    List<FriendViewModel> friends;
    FriendSearchAdapter friendSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fr_add);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friends = new ArrayList<>();
        friendSearchAdapter = new FriendSearchAdapter(this, friends);
        recyclerView.setAdapter(friendSearchAdapter);
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
                    String id = friendViewModel.getFrID();
                    String json = String.format( "{\"player1Id\":\"myself\",\"player2Id\":\"%s\",\"inviteStatus\":1,\"pointCount\":0}", id);
                    MyTask task = new MyTask(
                            "http://10.0.2.2:8080/Advertisement_Server/CreateFriend",
                            json,
                            null);
                    try {
                        String result = task.execute().get();
                        Log.i("POST_RESULT", result);

                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }
                }
            });
        }

        @Override
        public int getItemCount() { return friendViewModelList.size(); }

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

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    FriendSearchAdapter adapter = (FriendSearchAdapter) recyclerView.getAdapter();
                    if (adapter != null) {
                        // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                        if (newText.isEmpty()) {
                            adapter.setFriends(friends);
                        } else {
                            List<FriendViewModel> searchFriends = new ArrayList<>();
                            // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                            for (FriendViewModel friend : friends) {
                                if (friend.getFrNkName().toUpperCase().contains(newText.toUpperCase())
                                        ||friend.getFrID().toUpperCase().contains(newText.toUpperCase())) {
                                    searchFriends.add(friend);
                                }
                            }
                            adapter.setFriends(searchFriends);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    return false;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Log.i("InF", String.valueOf(id));

        switch (id){
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
        return super.onOptionsItemSelected(item);    }
}
