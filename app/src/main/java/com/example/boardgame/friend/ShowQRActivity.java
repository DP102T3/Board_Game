package com.example.boardgame.friend;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.example.boardgame.chat.Common;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.example.boardgame.friend.qrcode.Contents;
import com.example.boardgame.friend.qrcode.QRCodeEncoder;

import static android.content.ContentValues.TAG;

public class ShowQRActivity extends AppCompatActivity {

    ImageView ivQRCode;

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_NOR_BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qr);
        ivQRCode = findViewById(R.id.ivQRCode);
        int dimension = getResources().getDisplayMetrics().widthPixels;

//        FriendViewModel放入物件

        String myId = Common.loadPlayerId(this);

        // Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(myId,null, Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(), dimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ivQRCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            Log.e(TAG, e.toString());
        }
    }
}