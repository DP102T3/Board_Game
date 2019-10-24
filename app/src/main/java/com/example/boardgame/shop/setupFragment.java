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

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;


public class setupFragment extends Fragment {

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


    }

    @Override
    public void onStart() {
        super.onStart();

        // 隱藏 TabBar
        MainActivity.changeBarsStatus(MainActivity.ONLY_BOTTOM);
    }
}
