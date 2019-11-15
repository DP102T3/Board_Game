package com.example.boardgame.advertisement_points;


import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;

import tech.cherri.tpdirect.api.TPDCard;


public class Common {

    public static final String TAPPAY_DOMAIN_SANDBOX = "https://sandbox.tappaysdk.com";
    public static final String TAPPAY_PAY_BY_PRIME_URL = "/tpc/payment/pay-by-prime";
    public static final TPDCard.CardType[] CARD_TYPES = new TPDCard.CardType[]{
            TPDCard.CardType.Visa
            , TPDCard.CardType.MasterCard
            , TPDCard.CardType.JCB
            , TPDCard.CardType.AmericanExpress
    };

    public final static String URL = "http://10.0.2.2:8080/ImageToJson_Login_Web";
    public final static String PREF_FILE = "preference";
    private static final String TAG = "Common";

    public static byte[] bitmapToPNG(Bitmap srcBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 轉成PNG不會失真，所以quality參數值會被忽略
        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // 設定長寬不超過scaleSize
    public static Bitmap downSize(Bitmap srcBitmap, int newSize) {
        // 如果欲縮小的尺寸過小，就直接定為128
        if (newSize <= 50) {
            newSize = 128;
        }
        int srcWidth = srcBitmap.getWidth();
        int srcHeight = srcBitmap.getHeight();
        String text = "source image size = " + srcWidth + "x" + srcHeight;
        Log.d(TAG, text);
        int longer = Math.max(srcWidth, srcHeight);

        if (longer > newSize) {
            double scale = longer / (double) newSize;
            int dstWidth = (int) (srcWidth / scale);
            int dstHeight = (int) (srcHeight / scale);
            srcBitmap = Bitmap.createScaledBitmap(srcBitmap, dstWidth, dstHeight, false);
            System.gc();
            text = "\nscale = " + scale + "\nscaled image size = " +
                    srcBitmap.getWidth() + "x" + srcBitmap.getHeight();
            Log.d(TAG, text);
        }
        return srcBitmap;
    }
}
