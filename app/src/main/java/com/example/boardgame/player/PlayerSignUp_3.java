package com.example.boardgame.player;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Calendar;

import static com.example.boardgame.MainActivity.PLAYER;
import static com.example.boardgame.MainActivity.loginId;
import static com.example.boardgame.chat.Common.savePlayerId;
import static com.example.boardgame.chat.Common.showToast;

public class PlayerSignUp_3 extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "TAG_PlayerSignUp_3";
    private Activity activity;
    private String player_id;
    private String player_pw;

    private EditText etFullName, etNickName, etBirthday;
    private Spinner spGender;
    private Button btSubmit;
    private Gson gson = new Gson();

    int player_gender = 0;
    // DatePickerDialog_1
    private static int year, month, day;
    private DatePickerDialog dialog;
    private Calendar calendar = Calendar.getInstance();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("玩家註冊");
        Bundle bundle = getArguments();
        player_id = bundle.getString("player_id");
        player_pw = bundle.getString("player_pw");
        return inflater.inflate(R.layout.fragment_player_sign_up_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etFullName = view.findViewById(R.id.etFullName);
        etNickName = view.findViewById(R.id.etNickName);

        // 選擇性別的 Spinner
        spGender = view.findViewById(R.id.spGender);
        final String[] spGenderArray = {"Male", "Female"};
        ArrayAdapter<String> spGenderAdapter = new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item, spGenderArray);
        spGender.setAdapter(spGenderAdapter);
        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spGenderArray[position].equals("Male")) {
                    player_gender = 0;
                } else if (spGenderArray[position].equals("Female")) {
                    player_gender = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        etBirthday = view.findViewById(R.id.etBirthday);
        etBirthday.setInputType(InputType.TYPE_NULL);
        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        // 送出按鈕 及 建立監聽器
        btSubmit = view.findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String player_name = etFullName.getText().toString().trim();
                String player_nkname = etNickName.getText().toString().trim();
                String player_bday = etBirthday.getText().toString().trim();

                // 檢查欄位是否為空字串
                Boolean valid = true;
                if (player_name.isEmpty()) {
                    valid = false;
                    etFullName.setError(getString(R.string.txCanNotBeEmpty));
                }
                if (player_nkname.isEmpty()) {
                    valid = false;
                    etNickName.setError(getString(R.string.txCanNotBeEmpty));
                }
                if (player_bday.isEmpty()) {
                    valid = false;
                    etBirthday.setError(getString(R.string.txCanNotBeEmpty));
                }

                // 若 以上欄位的值 皆有輸入
                if (!valid) {
                    return;
                } else {
                    JsonObject jsonOut = new JsonObject();
                    jsonOut.addProperty("action", "playerSignUp");
                    Player player = new Player(player_id, player_pw, player_name, player_nkname, player_gender, player_bday);
                    jsonOut.addProperty("player", gson.toJson(player));
                    Log.d(TAG, "jsonOut = " + jsonOut);

                    int count = 0;
                    if (Common.networkConnected(activity)) {
                        String url = Common.SERVLET_URI;
                        try {
                            count = Integer.valueOf(new CommonTask(url, jsonOut.toString()).execute().get());
                            Log.d(TAG, "count = " + count);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }

                        if (count > 0) {
                            showToast(activity, "註冊成功");
                            // 紀錄登入身份
                            loginId = PLAYER;
                            // 將假的 玩家id 存入 偏好設定
                            savePlayerId(activity, player_id);
                            // 跳轉到首頁
                            Navigation.findNavController(v).navigate(R.id.action_playerSignUp_3_to_groupsFragment);
                        }else {
                            showToast(activity, "註冊失敗");
                        }
                    } else {
                        showToast(activity, R.string.tx_NoNetwork);
                    }
                }
            }
        });

        // 顯示現在時間，同時更新 PlayerSignUp_3 內的 year, month, day 三個日期參數
        showNow();
        // dialog建構式
        dialog = new DatePickerDialog(activity, PlayerSignUp_3.this, PlayerSignUp_3.year, PlayerSignUp_3.month, PlayerSignUp_3.day);
        // 設定最大日期（限定選擇範圍）
        dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
    }

    @Override
    public void onStart() {
        super.onStart();
        // 隱藏 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }

    /* 覆寫OnDateSetListener.onDateSet()以處理日期挑選完成事件。
    日期挑選完成會呼叫此方法，並傳入選取的年月日 */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        PlayerSignUp_3.year = year;
        PlayerSignUp_3.month = month;
        PlayerSignUp_3.day = dayOfMonth;
        updateDisplay();
    }

    /* 取得現在日期時間並呼叫updateDisplay()顯示在TextView上 */
    private void showNow() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /* 將指定的日期顯示在TextView上。
    一月的值是0而非1，所以「month + 1」後才顯示 */
    private void updateDisplay() {
        etBirthday.setText(new StringBuilder()
                .append(year).append("-")
                .append(pad(month + 1)).append("-")
                .append(pad(day)));
    }

    /* 若數字有十位數，直接顯示；
       若只有個位數則補0後再顯示，例如7會改成07後再顯示 */
    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        } else {
            return "0" + number;
        }
    }
}
