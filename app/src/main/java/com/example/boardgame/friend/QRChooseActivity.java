package com.example.boardgame.friend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.boardgame.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRChooseActivity extends AppCompatActivity {

    private Button btnScan, btnShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrchoose);
        btnShow = findViewById(R.id.btnShow);
        btnScan = findViewById(R.id.btnScan);

        this.setTitle("QR-code加好友");

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRChooseActivity.this, ShowQRActivity.class);
                startActivity(intent);
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 IntentIntegrator integrator = new IntentIntegrator(QRChooseActivity.this);
//                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(ScanQRFragment.this);
                // Set to true to enable saving the barcode image and sending its path in the result Intent.
                integrator.setBarcodeImageEnabled(true);
                // Set to false to disable beep on scan.
                integrator.setBeepEnabled(false);
                // Use the specified camera ID.
                integrator.setCameraId(0);
                // By default, the orientation is locked. Set to false to not lock.
                integrator.setOrientationLocked(false);
                // Set a prompt to display on the capture screen.
                integrator.setPrompt("Scan a QR Code");
                // Initiates a scan
                integrator.initiateScan();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null && intentResult.getContents() != null) {
            Log.i("scan", "掃描成功");
            Toast.makeText(this, "掃描成功", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, ShowScanResultActivity.class);
            intent.putExtra("QRCode", intentResult.getContents());
            startActivity(intent);
//            tvResult.setText(intentResult.getContents());
        } else {
//            tvResult.setText(R.string.textResultNotFound);
        }
    }
}
