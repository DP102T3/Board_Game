package com.example.boardgame.friend;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.boardgame.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.example.boardgame.friend.qrcode.Contents;
import com.example.boardgame.friend.qrcode.QRCodeEncoder;

import static android.content.ContentValues.TAG;

public class ShowQRActivity extends AppCompatActivity {

    ImageView ivQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qr);
        ivQRCode = findViewById(R.id.ivQRCode);
        int dimension = getResources().getDisplayMetrics().widthPixels;

        // Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder("{\"frID\":\"myself\", " +
                "\"frNkName\":\"我自己\"}",null, Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(), dimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ivQRCode.setImageBitmap(bitmap);

        } catch (WriterException e) {
            Log.e(TAG, e.toString());
        }
    }
}