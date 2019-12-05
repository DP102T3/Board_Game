package com.example.boardgame.friend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ShowScanResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_AND_BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_scan_result);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<FriendViewModel> friendViewModelList = new ArrayList<>();
        String jsonQRCode = getIntent().getStringExtra("QRCode");
        Log.i("QRCode", jsonQRCode);
        Gson gson = new Gson();
        FriendViewModel friendViewModel = gson.fromJson(jsonQRCode, FriendViewModel.class);
        friendViewModelList.add(friendViewModel);
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
        public void onBindViewHolder(@NonNull MyViewHolder holder, int index) {
            final FriendViewModel fv = friendViewModelList.get(index);

            Log.i("fv", fv.getFrNkName());

            holder.tvName.setText(fv.getFrNkName());
            holder.btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = fv.getFrID();
                    String json = String.format( "{\"player1Id\":\"myself\",\"player2Id\":\"%s\",\"inviteStatus\":1,\"pointCount\":0}", id);
                    MyTask task = new MyTask(
                            "http://10.0.2.2:8080/Advertisement_Server/CreateFriend",
                            json,
                            null);
                    try {
                        String result = task.execute().get();
                        Log.i("POST_RESULT", result);
//                        Intent intent = new Intent(getApplicationContext(), FriendMainActivity.class);
//                        intent.putExtra("inviting", 3);
//                        startActivity(intent);
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
}
