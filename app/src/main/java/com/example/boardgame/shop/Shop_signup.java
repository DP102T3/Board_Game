package com.example.boardgame.shop;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;


public class Shop_signup extends Fragment {

    private  static  final  String TAG = "TAG_Shop_signup";
    private Activity activity;
    private EditText shopId;
    private Button btcf;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("店家註冊");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shopId = view.findViewById(R.id.shop_id);
        btcf = view.findViewById(R.id.btcf);
        Bundle bundle = getArguments();


        btcf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();

                String id = shopId.getText().toString().trim();

                if(id.isEmpty()) {
                    shopId.setError("請輸入統一編號");

                }

//                if(!(id.trim().length() == 8) )
                if(id.trim().length() !=8){
                    shopId.setError("請輸入8碼統編");
                    return;
                }
                shopId.setText("");


                Shop shop = new Shop();
                shop.setShopId(Integer.valueOf(id));
                Log.d("testshopId", String.valueOf(shopId));
                bundle.putSerializable("shop",shop);

                Navigation.findNavController(view).navigate(R.id.action_shop_signup_to_shop_signuptwo,bundle);

            }



        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_AND_BOTTOM);
    }
}