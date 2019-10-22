package com.example.boardgame.shop;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CommonTask extends AsyncTask<Void, Void, String> {

    private  final  String TAG = "TAG_CommonTask";
    private String url, outStr;


    public CommonTask(String url, String outStr) {
        this.url = url;
        this.outStr = outStr;
    }

    @Override
    protected String doInBackground(Void... voids)  {
        return getRemoteData();
    }
    private String getRemoteData(){
        HttpURLConnection connection = null;
        StringBuilder inStr = new StringBuilder();
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "UTF-8");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(outStr);
            Log.d(TAG, "output" + outStr);
            bw.close();

            int responseCode = connection.getResponseCode();
            if(responseCode == 200) {
                Log.d(TAG, "response code: " + responseCode);
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null){
                    inStr.append(line);
                }
            }else {
                Log.d(TAG, "response code: " + responseCode);
            }

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
        Log.d(TAG, "input " + inStr);
        return inStr.toString();

    }
}