package com.example.boardgame.advertisement_points;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdHistoryFragment extends Fragment {

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<AdHistory> adHisList = getAdHisList();
        recyclerView.setAdapter(new AdHisAdapter(this, adHisList));
    }

    private List<AdHistory> getAdHisList(){
        List<AdHistory> adHisList = new ArrayList<>();
        // AdNow是 View Object, 用JSon轉換的object(資料)來填入View Object
        List<Advertisement> advertisementList;

        //1. 使用MyTask跟server接收資料 參考 Network Demo (postJsonToServer)
        MyTask task = new MyTask(
                "http://10.0.2.2:8080/Advertisement_Server/GetAdvertisement",
                "{\"shopId\": \"123\"}",
                null
        );

        try {
            String result = task.execute().get();
            Log.i("POST_RESULT", result);
            advertisementList = advertisementList(result);

            for (Advertisement advertisement: advertisementList) {
                String startDate = advertisement.getAdStart();

                String duration = "";

                if (!startDate.equals("")) {


                    String[] dayArray = startDate.split("/");

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(
                            Integer.parseInt(dayArray[0]),
                            Integer.parseInt(dayArray[1]) - 1,
                            Integer.parseInt(dayArray[2])
                    );
                    calendar.add(Calendar.DATE, advertisement.getAdBuyDate());

                    Date endDate = calendar.getTime();

                    if (endDate.compareTo(new Date()) > 0) {
                        continue;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
                    duration = startDate + " ~ " + sdf.format(endDate);
                }
                String status = "";
                String pic = advertisement.getAdPic();

                switch (advertisement.getAdState()){
                    case 0:
                        status = "未審核";
                        break;
                    case 1:
                        status = "已審核";
                        break;
                    case 2:
                        status = "已駁回";
                        break;
                }
                    AdHistory adHistory = new AdHistory(duration, status, pic);
                    adHisList.add(adHistory);
            }

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return adHisList;
    }

    private List<Advertisement> advertisementList(String json) {
        Gson gson = new Gson();

        AdvertisementListResult advertisementListResult = gson.fromJson(json, AdvertisementListResult.class);

        return advertisementListResult.getResult();
    }

    private class AdHisAdapter extends RecyclerView.Adapter<AdHisAdapter.MyViewHolder> {
        Context context;
        List<AdHistory> adHisList;

        public AdHisAdapter(AdHistoryFragment adHistoryFragment, List<AdHistory> adHisList) {
            context = adHistoryFragment.getContext();
            this.adHisList = adHisList;
        }

        @Override
        public int getItemCount() { return adHisList.size(); }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivNowPic;
            TextView tvDuration, tvStatus;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                ivNowPic = itemView.findViewById(R.id.ivNowPic);
                tvDuration = itemView.findViewById(R.id.tvDuration);
                tvStatus = itemView.findViewById(R.id.tvStatus);
            }
        }

        @NonNull
        @Override
        public AdHisAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.itemview_history, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int index) {
            final AdHistory adHistory = adHisList.get(index);
            viewHolder.tvDuration.setText(adHistory.getDuration());
            viewHolder.tvStatus.setText(adHistory.getStatus());
            if (adHistory.getAdPic() != null) {
                byte[] image = Base64.decode(adHistory.getAdPic(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                viewHolder.ivNowPic.setImageBitmap(bitmap);
            }
        }
    }
}
