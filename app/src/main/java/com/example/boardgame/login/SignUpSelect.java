package com.example.boardgame.login;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.boardgame.R;

public class SignUpSelect extends Fragment {
    Activity activity;
    Button btPlayer, btShop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("選擇註冊身份");
        return inflater.inflate(R.layout.fragment_sign_up_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btPlayer = view.findViewById(R.id.btPlayer);
        btPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(btPlayer).navigate(R.id.action_signUpSelect_to_playerSignUp_1);
            }
        });

        btShop = view.findViewById(R.id.btShop);
//        btShop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(btShop).navigate(R.id.action_signUpSelect_to_shop_signup);
//            }
//        });
    }
}
