package com.example.boardgame.advertisement_points;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiUtil {
    private static final String TAG = "TAG_ApiUtil";

    // 開啟MyTask (AsyncTask) 將交易資訊送至TapPay測試區
    public static String generatePayByPrimeCURLForSandBox(String prime, String partnerKey, String merchantId) {
        JSONObject paymentJO = new JSONObject();
        try {
            paymentJO.put("partner_key", partnerKey);
            paymentJO.put("prime", prime);
            paymentJO.put("merchant_id", merchantId);
            paymentJO.put("amount", AdNewFragment.total);
            paymentJO.put("currency", "TWD");
            paymentJO.put("order_number", "SN0001");
            paymentJO.put("details", "廣告" + AdNewFragment.adDays + "天");
            JSONObject cardHolderJO = new JSONObject();
            cardHolderJO.put("phone_number", "+886912345678");
            cardHolderJO.put("name", "");
            cardHolderJO.put("email", "ron@email.com");

            paymentJO.put("cardholder", cardHolderJO);
            Log.d(TAG, "paymentJO: " + paymentJO.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TapPay測試區網址
        String url = Common.TAPPAY_DOMAIN_SANDBOX + Common.TAPPAY_PAY_BY_PRIME_URL;
        MyTask myTask = new MyTask(url, paymentJO.toString(), partnerKey);
        String result = "";
        try {
            result = myTask.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }

    public static String generatePayByPrimeCURLForPoint(String prime, String partnerKey, String merchantId) {
        JSONObject paymentJO = new JSONObject();
        try {
            paymentJO.put("partner_key", partnerKey);
            paymentJO.put("prime", prime);
            paymentJO.put("merchant_id", merchantId);
            paymentJO.put("amount", PointActivity.buyCount);
            paymentJO.put("currency", "TWD");
            paymentJO.put("order_number", "SN0001");
            paymentJO.put("details", "點數儲值" + PointActivity.total + "元");
            JSONObject cardHolderJO = new JSONObject();
            cardHolderJO.put("phone_number", "+886912345678");
            cardHolderJO.put("name", "");
            cardHolderJO.put("email", "ron@email.com");

            paymentJO.put("cardholder", cardHolderJO);
            Log.d(TAG, "paymentJO: " + paymentJO.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TapPay測試區網址
        String url = Common.TAPPAY_DOMAIN_SANDBOX + Common.TAPPAY_PAY_BY_PRIME_URL;
        MyTask myTask = new MyTask(url, paymentJO.toString(), partnerKey);
        String result = "";
        try {
            result = myTask.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return result;
    }
}
