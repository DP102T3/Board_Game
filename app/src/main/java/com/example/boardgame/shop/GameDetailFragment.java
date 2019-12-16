package com.example.boardgame.shop;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.player.CommonTask;
import com.example.boardgame.player.Player;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import static com.example.boardgame.shop.Common.showToast;

public class GameDetailFragment extends Fragment {
    private static final String TAG = "TAG_GameDetailFragment";
    private Activity activity;
    private Gson gson = new Gson();

    private ImageView ivGame;
    private TextView tvGameName, tvGameType, tvGameNp, tvGameTime, tvGameIntro;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("遊戲資訊");
        return inflater.inflate(R.layout.fragment_game_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle;
        bundle = getArguments();
        Game game = (Game) bundle.getSerializable("game");

        ivGame = view.findViewById(R.id.ivGame);
        // 以 gameNo 到 Servlet 取圖
        String imageUrl = Common.URL + "SignupServlet";
        int imageId = game.getGameNo();
        int imageSize = activity.getResources().getDisplayMetrics().widthPixels / 100 * 68;
        GameImageTask imageTask = new GameImageTask(imageUrl, imageId, imageSize, ivGame);
        imageTask.execute();

        tvGameName = view.findViewById(R.id.tvGameName);
        tvGameType = view.findViewById(R.id.tvGameType);
        tvGameNp = view.findViewById(R.id.tvGameNp);
        tvGameTime = view.findViewById(R.id.tvGameTime);
        tvGameIntro = view.findViewById(R.id.tvGameIntro);

        tvGameName.setText(game.getGameName());
        tvGameType.setText(game.getGameType());
        tvGameNp.setText(game.getGameNp());
        tvGameTime.setText(game.getGameTime());
        tvGameIntro.setText(game.getGameIntro());
    }

    @Override
    public void onStart() {
        super.onStart();
        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }
}
