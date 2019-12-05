package com.example.boardgame.friend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;

public class FriendMainActivity extends AppCompatActivity {

    Activity activity;
    private Fragment fragment;
    private FragmentManager manager;

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.setTabBar(R.menu.tab_menu_friend);
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_main);
        manager = getSupportFragmentManager();
        fragment = manager.findFragmentById(R.id.fragment_friend);
        activity = this;
        int page = getIntent().getIntExtra("inviting", 1);
        if (page == 3){
            NavHostFragment.findNavController(fragment).navigate(R.id.frInvitingFragment);
        }
    }

//    public void onButtonClick(View view) {
//        FragmentTransaction transaction = manager.beginTransaction();
//        switch (view.getId()){
//            case R.id.btnFriendAll:
//                NavHostFragment.findNavController(fragment).navigate(R.id.frAllListFragment);
//                break;
//
//            case R.id.btnInvited:
//                NavHostFragment.findNavController(fragment).navigate(R.id.frInvitedFragment);
//                break;
//
//            case R.id.btnInviting:
//                NavHostFragment.findNavController(fragment).navigate(R.id.frInvitingFragment);
//                break;
//        }
//        transaction.commit();
//    }

}
