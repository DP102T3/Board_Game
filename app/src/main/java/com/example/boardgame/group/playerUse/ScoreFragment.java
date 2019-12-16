package com.example.boardgame.group.playerUse;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.R;
import com.example.boardgame.group.Common;
import com.example.boardgame.group.CommonTask;
import com.example.boardgame.group.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.boardgame.group.Common.playerId;


public class ScoreFragment extends Fragment {
    private static final String TAG = "TAG_ScoreFragment";
    private Activity activity;
    private CommonTask scoreTask, shopTask;
    private ImageTask scoreImageTask, shopImageTask;
    private RecyclerView rvScore;
    private List<Member> members;
    private ImageView ivShop;
    private TextView tvShopName;
    private RatingBar rbShop;
    private Button btReport, btInput;
    private int groupNo, shopId;
    private int shopScore;
    Gson gson = new Gson();
    Map<String, Integer> rate = new HashMap<String, Integer>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        groupNo = getArguments() != null ? getArguments().getInt("groupNo") : -1;
        shopId = getArguments() != null ? getArguments().getInt("shopId") : -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivShop = view.findViewById(R.id.ivShop);
        tvShopName = view.findViewById(R.id.tvShopName);
        rbShop = view.findViewById(R.id.rbShop);
        rbShop.setStepSize(1);

        btReport = view.findViewById(R.id.btReport);
        btInput=view.findViewById(R.id.btInput);

        showShop();

        rvScore = view.findViewById(R.id.rvScore);
        rvScore.setLayoutManager(new LinearLayoutManager(activity));
        members = getMembers();
        showMembers(members);

        rbShop.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                shopScore = (int)rating;

            }
        });

        btInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.networkConnected(activity)) {
                    //送出店家評分
                    String url = Common.URL_SERVER + "/ShopServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "setShopScore");
                    jsonObject.addProperty("shopId", shopId);
                    jsonObject.addProperty("shopScore", shopScore);
                    jsonObject.addProperty("groupNo", groupNo);
                    jsonObject.addProperty("playerId", playerId);
                    int count = 0;

                    //送出玩家評分
                    String url2 = Common.URL_SERVER + "/JoinMemberServlet";
                    JsonObject jsonObject2 = new JsonObject();
                    jsonObject2.addProperty("action", "setMemberScore");
                    jsonObject2.addProperty("groupNo", groupNo);
                    jsonObject2.addProperty("playerId", playerId);
                    jsonObject2.addProperty("rate", gson.toJson(rate));
                    int count2 = 0;


                    try {
                        //送出店家評分
                        String result = new CommonTask(url, jsonObject.toString()).execute().get();
                        count = Integer.valueOf(result);

                        //送出玩家評分
                        String result2 = new CommonTask(url2, jsonObject2.toString()).execute().get();
                        count2 = Integer.valueOf(result2);

                    }catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }if (count == 0||count2==0) {
                        Common.showToast(getActivity(), R.string.textInsertFail);

                    }else {
                        Common.showToast(getActivity(), R.string.textInsertSuccess);
                        Navigation.findNavController(btInput).popBackStack();
                    }
                }else {
                    Common.showToast(activity, R.string.textNoNetwork);
                }
            }
        });
    }

    private void showShop() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/ShopServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getShopName");
            jsonObject.addProperty("shopId", shopId);
            String jsonOut = jsonObject.toString();
            shopTask = new CommonTask(url, jsonOut);

            shopImageTask = new ImageTask(url, shopId, ivShop);
            shopImageTask.execute();

            try {
                String shopName = shopTask.execute().get();
                tvShopName.setText(shopName);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
    }

    private List<Member> getMembers() {
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/JoinMemberServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getMemberName");
            jsonObject.addProperty("groupNo", groupNo);
            jsonObject.addProperty("playerId", playerId);
            String jsonOut = jsonObject.toString();
            scoreTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = scoreTask.execute().get();
                Type listType = new TypeToken<List<Member>>() {
                }.getType();
                members = gson.fromJson(jsonIn, listType);

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }

        return members;
    }

    private void showMembers(List<Member> members) {
        if (members == null || members.isEmpty()) {
            Common.showToast(activity, R.string.textNoMember);
            return;
        }
        MemberAdapter memberAdapt = (MemberAdapter) rvScore.getAdapter();
        if (memberAdapt == null) {
            rvScore.setAdapter(new ScoreFragment.MemberAdapter(activity, members));
        } else {
            memberAdapt.setMembers(members);
            memberAdapt.notifyDataSetChanged();//重刷
        }

    }

    class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Member> members;

        MemberAdapter(Context context, List<Member> members) {
            layoutInflater = LayoutInflater.from(context);
            this.members = members;
        }

        void setMembers(List<Member> members) {
            this.members = members;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivPlayer;
            TextView tvPlayerName;
            RatingBar rbPlayer;


            MyViewHolder(View itemView) {
                super(itemView);
                ivPlayer = itemView.findViewById(R.id.ivPlayer);
                tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
                rbPlayer = itemView.findViewById(R.id.rbPlayer);

            }
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.score_view, parent, false);
            return new MyViewHolder(itemView);

        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Member member = members.get(position);
            String url = Common.URL_SERVER + "JoinMemberServlet";
            final String playerId = member.getPlayerId();


            scoreImageTask = new ImageTask(url, playerId, holder.ivPlayer);
            scoreImageTask.execute();

            holder.tvPlayerName.setText(member.getPlayerNKName());
            holder.rbPlayer.setStepSize(1);
            holder.rbPlayer.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    rate.put(playerId, (int)v);
                }
            });
        }

        @Override
        public int getItemCount() {
            return members.size();

        }
    }
}
