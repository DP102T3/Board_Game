package com.example.boardgame.advertisement_points;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdNowFragment extends Fragment {
    private static final String TAG = "TAG_AdNowFragment";

    private RecyclerView recyclerView;

    @Override
    public void onStart() {
        super.onStart();
        // 顯示 TabBar 及 BottomBar
        MainActivity.changeBarsStatus(MainActivity.BOTH_TAB_AND_BOTTOM);
        // 置換 TabBar 的 menu
        MainActivity.setTabBar(MainActivity.TAB_ADVERTISEMENT);
        // 置換 BottomBar 的 menu
        MainActivity.setBottomBar(MainActivity.BOTTOM_SHOP);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad_now, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<AdNow> adNowList = getAdNowList();
        recyclerView.setAdapter(new AdNowAdapter(this, adNowList));
        Button btnAddNew = view.findViewById(R.id.btnAddNew);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_adNowFragment_to_adNewFragment);
            }
        });
//      TODO 整合要刪掉！！！！！！！！！！！！！！！！！！！
        Button btnToPoints = view.findViewById(R.id.btnToPoints);
        btnToPoints.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PointActivity.class);
                startActivity(intent);
            }
        });
    }



    private List<AdNow> getAdNowList() {
        List<AdNow> adNowList = new ArrayList<>();
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

            for (Advertisement advertisement : advertisementList) {
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

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);

                    calendar.add(Calendar.DATE, advertisement.getAdBuyDate());

                    Date endDate = calendar.getTime();

                    if (endDate.compareTo(new Date()) < -0.1) {
                        continue;
                    }

                    duration = startDate + " ~ " + sdf.format(endDate);
                }

                String status = "";
                String pic = advertisement.getAdPic();

                switch (advertisement.getAdState()) {
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

                AdNow adNow = new AdNow(duration, status, pic);
                adNowList.add(adNow);

            }

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return adNowList;
    }

    private List<Advertisement> advertisementList(String json) {
        Gson gson = new Gson();

        AdvertisementListResult advertisementListResult = gson.fromJson(json, AdvertisementListResult.class);

        return advertisementListResult.getResult();
    }

    private class AdNowAdapter extends RecyclerView.Adapter<AdNowAdapter.MyViewHolder> {
        Context context;
        List<AdNow> adNowList;

        public AdNowAdapter(AdNowFragment adNowFragment, List<AdNow> adNowList) {
            context = adNowFragment.getContext();
            this.adNowList = adNowList;
        }

        @Override
        public int getItemCount() {
            return adNowList.size();
        }

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
        public AdNowAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.itemview_now, viewGroup, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int index) {
            final AdNow adNow = adNowList.get(index);
            viewHolder.tvDuration.setText(adNow.getDuration());
            viewHolder.tvStatus.setText(adNow.getStatus());
            if (adNow.getAdPic() != null) {
                byte[] image = Base64.decode(adNow.getAdPic(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                viewHolder.ivNowPic.setImageBitmap(bitmap);
            }
        }
    }
}
