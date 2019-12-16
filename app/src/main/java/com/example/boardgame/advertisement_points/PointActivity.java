package com.example.boardgame.advertisement_points;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import tech.cherri.tpdirect.api.TPDCardInfo;
import tech.cherri.tpdirect.api.TPDConsumer;
import tech.cherri.tpdirect.api.TPDGooglePay;
import tech.cherri.tpdirect.api.TPDMerchant;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDGooglePayListener;
import tech.cherri.tpdirect.callback.TPDTokenFailureCallback;
import tech.cherri.tpdirect.callback.TPDTokenSuccessCallback;

import static com.example.boardgame.advertisement_points.Common.CARD_TYPES;

public class PointActivity extends AppCompatActivity {

    private int pointCount;
    private TextView tvPoints;
    private Button btn300, btn800, btn1000;
    private static final String TAG = "TAG_PointActivity";
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 101;
    private TPDGooglePay tpdGooglePay;
    private PaymentData paymentData;

    public static int buyCount;
    public static int total;

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.changeBarsStatus(MainActivity.ONLY_BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        handleViews();

        TPDSetup.initInstance(this.getApplicationContext(),
                Integer.parseInt(getString(R.string.TapPay_AppID)),
                getString(R.string.TapPay_AppKey),
                TPDServerType.Sandbox);

        prepareGooglePay();
        getAllPoint();
    }

    private void getAllPoint() {
        String postJsonString = String.format(
                Locale.TAIWAN,
                "{\"playerId\" : \"%s\"}",
                "myself"
        );

        MyTask task = new MyTask(
                "http://10.0.2.2:8080/Advertisement_Server/GetAllPoint", // server服務的網址
                postJsonString, // 要傳給Server服務的字串，這邊為要新增的廣告Object
                "POST"
        );

        try {
            String result = task.execute().get();
            if(result != "") {
                Log.i(TAG, result);
                tvPoints.setText(result);
            } else {
                tvPoints.setText("ERROR");
            }
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    public void prepareGooglePay() {
        TPDMerchant tpdMerchant = new TPDMerchant();
        tpdMerchant.setMerchantName(getString(R.string.TapPay_MerchantName));
        tpdMerchant.setSupportedNetworks(CARD_TYPES);
        TPDConsumer tpdConsumer = new TPDConsumer();
        tpdConsumer.setPhoneNumberRequired(false);
        tpdConsumer.setShippingAddressRequired(false);
        tpdConsumer.setEmailRequired(false);

        tpdGooglePay = new TPDGooglePay(this, tpdMerchant, tpdConsumer);
        tpdGooglePay.isGooglePayAvailable(new TPDGooglePayListener() {
            @Override
            public void onReadyToPayChecked(boolean isReadyToPay, String msg) {
                Log.d(TAG, "Pay with Google availability : " + isReadyToPay);
                if (isReadyToPay) {
                    btn300.setEnabled(true);
                    btn800.setEnabled(true);
                    btn1000.setEnabled(true);
                } else {
                    btn300.setEnabled(false);
                    btn800.setEnabled(false);
                    btn1000.setEnabled(false);
                }
            }
        });
}

    private void handleViews() {
        tvPoints = findViewById(R.id.tvPoints);

        btn300 = findViewById(R.id.btn300);
        btn800 = findViewById(R.id.btn800);
        btn1000 = findViewById(R.id.btn1000);
        btn300.setEnabled(false);
        btn800.setEnabled(false);
        btn1000.setEnabled(false);

        btn300.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyCount = 100;
                total = 300;
                tpdGooglePay.requestPayment(TransactionInfo.newBuilder()
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        // 消費總金額
                        .setTotalPrice("300")
                        // 設定幣別
                        .setCurrencyCode("TWD")
                        .build(), LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        });

        btn800.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyCount = 300;
                total = 800;
                tpdGooglePay.requestPayment(TransactionInfo.newBuilder()
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        // 消費總金額
                        .setTotalPrice("800")
                        // 設定幣別
                        .setCurrencyCode("TWD")
                        .build(), LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        });

        btn1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyCount = 500;
                total = 1000;
                tpdGooglePay.requestPayment(TransactionInfo.newBuilder()
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        // 消費總金額
                        .setTotalPrice("1000")
                        // 設定幣別
                        .setCurrencyCode("TWD")
                        .build(), LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, String.valueOf(requestCode));
        Log.d(TAG, String.valueOf(resultCode));

        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE){
            switch (resultCode){
                case Activity.RESULT_OK:
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    getPrimeFromTapPay(paymentData);
                    postPointInfo();
                    break;
                case Activity.RESULT_CANCELED:
                    break;
                case AutoResolveHelper.RESULT_ERROR:
                    break;
            }
        }
    }
    private void showPaymentInfo(PaymentData paymentData) {
        try {
            JSONObject paymentDataJO = new JSONObject(paymentData.toJson());
            String cardDescription = paymentDataJO.getJSONObject("paymentMethodData").getString
                    ("description");
//            tvPaymentInfo.setText(cardDescription);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getPrimeFromTapPay(PaymentData paymentData) {
        tpdGooglePay.getPrime(
                paymentData,
                new TPDTokenSuccessCallback() {
                    @Override
                    public void onSuccess(String prime, TPDCardInfo tpdCardInfo) {
                        String text = "Your prime is " + prime
                                + "\n\nUse below cURL to proceed the payment : \n"
                                + ApiUtil.generatePayByPrimeCURLForPoint(prime,
                                getString(R.string.TapPay_PartnerKey),
                                getString(R.string.TapPay_MerchantID));
                        Log.d(TAG, text);
                        showMsgDialog("點數購買成功", true);
                    }
                },
                new TPDTokenFailureCallback() {
                    @Override
                    public void onFailure(int status, String reportMsg) {
                        String text = "TapPay getPrime failed. status: " + status + ", message: " + reportMsg;
                        Log.d(TAG, text);
                        showMsgDialog("點數購買失敗", false);
                    }
                });
    }

    private void postPointInfo() {
        // 把資料送到資料庫
        // 組合成後端要的格式

        String postJsonString = String.format(
                Locale.TAIWAN,
                "{\"playerId\" : \"%s\", \"bpAmount\":%d, \"bpCount\":%d }",
                PointActivity.this,
                total,
                buyCount
        );

        MyTask task = new MyTask(
                "http://10.0.2.2:8080/Advertisement_Server/BuyPoint", // server服務的網址
                postJsonString, // 要傳給Server服務的字串
                "POST"
        );

        try {
            task.execute();
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }

    }

    private void showMsgDialog(String idMessage, final boolean isSuccessful) {
        new AlertDialog.Builder(this)
                .setTitle("購買點數結果")
                .setMessage(idMessage)
                .setPositiveButton(R.string.ADbtn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isSuccessful) {
//                            TODO!!!!!  跳轉至會員個人頁面
//                            NavHostFragment.findNavController(fragment).navigate(R.id.adNowFragment);
                        }
                    }
                }).create().show();
    }
}
