package com.example.boardgame.notification.SystemNotification;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.friend.FrAddActivity;
import com.example.boardgame.notification.Common;
import com.example.boardgame.notification.CommonShop;
import com.example.boardgame.notification.Websocket.NetWorkService;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Locale;



import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.boardgame.MainActivity.ONLY_BOTTOM;

public class SystemNotificationFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private Activity activity;
    private static final String TAG = "SystemNotificationFragment";
    public String types;
    private long alarmTime;
    private int year, month, day, hour, minute;
    public int target;
    private EditText edContent,edTitle;
    public static String inputContent,inputTitle;
    private Spinner spTarget;
    private static SystemNosWebSocketClient systemNosWebSocketClient;
    private Gson gson;
    public static String SERVER_URI =
            "ws://10.0.2.2:8080/BoardGame_Web/SystemNotificationServer/";
    private static String player_id;
    private static int shop_id;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.changeBarsStatus(3);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.actionBar.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_ad_setting,menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = getActivity();
        if (activity == null) {
            return;
        }
        player_id = Common.loadPlayer_id(activity);
        shop_id = CommonShop.loadShop_id(activity);
        setHasOptionsMenu(true);
        connectServer();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_system_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edContent = view.findViewById(R.id.edContent);
        edTitle = view.findViewById(R.id.edTitle);
        spTarget = view.findViewById(R.id.spTarget);

        //設置spinner data
        String[] type = {"店家","玩家","店家與玩家"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,R.layout.support_simple_spinner_dropdown_item,type);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spTarget.setAdapter(adapter);
        //預設選項為店家
        spTarget.setSelection(0,true);
        //這邊types預設為店家 如果客服以預設值直接送出才有值switch判斷
        types ="店家";

        spTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //點擊後取得客服選取的值
                types = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        view.findViewById(R.id.btSetAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputContent =edContent.getText().toString().trim();
                inputTitle = edTitle.getText().toString().trim();
                if(inputContent.isEmpty()||inputTitle.isEmpty()){
                    Common.showToast(getActivity(), R.string.textNoInput);
                    return;}
                else{
                    popDatePicker();
                }
            }
        });
    }

    // 跳出DatePicker選alarm設定日期
    private void popDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(activity, this, year, month, day).show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // 將挑選的日期存至實體變數
        this.year = year;
        this.month = month;
        this.day = dayOfMonth;
        // 日期選完後選時間
        popTimePicker();
    }

    // 跳出TimePicker選alarm設定時間
    private void popTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(activity, this, hour, minute, false).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // 將挑選的時間存至實體變數
        this.hour = hourOfDay;
        this.minute = minute;
        alarmTime = getTimeFromPicker();
        //判斷發送類型
        switch(types){
            case "店家":
                target = 1;
                break;
            case "玩家":
                target = 2;
                break;
            case "店家與玩家":
                target = 3;
                break;
        }
        //客服時間日期挑選完畢後send message至websocket
        SystemNotification systemNotification = new SystemNotification(target,0,0,null,inputTitle,inputContent,alarmTime);
        String systemNosJson = new Gson().toJson(systemNotification);
        systemNosWebSocketClient.send(systemNosJson);
    }

    // 將選好的日期時間轉成設定alarm所需的時間格式-毫秒
    private long getTimeFromPicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        return calendar.getTimeInMillis();
    }

    public class SystemNosWebSocketClient extends WebSocketClient {
        SystemNosWebSocketClient(URI serverURI) {
            // Draft_17是連接協議，就是標準的RFC 6455（JSR256）
            super(serverURI, new Draft_17());
            gson = new Gson();
        }

        @Override
        public void onOpen(ServerHandshake handshakeData) {
            String text = String.format(Locale.getDefault(),
                    "onOpen: Http status code = %d; status message = %s",
                    handshakeData.getHttpStatus(),
                    handshakeData.getHttpStatusMessage());
            Log.d(TAG, "onOpen: " + text);
        }

        @Override
        public void onMessage(String message) {
            Log.d("SystemSelfOnMessage: ", message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            String text = String.format(Locale.getDefault(),
                    "code = %d, reason = %s, remote = %b",
                    code, reason, remote);
            Log.d(TAG, "onClose: " + text);
        }

        @Override
        public void onError(Exception ex) {
            Log.d(TAG, "onError: exception = " + ex.toString());
        }
    }

    // 建立WebSocket連線
    private void connectServer() {
        URI uri = null;
        try {
            if(!player_id.isEmpty()){
                uri = new URI(SERVER_URI + player_id);
            }else {
                uri = new URI(SERVER_URI + shop_id);
            }
        } catch (URISyntaxException e) {
            Log.e(TAG, e.toString());
        }
        if (systemNosWebSocketClient == null) {
            systemNosWebSocketClient = new SystemNosWebSocketClient(uri);
            systemNosWebSocketClient.connect();
        }
    }

    // 中斷WebSocket連線
    private void disconnectServer() {
        if (systemNosWebSocketClient != null) {
            systemNosWebSocketClient.close();
            systemNosWebSocketClient = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectServer();
    }
}
