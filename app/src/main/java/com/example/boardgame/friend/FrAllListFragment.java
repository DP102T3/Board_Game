package com.example.boardgame.friend;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.Common;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FrAllListFragment extends Fragment {
    private final static String TAG = "TAG_FrAllListFragment";
    private RecyclerView recyclerView;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    List<FriendViewModel> friends;
    FriendAllAdapter friendAllAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 設置標題
        getActivity().setTitle("Friend");
        return inflater.inflate(R.layout.fragment_fr_all_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friends = getFriend();
        friendAllAdapter = new FriendAllAdapter(this, friends);
        recyclerView.setAdapter(friendAllAdapter);
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

    private List<Friend> convertJSONstringToFriendList(String json) {
        Gson gson = new Gson();

        FriendListResult friendListResult = gson.fromJson(json, FriendListResult.class);

        return friendListResult.getResult();
    }

    private List<FriendViewModel> getFriend() {
        List<FriendViewModel> friendViewModelList = new ArrayList<>();

//        ===== 有修改 =====
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("playerId", Common.loadPlayerId(getActivity()));
        jsonObject.addProperty("action", "getAll");
        String jsonOut = jsonObject.toString();

        MyTask task = new MyTask("http://10.0.2.2:8080/Advertisement_Server/GetFriendList", jsonOut);
//        ===============

        try {
            String result = task.execute().get();
            Log.i("POST_RESULT", result);

            List<Friend> friends = convertJSONstringToFriendList(result);

            for (Friend friend : friends) {

                FriendViewModel friendViewModel = new FriendViewModel(friend.getPlayer2Name(),
                        friend.getPlayer2Pic(), friend.getPlayer2Mood(), friend.getPlayer2Id());

                if (friend.getInviteStatus() == 2) {
                    friendViewModelList.add(friendViewModel);
                }
            }

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return friendViewModelList;
    }

   private class FriendAllAdapter extends RecyclerView.Adapter<MyViewHolder> {
        Context context;
        List<FriendViewModel> friendViewModelList;
        public FriendAllAdapter(FrAllListFragment frAllListFragment, List<FriendViewModel> friendViewModel) {
            context = frAllListFragment.getContext();
            this.friendViewModelList = friendViewModel;
        }

        public void setFriends(List<FriendViewModel> searchFriends) {
            this.friendViewModelList = searchFriends;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.itemview_all_friend, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int index) {
            final FriendViewModel friendViewModel = friendViewModelList.get(index);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("playerId", friendViewModel.getFrID());
                    Navigation.findNavController(v).navigate(R.id.action_frAllListFragment_to_profile1_Fragment, bundle);
                }
            });

            holder.tvName.setText(friendViewModel.getFrNkName());
            holder.tvStatus.setText(friendViewModel.getFrMood());
            if (friendViewModel.getFrPic() != null) {
                byte[] image = Base64.decode(friendViewModel.getFrPic(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                holder.frPic.setImageBitmap(bitmap);
            }
        }

        @Override
        public int getItemCount() { return friendViewModelList.size(); }

   }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView frPic;
        TextView tvName, tvStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            frPic = itemView.findViewById(R.id.ivFriend);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

// ======= OptionMenu =======

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    FriendAllAdapter adapter = (FriendAllAdapter) recyclerView.getAdapter();
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
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                // Not implemented here
                return false;

            case R.id.action_fradd:
                Intent intent = new Intent(getContext(), FrAddActivity.class);
                startActivity(intent);
                break;

            case R.id.action_note:
//            TODO 設定 action!!!!!!!
//                NavHostFragment.findNavController().navigate(R.id.PlayNosListFragment);
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }
}
