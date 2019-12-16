package com.example.boardgame.shop;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.R;
import com.example.boardgame.notification.Common;

import static com.example.boardgame.MainActivity.loginId;
import static com.example.boardgame.chat.Common.disConnectSocket;


public class SetupFragment extends Fragment {

    private Activity activity;
    private Button btinfo;
    int Id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle("設定");
        return inflater.inflate(R.layout.fragment_setup, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btinfo = view.findViewById(R.id.btinfo);

        btinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Navigation.findNavController(view).navigate(R.id.action_setupFragment_to_editinfoFragment);

            }
        });

        view.findViewById(R.id.btSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 初始化登入身份
                loginId = 0;
                // 清空使用者帳號
                Common.savePlayer_id(activity, "");
                // 關閉聊天的WebSocket
                disConnectSocket();
                Navigation.findNavController(v).navigate(R.id.action_setupFragment_to_loginFragment);
            }
        });


    }

}
