package com.example.boardgame.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.boardgame.R;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageTask extends AsyncTask<Object, Integer, Bitmap> {
    private final static String TAG = "TAG_ImageTask";
    private String playerId,idType,  url;
    private int groupNo, imageSize;
    private WeakReference<ImageView> imageViewWeakReference;

    // Player 取單張圖片
    public ImageTask(String url, String playerId, int imageSize) {
        this(url, playerId, imageSize, null);
    }

    // Group 取單張圖片
    public ImageTask(String url, int groupNo, int imageSize) {
        this(url, groupNo, imageSize, null);
    }

    // Player 取多張圖片
    public ImageTask(String url, String playerId, int imageSize, ImageView imageView) {
        this.url = url;
        this.playerId = playerId;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
        this.idType = "String";
    }

    // Group 取多張圖片
    public ImageTask(String url, int groupNo, int imageSize, ImageView imageView) {
        this.url = url;
        this.groupNo = groupNo;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
        this.idType = "int";
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getImage");
        jsonObject.addProperty("playerId", "emptyStr");
        jsonObject.addProperty("imageSize", imageSize);
        jsonObject.addProperty("idType", idType);
        if(idType.equals("String")) {
            jsonObject.addProperty("imageId", playerId);
        }else if(idType.equals("int")) {
            jsonObject.addProperty("imageId", groupNo);
        }
        return getRemoteImage(url, jsonObject.toString());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = imageViewWeakReference.get();
        if (isCancelled() || imageView == null) {
            return;
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.no_image);
        }
    }

    private Bitmap getRemoteImage(String url, String jsonOut) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(jsonOut);
            Log.d(TAG, "output: " + jsonOut);
            bw.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                bitmap = BitmapFactory.decodeStream(
                        new BufferedInputStream(connection.getInputStream()));
            } else {
                Log.e(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }
}
