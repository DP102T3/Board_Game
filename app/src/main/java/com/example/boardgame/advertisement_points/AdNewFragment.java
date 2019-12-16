package com.example.boardgame.advertisement_points;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tech.cherri.tpdirect.api.TPDConsumer;
import tech.cherri.tpdirect.api.TPDGooglePay;
import tech.cherri.tpdirect.api.TPDMerchant;
import tech.cherri.tpdirect.api.TPDServerType;
import tech.cherri.tpdirect.api.TPDSetup;
import tech.cherri.tpdirect.callback.TPDGooglePayListener;

import static com.example.boardgame.advertisement_points.Common.CARD_TYPES;

public class AdNewFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    private Activity activity;
    private TextView tvDate, tvDays, tvDuration, tvPayment;
    private Button btnSubmit;
    private static int nYear, nMonth, nDay;
    public static double total;
    public static int adDays;
    public static String imageBase64String;
    private Button btnAddPic;
    private SeekBar seekBar;
    private DatePickerDialog dialog;
    private Calendar calendar = Calendar.getInstance();
    private static final String TAG = "TAG_MainActivity";
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 101;
    private TPDGooglePay tpdGooglePay;


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        dialog = new DatePickerDialog(activity, AdNewFragment.this,
                AdNewFragment.nYear, AdNewFragment.nMonth, AdNewFragment.nDay);
        dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad_new, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AdPhotoFragment.newUri = null;
        btnAddPic.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border));
        btnAddPic.setText(getString(R.string.tvAddNew));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvDays = view.findViewById(R.id.tvDays);
        tvDuration = view.findViewById(R.id.tvDuration);
        seekBar = view.findViewById(R.id.seekBar);
        tvDate = view.findViewById(R.id.tvDate);
        tvPayment = view.findViewById(R.id.tvPayment);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnAddPic = view.findViewById(R.id.btnAddPic);

//********AddPicture********
        btnAddPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_adNewFragment_to_adPhotoFragment);
            }
        });

        if (AdPhotoFragment.newUri != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(
                        activity.getContentResolver().openInputStream(AdPhotoFragment.newUri));
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Drawable drawable = new BitmapDrawable(bitmap);
                btnAddPic.setBackground(drawable);
                btnAddPic.setText("");

                byte[] image = Common.bitmapToPNG(bitmap);
                imageBase64String = Base64.encodeToString(image, Base64.DEFAULT);

            } catch (IOException e){
                Log.v("TAG", e.toString());
            }
        }

//********DatePicker********
        showNow();
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

//********SeekBar********
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int days = seekBar.getProgress();
                String strDays = days*10 + "天";
                tvDays.setText(strDays);
                changeDuration();
                payRate();
            }
        });
        changeDuration();
        payRate();

        handleViews();

        Log.d(TAG, "SDK version is " + TPDSetup.getVersion());

        TPDSetup.initInstance(getContext().getApplicationContext(),
                Integer.parseInt(getString(R.string.TapPay_AppID)),
                getString(R.string.TapPay_AppKey),
                TPDServerType.Sandbox);

        prepareGooglePay();
    }
//!!!!!!!!onViewCreated ENDS!!!!!!!!
//********DatePicker part 2********
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        AdNewFragment.nYear = year;
        AdNewFragment.nMonth = month;
        AdNewFragment.nDay = dayOfMonth;
        changeDuration();
        updateDisplay();
    }

    private void showNow() {
        nYear = calendar.get(Calendar.YEAR);
        nMonth = calendar.get(Calendar.MONTH);
        nDay = calendar.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
    }

    public static String startDate(){
        return String.format(Locale.TAIWAN,"%d/%d/%d", nYear, nMonth + 1, nDay);
    }

    private void updateDisplay() {
        tvDate.setText(startDate());
    }

//********Duration********

    private void changeDuration(){

        Calendar calendar = Calendar.getInstance();
        calendar.set(nYear, nMonth, nDay);
        calendar.add(Calendar.DATE, getSeekBarProgress()*10 - 1);

        Date endDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
        String duration = startDate() + " ~ " + sdf.format(endDate);

        if(getSeekBarProgress() != 0) {
            btnSubmit.setEnabled(true);
            tvDuration.setText(duration);
        }
        else {
            btnSubmit.setEnabled(false);
            duration = startDate() + " ~ " + startDate();
            tvDuration.setText(duration);
        }
    }

    private int getSeekBarProgress(){
        return seekBar.getProgress();
    }

//********SetPaymentAmount********

    private void payRate(){
         adDays = getSeekBarProgress()* 10;
         int dayRate = 100;
         double discount;

         switch (adDays){
             case 10:
                 discount = 0.9;
                 break;

             case 20:
                 discount = 0.8;
                 break;

             case 30:
                 discount = 0.7;
                 break;
             default:
                 discount = 0;
                 break;
         }
         total = dayRate * adDays * discount;
         String money = "$ " + Math.round(total) ;
         tvPayment.setText(money);
    }

//********MobilePayment********

    private void handleViews() {
        btnSubmit.setEnabled(false);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpdGooglePay.requestPayment(TransactionInfo.newBuilder()
                        .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                        .setTotalPrice(String.valueOf(total))
                        .setCurrencyCode("TWD")
                        .build(), LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        });
    }

    public void prepareGooglePay() {
        TPDMerchant tpdMerchant = new TPDMerchant();
        tpdMerchant.setMerchantName(getString(R.string.TapPay_MerchantName));
        tpdMerchant.setSupportedNetworks(CARD_TYPES);
        TPDConsumer tpdConsumer = new TPDConsumer();
        tpdConsumer.setPhoneNumberRequired(false);
        tpdConsumer.setShippingAddressRequired(false);
        tpdConsumer.setEmailRequired(false);

        tpdGooglePay = new TPDGooglePay(getActivity(), tpdMerchant, tpdConsumer);
        tpdGooglePay.isGooglePayAvailable(new TPDGooglePayListener() {
            @Override
            public void onReadyToPayChecked(boolean isReadyToPay, String msg) {
                Log.d(TAG, "Pay with Google availability : " + isReadyToPay);
                if (isReadyToPay) {
                    btnSubmit.setEnabled(true);
                } else {
                    btnSubmit.setEnabled(false);
                }
            }
        });
    }
}
