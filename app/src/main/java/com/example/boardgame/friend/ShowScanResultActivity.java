package com.example.boardgame.friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class ShowScanResultActivity extends AppCompatActivity {
    private static final String TAG = "TAG_ShowScanResultActivity";

    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_scan_result);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<FriendViewModel> friendViewModelList = new ArrayList<>();
        String qrCodeStr = getIntent().getStringExtra("QRCode");
        Log.i("QRCode", qrCodeStr);

        friendViewModelList = searchNew(qrCodeStr);
        recyclerView.setAdapter(new ShowResultAdapter(this, friendViewModelList));
    }

    private class ShowResultAdapter extends RecyclerView.Adapter<MyViewHolder> {
        Context context;
        List<FriendViewModel> friendViewModelList;
        public ShowResultAdapter(ShowScanResultActivity showScanResultActivity, List<FriendViewModel> friendViewModelList) {
            this.friendViewModelList = friendViewModelList;
            this.context = showScanResultActivity;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemview = LayoutInflater.from(context).inflate(R.layout.itemview_scan_result,viewGroup,false);
            return new MyViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int index) {
            final FriendViewModel friendViewModel = friendViewModelList.get(index);

            Log.i("friendViewModel", friendViewModel.getFrNkName());
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

                    Friend friend = new Friend(Common.loadPlayerId(ShowScanResultActivity.this), friendViewModel.getFrID(), friendViewModel.getPointCount(), 1);
                    String jsonOut = gson.toJson(friend);

                    MyTask task = new MyTask("http://10.0.2.2:8080/Advertisement_Server/CreateFriend", jsonOut);
//        ===============
                    try {
                        int result = Integer.valueOf(task.execute().get());
                        if (result != 0) {
                            Common.showToast(ShowScanResultActivity.this, "已送出邀請");
                            Navigation.findNavController(holder.btnInvite).popBackStack();
                        } else {
                            Log.d(TAG, "新增好友失敗，請重新開啟 App");
                        }
                        notifyDataSetChanged();
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

    public List<FriendViewModel> searchNew(String searchId) {
        List<Friend> newPlayers;
        Friend newPlayer;
        List<FriendViewModel> friendViewModel = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("playerId", Common.loadPlayerId(ShowScanResultActivity.this));
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
