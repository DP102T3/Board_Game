package com.example.boardgame.shop;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import static android.content.Context.MODE_PRIVATE;

public class Shop_signuptwo extends Fragment {

    private Activity activity;

    private EditText shopPassword, shopcfPassword, ShopId;
    private final static String PREFERENCES_NAME = "preferences";
    private final static String DEFAULT_FILE_NAME = "Id";

    private SharedPreferences preferences;

    private Button ntbt;
    private Shop shop;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle("註冊");
        return inflater.inflate(R.layout.fragment_shop_signuptwo, null);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ShopId = view.findViewById(R.id.shop_id);
        shopPassword = view.findViewById(R.id.shop_password);
        shopcfPassword = view.findViewById(R.id.shop_cfpassword);
        ntbt = view.findViewById(R.id.ntbt);
        ShopId.setKeyListener(null);

// =============================================偏好設定=====================================================================================

        //給偏好設定檔名 會自動幫見一個檔案
        preferences = activity.getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

        Bundle bundle = getArguments();

        if (bundle != null) {

            //解鎖 getSerializable
            shop = (Shop) bundle.getSerializable("shop");
            //取得edName
            int text = shop.getShopId();
            ShopId.setText(String.valueOf(text));

        }


        ntbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferences();

                boolean invalid = false;
                Bundle bundle = new Bundle();

                String password = shopPassword.getText().toString().trim();
                String cfpassword = shopcfPassword.getText().toString().trim();

                if (password.isEmpty()) {
                    shopPassword.setError("請輸入密碼");
                    invalid = true;

                }
                if (cfpassword.isEmpty()) {
                    shopcfPassword.setError("請確認密碼");
                    invalid = true;

                } else {
                    if (!cfpassword.equals(password)) {
                        shopcfPassword.setError("密碼不一致");
                        invalid = true;
                    }
                }

                if (invalid) {
                    return;
                }

                shop.setShopPassword(password);
                bundle.putSerializable("shop", shop);


                Navigation.findNavController(view)
                        .navigate(R.id.action_shop_signuptwo_to_shop_signupthree, bundle);

            }


//            private void loadPreferences() {
//                String fileName = preferences.getString("shopId", DEFAULT_FILE_NAME);
//                ShopId.setText(fileName);
//
//                AlarmCommon.showToast(activity, "textPreferencesload");
//            }


        });

    }


//==================================================儲存偏好設定===============================================================================

    private void savePreferences() {

        String shopId = ShopId.getText().toString();
        preferences.edit()
                .putInt("shopId", Integer.parseInt(shopId))
                .apply();
        Common.showToast(activity, "textPreferencesSaved");

    }
//==================================================取偏好設定===============================================================================


    @Override
    public void onStart() {
        super.onStart();
        // 隱藏 TabBar 及 BottomBar
        com.example.boardgame.MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_AND_BOTTOM);
    }
}





