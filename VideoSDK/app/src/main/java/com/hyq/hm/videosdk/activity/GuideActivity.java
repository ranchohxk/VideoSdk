package com.hyq.hm.videosdk.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyq.hm.videosdk.R;
import com.hyq.hm.videosdk.file.FileUtils;
import com.hyq.hm.videosdk.file.SharedPreferencesHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 海米 on 2018/6/7.
 */

public class GuideActivity extends AppCompatActivity {
    private String[] denied;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};

    private SharedPreferencesHelper sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        sharedPreferences = new SharedPreferencesHelper(this,"videoSDK");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            View decorView = getWindow().getDecorView();
            int mHideFlags =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(mHideFlags);

            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (PermissionChecker.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                    list.add(permissions[i]);
                }
            }
            if (list.size() != 0) {
                denied = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    denied[i] = list.get(i);
                }
                ActivityCompat.requestPermissions(this, denied, 5);
            } else {
                init();
            }
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            init();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5) {
            boolean isDenied = false;
            for (int i = 0; i < denied.length; i++) {
                String permission = denied[i];
                for (int j = 0; j < permissions.length; j++) {
                    if (permissions[j].equals(permission)) {
                        if (grantResults[j] != PackageManager.PERMISSION_GRANTED) {
                            isDenied = true;
                            break;
                        }
                    }
                }
            }
            if (isDenied) {
                Toast.makeText(this, "请开启权限", Toast.LENGTH_SHORT).show();
            } else {
                init();

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void init(){
        FileUtils.AppPath = getFilesDir().getAbsolutePath();
        int version = (int) sharedPreferences.getSharedPreference("Version",0);
        if(version < FileUtils.Version){
            File fv = new File(FileUtils.AppPath+ "/"+FileUtils.PathMain);
            if (!fv.exists()){
                fv.mkdir();
            }
            sharedPreferences.put("Version", FileUtils.Version);
            deleteFolderFile(fv);
            copyVideo();
        }else{
            ImageView imageView = findViewById(R.id.guide_image);
            imageView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            },1000);
        }
    }
    private void copyVideo(){
        FileUtils.getInstance(this).copyAssetsToSD("video",FileUtils.PathMain+"/"+FileUtils.PathVideo).setFileOperateCallback(new FileUtils.FileOperateCallback() {
            @Override
            public void onSuccess(List<String> files) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        copyModel();
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                // TODO: 文件复制失败时，主线程回调
            }
        });
    }
    private void copyModel(){
        FileUtils.getInstance(this).copyAssetsToSD("models",FileUtils.PathMain+"/"+FileUtils.PathModel).setFileOperateCallback(new FileUtils.FileOperateCallback() {
            @Override
            public void onSuccess(List<String> files) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                // TODO: 文件复制失败时，主线程回调
            }
        });
    }

    private void deleteFolderFile(File file){
        if (file.isDirectory()){
            File files[] = file.listFiles();
            if(files.length != 0){
                for (File f : files){
                    deleteFolderFile(f);
                }
            }
        }else{
            file.delete();
        }
    }

}
