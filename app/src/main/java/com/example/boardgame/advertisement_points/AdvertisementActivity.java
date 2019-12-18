package com.example.boardgame.advertisement_points;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.android.gms.wallet.PaymentData;
import com.google.gson.Gson;

import java.util.Locale;

import tech.cherri.tpdirect.api.TPDCardInfo;
import tech.cherri.tpdirect.api.TPDConsumer;
import tech.cherri.tpdirect.api.TPDGooglePay;
import tech.cherri.tpdirect.api.TPDMerchant;
import tech.cherri.tpdirect.callback.TPDTokenFailureCallback;
import tech.cherri.tpdirect.callback.TPDTokenSuccessCallback;

import static com.example.boardgame.advertisement_points.Common.CARD_TYPES;

public class AdvertisementActivity extends AppCompatActivity {
    private static final String TAG = "TAG_AdvertisementActivity";
    private Activity activity;
    private FragmentManager manager;
//    private FragmentTransaction transaction;
    private Fragment fragment;
    private TPDGooglePay tpdGooglePay;

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
        MainActivity.setTabBar(R.menu.tab_menu_advertisement);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = getSupportFragmentManager();
        fragment = manager.findFragmentById(R.id.fragment_friend);
        activity = this;
        Log.d(TAG, "========== in AdvertisementActivity onCreate ==========");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("AdvertisementActivity", "onActivityResult: " + requestCode);

        if (requestCode == 101) {

            switch (resultCode) {

                case Activity.RESULT_OK:
                    System.out.println("OK!");
                    prepareGooglePay();
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    getPrimeFromTapPay(paymentData);
                    break;

                case Activity.RESULT_CANCELED:
                    System.out.println("Cancelled!");
                    break;

                default:
                    break;
            }
        }
    }

//    public void onButtonClick(View view){
//        transaction = manager.beginTransaction();
//
//        switch (view.getId()){
//
//            case R.id.btnNow:
//                NavHostFragment.findNavController(fragment).navigate(R.id.adNowFragment);
//                break;
//
//            case R.id.btnPast:
//                NavHostFragment.findNavController(fragment).navigate(R.id.adHistoryFragment);
//                break;
//        }
//        transaction.commit();
//    }

    private void getPrimeFromTapPay(PaymentData paymentData) {
        tpdGooglePay.getPrime(
                paymentData,
                new TPDTokenSuccessCallback() {
                    @Override
                    public void onSuccess(String prime, TPDCardInfo tpdCardInfo) {

                        postAdvertisementInfo();

                        String text = "Your prime is " + prime
                                + "\n\nUse below cURL to proceed the payment : \n"
                                + ApiUtil.generatePayByPrimeCURLForSandBox(prime,
                                getString(R.string.TapPay_PartnerKey),
                                getString(R.string.TapPay_MerchantID));
                        Log.d("onSuccess", text);

                        showMsgDialog(R.string.ADmessage, true);
                    }
                },
                new TPDTokenFailureCallback() {
                    @Override
                    public void onFailure(int status, String reportMsg) {
                        String text = "TapPay getPrime failed. status: " + status + ", message: " + reportMsg;
                        Log.d("onFailure", text);

                        showMsgDialog(R.string.ADmessageFailed, false);
                    }
                });
    }

    private void postAdvertisementInfo() {
        // 把資料送到資料庫，從AdNewFragment上的欄位取得
        // 組合成後端要的格式

        int adBuyDate = AdNewFragment.adDays;
        String shopId = com.example.boardgame.chat.Common.loadPlayerId(activity);
        String adStart = AdNewFragment.startDate();
        int adAmount = (int)(AdNewFragment.total);
        int adState = 0;
        String imageBase64String = AdNewFragment.imageBase64String;

        String postJsonString = String.format(
                Locale.TAIWAN,
                "{\"shopId\" : \"%s\", \"adStart\":\"%s\", " +
                        "\"adBuyDate\":%d, \"adAmount\":\"%d\",\"adState\":\"%d\", \"adPic\":\"%s\" }",
                shopId,
                adStart,
                adBuyDate,
                adAmount,
                adState,
                imageBase64String
        );

        MyTask task = new MyTask(
                "http://10.0.2.2:8080/Advertisement_Server/CreateAdvertisement", // server服務的網址
                postJsonString, // 要傳給Server服務的字串，這邊為要新增的廣告Object
                "POST"
        );

        try {
            String result = task.execute().get();  //拿json字串資料
            Gson gson = new Gson();
            gson.fromJson(result, AdvertisementListResult.class);
            AdvertisementListResult advertisementListResult = gson.fromJson(result, AdvertisementListResult.class);
            Log.i("AdvertisementListResult", advertisementListResult.toString());
            Log.i("POST_RESULT", result);
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    private void showMsgDialog(int idMessage, final boolean isSuccessful) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.ADtitle)
                .setMessage(idMessage)
                .setPositiveButton(R.string.ADbtn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isSuccessful) {
                            NavHostFragment.findNavController(fragment).navigate(R.id.adNowFragment);
                        }
                    }
                }).create().show();
    }

    private void prepareGooglePay() {
        TPDMerchant tpdMerchant = new TPDMerchant();
        tpdMerchant.setMerchantName(getString(R.string.TapPay_MerchantName));
        tpdMerchant.setSupportedNetworks(CARD_TYPES);
        // 設定客戶填寫項目
        TPDConsumer tpdConsumer = new TPDConsumer();
        tpdConsumer.setPhoneNumberRequired(false);
        tpdConsumer.setShippingAddressRequired(false);
        tpdConsumer.setEmailRequired(false);

        tpdGooglePay = new TPDGooglePay(this, tpdMerchant, tpdConsumer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.i("InF", String.valueOf(id));
        switch (id){
            case R.id.action_note:
                Navigation.findNavController(this,0).navigate(R.id.shopNotificationListFragment);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
