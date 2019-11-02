package com.example.boardgame.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FrInvitedFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fr_invited, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recyclerView);
        List<FriendViewModel> friends = getFriend();
        recyclerView.setAdapter(new FrInvitedAdapter(this, friends));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private List<FriendViewModel> getFriend() {
        List<FriendViewModel> friendViewModelList = new ArrayList<>();
        MyTask task = new MyTask(
                "http://10.0.2.2:8080/Advertisement_Server/GetInvitedFriend",
                "{\"player2Id\":\"myself\"}",
                null
        );
        try {
            String result = task.execute().get();
            Log.i("POST_RESULT", result);

            List<Friend> friends = convertJSONstringToFriendList(result);

            for (Friend friend : friends) {

                FriendViewModel friendViewModel = new FriendViewModel(friend.getPlayer2Name(),
                        friend.getPlayer2Pic(), friend.getPlayer2Mood(), friend.getPlayer1Id());
                if (friend.getInviteStatus() == 1) {
                    friendViewModelList.add(friendViewModel);
                }
            }

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return friendViewModelList;
    }

    private List<Friend> convertJSONstringToFriendList(String json) {
        Gson gson = new Gson();

        FriendListResult friendListResult = gson.fromJson(json, FriendListResult.class);

        return friendListResult.getResult();
    }


    private class FrInvitedAdapter extends RecyclerView.Adapter<MyViewHolder> {
        Context context;
        List<FriendViewModel> friends;
        public FrInvitedAdapter(FrInvitedFragment frInvitedFragment, List<FriendViewModel> friends) {
            context = frInvitedFragment.getContext();
            this.friends = friends;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.itemview_invited, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int index) {
            final FriendViewModel friend = friends.get(index);
            holder.tvName.setText(friend.getFrNkName());
            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyTask task = new MyTask(
                            "http://10.0.2.2:8080/Advertisement_Server/CreateFriend",
                            String.format("{\"player1Id\":\"%s\",\"player2Id\":\"myself\",\"inviteStatus\":2}", friend.getFrID()),
                            null);
                    MyTask task2 = new MyTask(
                            "http://10.0.2.2:8080/Advertisement_Server/CreateFriend",
                            String.format("{\"player1Id\":\"myself\",\"player2Id\":\"%s\",\"inviteStatus\":2, \"pointCount\":0}", friend.getFrID()),
                            null);
                    try {
                        String result = task.execute().get();
                        task2.execute();
                        friends.remove(index);
                        notifyDataSetChanged();
                        Log.i("POST_RESULT", result);

                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }
                }
            });
            holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyTask task = new MyTask(
                            "http://10.0.2.2:8080/Advertisement_Server/DeleteFriend",
                            String.format("{\"player1Id\":\"%s\",\"player2Id\":\"myself\"}", friend.getFrID()),
                            null);
                    try {
                        String result = task.execute().get();
                        friends.remove(index);
                        notifyDataSetChanged();
                        Log.i("POST_RESULT", result);

                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }
                }
            });
        }

        @Override
        public int getItemCount() { return friends.size(); }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnDecline;
        Button btnAccept;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnAccept = itemView.findViewById(R.id.btnAccept);


        }
    }

//======== OptionsMenu ========

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_noteadd, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_fradd:
                Intent intent = new Intent(getContext(), FrAddActivity.class);
                startActivity(intent);
                break;

            case R.id.action_note:
//            TODO 設定 action
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}