package com.example.boardgame.friend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FrInvitingFragment extends Fragment {
    private static final String TAG = "TAG_FrInvitingFragment";

    private RecyclerView recyclerView;
    private List<FriendViewModel> friendViewModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fr_inviting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getFriend();
        recyclerView.setAdapter(new FrInvitingAdapter(this, friendViewModelList));
    }

    @Override
    public void onStart() {
        super.onStart();
        // 顯示 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
        // 置換 TabBar 的 menu
        MainActivity.setTabBar(MainActivity.TAB_FRIEND);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);
    }

    @Override
    public void onResume() {
        super.onResume();
        getFriend();
        FrInvitingAdapter adapter =  (FrInvitingAdapter)recyclerView.getAdapter();
        if (adapter == null) {
            adapter = new FrInvitingAdapter(FrInvitingFragment.this, friendViewModelList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setFriends(friendViewModelList);
            adapter.notifyDataSetChanged();
        }
    }

    private void getFriend() {
        JsonObject jsonObject = new JsonObject();
//        ===== 有修改 =====
        jsonObject.addProperty("playerId", Common.loadPlayerId(getActivity()));
        jsonObject.addProperty("action", "inviting");
        String jsonOut = jsonObject.toString();
//        ===============
        MyTask task = new MyTask("http://10.0.2.2:8080/Advertisement_Server/GetFriendList", jsonOut);

        try {
            String result = task.execute().get();
            Log.i("POST_RESULT", result);

            List<Friend> friends = convertJSONstringToFriendList(result);

            for (Friend friend : friends) {

                FriendViewModel friendViewModel = new FriendViewModel(friend.getPlayer2Name(),
                        friend.getPlayer2Pic(), friend.getPlayer2Mood(), friend.getPlayer2Id());

                if (friend.getInviteStatus() == 1) {
                    friendViewModelList.add(friendViewModel);
                }
            }

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    private List<Friend> convertJSONstringToFriendList(String json) {
        Gson gson = new Gson();
        if(json==null){
            return new ArrayList<>();
        }
        FriendListResult friendListResult = gson.fromJson(json, FriendListResult.class);
        return friendListResult.getResult();
    }

    private class FrInvitingAdapter extends RecyclerView.Adapter<MyViewHolder> {
        Context context;
        List<FriendViewModel> friends;

        public FrInvitingAdapter(FrInvitingFragment frInvitingFragment, List<FriendViewModel> friends) {
            context = frInvitingFragment.getContext();
            this.friends = friends;
        }

        public void setFriends(List<FriendViewModel> friends){
            this.friends = friends;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.itemview_inviting, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int index) {
            final FriendViewModel friendViewModel = friends.get(index);
            holder.tvName.setText(friendViewModel.getFrNkName());
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//        ===== 有修改 =====
                    Gson gson = new Gson();

                    Friend friend = new Friend(Common.loadPlayerId(getActivity()), friendViewModel.getFrID());
                    String jsonOut = gson.toJson(friend);

                    MyTask task = new MyTask("http://10.0.2.2:8080/Advertisement_Server/DeleteFriend", jsonOut);

                    try {
                        String result = task.execute().get();
                        Log.i("POST_RESULT", result);
                        if (!"0".equals(result)) {
                            friends.remove(index);
                            notifyDataSetChanged();
                        }else {
                            Log.d(TAG, "Delete invitation failed !");
                        }
                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }
//        ===============
                }
            });

            if (friendViewModel.getFrPic() != null) {
                byte[] image = Base64.decode(friendViewModel.getFrPic(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                holder.ivFriend.setImageBitmap(bitmap);
            }
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnDelete;
        ImageView ivFriend;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ivFriend = itemView.findViewById(R.id.ivFriend);
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

        Log.i("InF", String.valueOf(id));

        switch (id) {
            case R.id.action_fradd:
                Intent intent = new Intent(getContext(), FrAddActivity.class);
                startActivity(intent);
                break;

            case R.id.action_note:
                Navigation.findNavController(recyclerView).navigate(R.id.playerNosListFragment);
                MainActivity.changeBarsStatus(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
