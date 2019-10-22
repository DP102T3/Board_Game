package com.example.boardgame.chat;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.R;

import static com.example.boardgame.chat.Common.savePlayerId;


public class mainFragment extends Fragment {
    private Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("Test List");
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ChatGroup
        view.findViewById(R.id.btUserNameRyan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ＊測試：將假的 玩家id 存入 偏好設定
                savePlayerId(activity,"chengchi1223");
                Navigation.findNavController(v)
                        .navigate(R.id.action_mainFragment_to_listFriendsFragment);
            }
        });

        view.findViewById(R.id.btUserNameMay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ＊測試：將假的 玩家id 存入 偏好設定
                savePlayerId(activity,"gerfarn0523");
                Navigation.findNavController(v)
                        .navigate(R.id.action_mainFragment_to_listFriendsFragment);
            }
        });

        view.findViewById(R.id.btUserNameJerry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ＊測試：將假的 玩家id 存入 偏好設定
                savePlayerId(activity,"jerry1124");
                Navigation.findNavController(v)
                        .navigate(R.id.action_mainFragment_to_listFriendsFragment);
            }
        });
    }
}
