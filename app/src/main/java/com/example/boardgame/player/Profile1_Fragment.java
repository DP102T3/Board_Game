package com.example.boardgame.player;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.ImageTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static com.example.boardgame.chat.Common.showToast;
import static com.example.boardgame.player.Common.loadPlayerId;

public class Profile1_Fragment extends Fragment {
    private static final String TAG = "TAG_Profile1_Fragment";
    private Activity activity;
    private String playerId, findPlayerId;
    private Gson gson = new Gson();
    ScrollView scrollViewProfile;
    ConstraintLayout constraintLayoutProfileParent, constraintLayoutProfile;
    ImageView ivPlayerPic1, ivStarIcon;
    TextView tvRate, tvPlayerIdTitle, tvPlayerId, tvPlayerNkName, tvPlayerGender, tvPlayerStar, tvPlayerArea, tvFavBg, tvPlayerIntro, tvPlayerMood;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_player_pofile, menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        constraintLayoutProfileParent = view.findViewById(R.id.constraintLayoutProfileParent);
        scrollViewProfile = view.findViewById(R.id.scrollViewProfile);
        constraintLayoutProfile = view.findViewById(R.id.constraintLayoutProfile);
        ivPlayerPic1 = view.findViewById(R.id.ivPlayerPic1);
        tvRate = view.findViewById(R.id.tvRate);
        ivStarIcon = view.findViewById(R.id.ivStarIcon);
        tvPlayerIdTitle = view.findViewById(R.id.tvPlayerIdTitle);
        tvPlayerId = view.findViewById(R.id.tvPlayerId);
        tvPlayerNkName = view.findViewById(R.id.tvPlayerNkName);
        tvPlayerGender = view.findViewById(R.id.tvPlayerGender);
        tvPlayerStar = view.findViewById(R.id.tvPlayerStar);
        tvPlayerArea = view.findViewById(R.id.tvPlayerArea);
        tvFavBg = view.findViewById(R.id.tvFavBg);
        tvPlayerIntro = view.findViewById(R.id.tvPlayerIntro);
        tvPlayerMood = view.findViewById(R.id.tvPlayerMood);
    }

    @Override
    public void onStart() {
        super.onStart();
        ConstraintSet constraintSet = new ConstraintSet();

        Bundle bundle = getArguments();

        if (bundle == null) {
            // 顯示 TabBar 及 BottomBar
            MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
            // 置換 TabBar 的 menu
            MainActivity.setTabBar(MainActivity.TAB_PROFILE);
            // 置換 BottomBar 的 menu
            MainActivity.setBottomBar(MainActivity.BOTTOM_PLAYER);
            // 取得 偏好設定的 playerId
            playerId = loadPlayerId(activity);
            findPlayerId = playerId;
//            findPlayerId = "gerfarn0523"; // 測試用，他人查看的視角
        } else {
            // 隱藏 TabBar 和 BottomBar
            MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);

            // 使 ScrollView 的 Constraint 符合沒有 TabBar 和 BottomBar 的畫面
            constraintSet.clone(constraintLayoutProfileParent);
            constraintSet.connect(R.id.scrollViewProfile, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(R.id.scrollViewProfile, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            constraintSet.applyTo(constraintLayoutProfileParent);
            findPlayerId = bundle.getString("playerId");
        }

        Log.d(TAG, "findPlayerId = " + findPlayerId);

        // 以 playerId 到 Servlet 取圖
        String imageUrl = com.example.boardgame.chat.Common.SERVLET_URI;
        String imageId = findPlayerId;
        int imageSize = activity.getResources().getDisplayMetrics().widthPixels / 100 * 68;
        ImageTask imageTask = new ImageTask(imageUrl, imageId, imageSize, ivPlayerPic1);
        imageTask.execute();

        // 從 Sevlet 取得要顯示的玩家資料
        JsonObject jsonOut = new JsonObject();
        jsonOut.addProperty("action", "showProfile");
        jsonOut.addProperty("findPlayerId", findPlayerId);

        if (Common.networkConnected(activity)) {
            String url = Common.SERVLET_URI;
            String jsonIn;
            Player player = null;
            try {
                jsonIn = new CommonTask(url, jsonOut.toString()).execute().get();
                player = gson.fromJson(jsonIn, Player.class);
                Log.d(TAG, jsonIn);
                if (player != null) {
                    if (findPlayerId.equals(playerId)) {
                        activity.setTitle("個人資訊");
                    } else {
                        activity.setTitle(player.getPlayer_nkname() != null ? player.getPlayer_nkname() : "");
                    }
                    double rate = 0.0;
                    try {
                        int rate_tatal = player.getRate_total();
                        int rate_count = player.getRate_count();
                        if (rate_count != 0) {
                            rate = (double) rate_tatal / (double) rate_count;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }

                    if (rate == 0) {
                        tvRate.setText("暫無評價");
                        ivStarIcon.setVisibility(View.INVISIBLE);
                    } else {
                        tvRate.setText(String.format("%.1f", rate));
                    }

                    // 判斷查看對象是否為本人
                    if (loadPlayerId(activity).equals(findPlayerId)) {
                        tvPlayerId.setText(player.getPlayer_id());
                    } else {
                        // 隱藏 tvPlayerIdTitle、tvPlayerId，修改tvPlayerNkName的Constraint
                        constraintSet.clone(constraintLayoutProfile);
                        constraintSet.connect(R.id.tvPlayerNkNameTitle, ConstraintSet.TOP, R.id.ivStarIcon, ConstraintSet.BOTTOM, 32);
                        constraintSet.applyTo(constraintLayoutProfile);

                        tvPlayerId.setVisibility(View.GONE);
                        tvPlayerIdTitle.setVisibility(View.GONE);
                    }
                    tvPlayerNkName.setText(player.getPlayer_nkname() != null ? player.getPlayer_nkname() : "");
                    tvPlayerGender.setText(player.getPlayer_gender() == 0 ? "男性" : "女性");
                    tvPlayerStar.setText(player.getPlayer_star() != null ? player.getPlayer_star() : "");
                    tvPlayerArea.setText(player.getPlayer_area() != null ? player.getPlayer_area() : "");
                    tvFavBg.setText(player.getFav_bg() != null ? player.getFav_bg() : "");
                    tvPlayerIntro.setText(player.getPlayer_intro() != null ? player.getPlayer_intro() : "");
                    tvPlayerMood.setText(player.getPlayer_mood() != null ? player.getPlayer_mood() : "");
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            showToast(activity, R.string.tx_NoNetwork);
        }
    }
}
