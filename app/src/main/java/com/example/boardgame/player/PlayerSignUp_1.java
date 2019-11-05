package com.example.boardgame.player;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

public class PlayerSignUp_1 extends Fragment {
    private static final String TAG = "TAG_PlayerSignUp_2";
    private Activity activity;
    private Gson gson = new Gson();
    private Spinner spinner;
    private String[] keys;
    private String[] ccodes;
    private String txCCode;
    private TextView tvCCode;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("玩家註冊");
        return inflater.inflate(R.layout.fragment_player_sign_up_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvCCode = view.findViewById(R.id.tvCCode);
        spinner = view.findViewById(R.id.spinner);
        JsonObject jsonCcode = new JsonObject();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                activity.getAssets().open("ccode.txt")))) {
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
                sb.append("\n");
            }
            jsonCcode = gson.fromJson(sb.toString(), JsonObject.class);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        Set<String> jsonKeys = jsonCcode.keySet();
        keys = jsonKeys.toArray(new String[jsonKeys.size()]);
        ccodes = new String[keys.length];

        for (int i = 0; i < keys.length; i++) {
            ccodes[i] = jsonCcode.get(keys[i]).getAsString();
        }

        spinner.setAdapter(adapter);
        spinner.setSelection(84);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txCCode = ccodes[position].split("\\+|\\)")[1];
                tvCCode.setText("+" + txCCode);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private BaseAdapter adapter = new BaseAdapter() {
        public int getCount() {
            return keys.length;
        }

        public Object getItem(int position) {
            int resourceImage = activity.getResources().getIdentifier("flag_" + keys[position].toLowerCase(), "drawable", activity.getPackageName());
            return resourceImage;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout ll = new LinearLayout(activity);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER_VERTICAL);
            int resourceImage = activity.getResources().getIdentifier("flag_" + keys[position].toLowerCase(), "drawable", activity.getPackageName());

            ImageView iv = new ImageView(activity);
            iv.setLayoutParams(new LinearLayout.LayoutParams(75, 75));
            iv.setImageResource(resourceImage);
            ll.addView(iv);

            TextView tv = new TextView(activity);
            tv.setTextSize(18);
            tv.setText(" " + keys[position] + ", " + ccodes[position]);// 設定內容
            ll.addView(tv);
            return ll;
        }
    };
}
