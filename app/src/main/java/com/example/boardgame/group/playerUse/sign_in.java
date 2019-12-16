package com.example.boardgame.group.playerUse;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.R;
import com.example.boardgame.group.Common;
import com.example.boardgame.group.CommonTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.boardgame.group.Common.playerId;


public class sign_in extends Fragment {
    private static final String TAG = "TAG_SignInFragment";
    private Activity activity;
    private MapView mapView;
    private GoogleMap map;//控制map的核心物件
    private String commandStr, shopLocation,shopAddress;
    private double myLocationLongitude,myLocationLatitude,shopLocationLongitude,shopLocationLatitude;
    private LocationManager locationManager;
    private Location location;
    private Button btSignIn;
    private int shopId,groupNo;
    private CommonTask signIgTask;

    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;


    private static final int PER_ACCESS_LOCATION = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        shopId = getArguments() != null ? getArguments().getInt("shopId") : -1;
        groupNo = getArguments() != null ? getArguments().getInt("groupNo") : -1;

        commandStr = LocationManager.GPS_PROVIDER;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_sign_in, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);//mapView一定要onCreat
        btSignIn=view.findViewById(R.id.btSignIn);

        mapView.getMapAsync(new OnMapReadyCallback() {//想取得google map的圖資
            //只有主執行緒可以控制ui元件，因此不能用主執行緒去做可能需等待很久的動作，需建立一個新的執行緒來做此動作（Async開新執行緒）
            @Override
            public void onMapReady(GoogleMap googleMap) {//只要新執行緒拿到資料，會將資料轉給主執行緒，然後自動呼叫onMapReady執行
                map = googleMap;
                showMyLocation();

            }
        });

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationManager = (LocationManager)activity.getSystemService(activity.LOCATION_SERVICE);

                if (ActivityCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // here to request the missing permissions, and then overriding
                    ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSION_ACCESS_COARSE_LOCATION);
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                location = locationManager.getLastKnownLocation(commandStr);
                myLocationLongitude=location.getLongitude();//經度
                myLocationLatitude=location.getLatitude();//緯度
                Log.d(TAG,"Longitude="+myLocationLongitude+" Latitude="+myLocationLatitude);

                if (Common.networkConnected(activity)) {
                    String url = Common.URL_SERVER + "ShopServlet";
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action", "SignIn");
                    jsonObject.addProperty("shopId", shopId);
                    String jsonOut = jsonObject.toString();
                    signIgTask= new CommonTask(url, jsonOut);
                    try {
                        shopAddress = signIgTask.execute().get();
                        Log.d(TAG,"shopAddress="+shopAddress);
                        Geocoder geocoder = new Geocoder(activity);
                        List<Address> addressList = null;

                        try {
                            addressList= geocoder.getFromLocationName(shopAddress, 1);
                            Address address=addressList.get(0);//取第一個
                            Log.d(TAG,"address 1 ="+address);
                            shopLocationLongitude=address.getLongitude();
                            shopLocationLatitude=address.getLatitude();
                            Log.d(TAG,"shopLocationLongitude="+shopLocationLongitude);
                            Log.d(TAG,"shopLocationLatitude="+shopLocationLatitude);

                            float[] results = new float[1];
                            Location.distanceBetween(myLocationLatitude,
                                    myLocationLongitude, shopLocationLatitude,
                                    shopLocationLongitude, results);
                            double distance=results[0];
                            Log.d(TAG,"distance="+distance);
                            if(distance<50){//測試用50公尺，最後要改成10公尺
                                if (Common.networkConnected(activity)) {
                                    String url2 = Common.URL_SERVER + "JoinMemberServlet";
                                    JsonObject jsonObject2 = new JsonObject();
                                    jsonObject.addProperty("action", "signInInput");
                                    jsonObject.addProperty("groupNo", groupNo);
                                    jsonObject.addProperty("playerId", playerId);

                                    int count = 0;
                                    try {
                                        String result = new CommonTask(url2, jsonObject.toString()).execute().get();
                                        count = Integer.valueOf(result);
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (count == 0) {
                                        Common.showToast(getActivity(), R.string.textSignInError);

                                    }else {
                                        Common.showToast(getActivity(), R.string.textSignInSuccess);
                                        Navigation.findNavController(view).popBackStack();
                                    }

                                }else {
                                    Common.showToast(getActivity(), R.string.textNoNetwork);
                                }

                            }else {
                                Common.showToast(activity, R.string.textSignInError);
                                return;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                }

            }
        });





    }

    @Override
    public void onStart() {
        super.onStart();
        // 在Fragment生命週期方法內呼叫對應的MapView方法
        mapView.onStart();//mapView一定要onStart（其實也可以在onViewCreated那段一起呼叫onCreat和onStart，一定要呼叫這兩個方法才能正常呈現google map
        askAccessLocationPermission();


    }

    private void askAccessLocationPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        int result = ContextCompat.checkSelfPermission(activity, permissions[0]);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(permissions, PER_ACCESS_LOCATION);
        }
    }

    private void showMyLocation() {
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {//檢查使用者是否同意使用定位
            map.setMyLocationEnabled(true);//顯示定位紐（十字紐

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        showMyLocation();
    }



}
