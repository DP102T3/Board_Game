package com.example.boardgame.group;

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
    private String url;
    private int id;
    private String playerId;
    private WeakReference<ImageView> imageViewWeakReference;

    // 取單張圖片
    public ImageTask(String url, int id) {
        this(url, id, null);
    }

    // 取完圖片後使用傳入的ImageView顯示，適用於顯示多張圖片
    public ImageTask(String url, int id,  ImageView imageView) {
        Log.d(TAG,"getImage");
        this.url = url;
        this.id = id;
        //this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    // 取完圖片後使用傳入的ImageView顯示，適用於顯示多張圖片(改用string取)
    public ImageTask(String url, String playerId,  ImageView imageView) {
        Log.d(TAG,"getImage");
        this.url = url;
        this.playerId = playerId;
        //this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getImage");
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("playerId", playerId);
        //jsonObject.addProperty("imageSize", imageSize);
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

    //把json轉成圖檔
    private Bitmap getRemoteImage(String url, String jsonOut) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true); // allow inputs
            connection.setDoOutput(true); // allow outputs
            connection.setUseCaches(false); // do not use a cached copy
            connection.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(jsonOut);
            Log.d(TAG, "output: " + jsonOut);
            bw.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                Log.d(TAG, "ImageTask response code: " + responseCode);
                bitmap = BitmapFactory.decodeStream(
                        new BufferedInputStream(connection.getInputStream()));
            } else {
                Log.d(TAG, "ImageTask response code: " + responseCode);
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
