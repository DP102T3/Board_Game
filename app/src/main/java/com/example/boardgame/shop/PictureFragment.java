package com.example.boardgame.shop;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.MainActivity;
import com.example.boardgame.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;
import static com.example.boardgame.chat.Common.bitmap;


public class PictureFragment extends Fragment {


    private Activity activity;
    private static final String TAG = "TAG_MainFragment";
    private Button takePic, choisePic;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;
    private static final int PER_EXTERNAL_STORAGE = 0;
    private byte[] image;
    private ImageView ivShop;
    private Uri contentUri;
    private File file;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle("選擇照片");
        return inflater.inflate(R.layout.fragment_picture, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//====================================================返回前一頁=================================================================================


//====================================================拍照片====================================================================================

        takePic = view.findViewById(R.id.bt_Takepic);

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定存檔路徑
                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (file != null && !file.exists()) {
                    if (!file.mkdirs()) {
                        Common.showToast(getActivity(), R.string.textfileNotCreated);
                        return;
                    }
                }


                file = new File(file, "picture.jpg");
//=============================================原圖保存下來 並提供暫時的目錄路徑===================================================================

                contentUri = FileProvider.getUriForFile(

//=============================================getPackageName 看AndroidManifest取得名字是什麼====================================================
//==================================還要要在AndroidManifest建立說明文件provider 跟Xml建立provider_path============================================
                        activity, activity.getPackageName() + ".provider", file);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                } else {
                    Common.showToast(getActivity(), R.string.textNoCameraApp);

                }


            }
        });

//====================================================選照片===================================================================================


        choisePic = view.findViewById(R.id.bt_Choisepic);

        choisePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//===============================================意圖讓使用者挑照片?=====================================================================================
                Intent intent = new Intent(Intent.ACTION_PICK,
//===============================================照片來源是圖庫=========================================================================================
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//================================================請求代碼自己key的=====================================================================================
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_PICK_PICTURE);
                } else {
                    Common.showToast(getActivity(), R.string.textNoImagePickerAppFound);

                    Navigation.findNavController(choisePic).popBackStack();
                }
            }
        });
    }

//========================挑完照片onActivityResult會執行 requsetCode resultCode intent會帶過來=====================================================

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

//================================================使用者選擇ok=================================================================================
//================================================剪裁照片=================================================================================

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                    break;
                case REQ_CROP_PICTURE:

//================================================uri是使用者存照片的位置=======================================================================

                    Uri uri = intent.getData();

                    if (uri != null) {
                        try {
//==============================decodeStrem會把資料輸入串流的jpg轉成bitmap=======================================================================
                            bitmap = BitmapFactory.decodeStream(
                                    activity.getContentResolver().openInputStream(uri));

                            ByteArrayOutputStream out = new ByteArrayOutputStream();
//==============================轉成Jpg==================================================================================

                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            image = out.toByteArray();

                            Navigation.findNavController(takePic).popBackStack();

                        } catch (FileNotFoundException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    break;
            }
        }
    }

    //========================================================剪裁圖片的大小=======================================================================
    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri uri = Uri.fromFile(file);
        // 開啟截圖功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 授權讓截圖程式可以讀取資料
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 設定圖片來源與類型
        intent.setDataAndType(sourceImageUri, "image/*");
        // 設定要截圖
        intent.putExtra("crop", "true");
        // 設定截圖框大小，0代表user任意調整大小
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        // 設定圖片輸出寬高，0代表維持原尺寸
        intent.putExtra("outputX", 0);
        intent.putExtra("outputY", 0);
        // 是否保持原圖比例
        intent.putExtra("scale", true);
        // 設定截圖後圖片位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 設定是否要回傳值
        intent.putExtra("return-data", true);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // 開啟截圖activity
            startActivityForResult(intent, REQ_CROP_PICTURE);
        } else {
            Toast.makeText(activity, R.string.textNoImageCropAppFound,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // 隱藏 TabBar 及 BottomBar
        com.example.boardgame.MainActivity.changeBarsStatus(MainActivity.NEITHER_TAB_AND_BOTTOM);


        askExternalStoragePermission();
    }

    //=================================================詢問user是否同意相簿使用======================================================================
    private void askExternalStoragePermission() {
        String[] permissions = {
//===============================================存取外部儲存體的permissions=====================================================================
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        int result = ContextCompat.checkSelfPermission(activity, permissions[0]);

//=================================================如果不同意的話使用api的 permissions===========================================================
        if (result == PackageManager.PERMISSION_DENIED) {

//=================================================跳出user是否同意的對話框======================================================================
            requestPermissions(permissions, PER_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PER_EXTERNAL_STORAGE) {
            // 如果user不同意將資料儲存至外部儲存體的公開檔案，就將儲存按鈕設為disable
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(activity, R.string.textShouldGrant, Toast.LENGTH_SHORT).show();
//               如果user不同意 就不能按按鈕
                choisePic.setEnabled(false);
            } else {
                choisePic.setEnabled(true);
            }
        }
    }
}
