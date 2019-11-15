package com.example.boardgame.advertisement_points;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.boardgame.R;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class AdPhotoFragment extends Fragment {

    private static final int REQ_TAKE_PICTURE = 101;
    private static final int REQ_PICK_PICTURE = 102;
    private static final int REQ_CROP_PICTURE = 103;
    private static final String TAG = "TAG_MainFragment";
    private File file;
    private Activity activity;
    private ImageButton ibAlbum;
    private Uri contentUri;
    private View fragmentView;
    public static Uri newUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        return inflater.inflate(R.layout.fragment_ad_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentView = view;
        ImageButton ibCamera = view.findViewById(R.id.ibCamera);
        ibAlbum = view.findViewById(R.id.ibAlbum);

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (dir != null && !dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.e(TAG, getString(R.string.textDirNotCreated));
                        return;
                    }
                }
                file = new File(dir, "picture.jpg");
                contentUri = FileProvider.getUriForFile(
                        activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                } else {
                    Toast.makeText(activity, R.string.textNoCameraAppFound,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_PICK_PICTURE);
                } else {
                    Toast.makeText(activity, R.string.textNoImagePickerAppFound,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_PICK_PICTURE:
                    crop(data.getData());
                    break;
                case REQ_CROP_PICTURE:
                    Uri uri = data.getData();
                    if (uri != null) {
                        try {
                            newUri = uri;
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        Navigation.findNavController(fragmentView)
                                .navigate(R.id.action_adPhotoFragment_to_adNewFragment);
                    }
                    break;
            }
        }
    }

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
        intent.putExtra("aspectX", 284);
        intent.putExtra("aspectY", 110);
        // 設定圖片輸出寬高，0代表維持原尺寸
        intent.putExtra("outputX", 284);
        intent.putExtra("outputY", 110);
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
}
