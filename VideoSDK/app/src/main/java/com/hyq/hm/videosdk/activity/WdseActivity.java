package com.hyq.hm.videosdk.activity;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.hyq.hm.videosdk.R;
import com.hyq.hm.videosdk.face.FaceManager;
import com.hyq.hm.videosdk.file.SharedPreferencesHelper;
import com.hyq.hm.videosdk.impl.OnAuthCallListener;
import com.hyq.hm.videosdk.impl.OnFaceSListener;
import com.hyq.hm.videosdk.util.C;


public class WdseActivity extends Activity implements
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "WdseActivity";
    private SharedPreferencesHelper mSharedPreferences;
    private Bitmap bitmap;
    private boolean mInTracking = false;
    private GLSurfaceView mGLSurfaceView;
    private ImageButton mImageButtonTrackingStatus;
    private ImageView imageView;
    private Button mNext;
    private SeekBar seekBar;
    private SeekBar seekBar1;
    private Boolean click;
    private Boolean change;
    private Boolean isTracing;
    private FaceManager mPictureManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wdse);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        if (mSharedPreferences == null)
            mSharedPreferences = new SharedPreferencesHelper(this);
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.saturation);
        mImageButtonTrackingStatus = (ImageButton) findViewById(R.id.id_tracking_status);
        mImageButtonTrackingStatus.setBackgroundResource(R.drawable.tracking_no);
        isTracing = false;
        mImageButtonTrackingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTracing) {
                    mImageButtonTrackingStatus.setBackgroundResource(R.drawable.tracking_yes);
                    isTracing = true;
                } else {
                    mImageButtonTrackingStatus.setBackgroundResource(R.drawable.tracking_no);
                    isTracing = false;
                }
            }
        });

        click = false;
        change = false;
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);
        //get value from cache
        int process = mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_FIRST);
        int process1 = mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_THIRD);// 取新值
        C.mCurVar1 = (float) (process / 100.f);
        C.mCurVar2 = (float) (mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_SECOND) / 100.f);//by AB
        C.mCurVarExtra = (float) (mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_THIRD) / 100.f);//by AB 20190123
        Log.i(TAG, "var1 =" + C.mCurVar1 + " var2 = " + C.mCurVar2 + " line:271");
        //  mPictureManager = PictureManager.getInstance(getApplicationContext());
        mPictureManager = new FaceManager(getApplicationContext(), null);
        mPictureManager.setBitmap(bitmap);
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.surfaceView);
        mPictureManager.auth(new OnAuthCallListener() {
            @Override
            public void onAuthSucess() {
                Log.d(TAG, "auth sucess");
            }

            @Override
            public void onAuthFail(int code) {
                Log.e(TAG, "auth fail");

            }
        });
        /**
         * 传递surfaceview
         */
        mPictureManager.init(mGLSurfaceView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(process);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar1.setProgress(process1);
        seekBar1.setOnSeekBarChangeListener(mSeekBar1ChangeListener);
        mNext = (Button) findViewById(R.id.next);
        mNext.setOnClickListener(this);
        /**
         * 人脸识别到回调
         */
        mPictureManager.setFaceSListener(new OnFaceSListener() {

            @Override
            public void onFaceStatus(boolean status) {
//                Log.d(TAG, "onFaceStatus.status:" + status);
                /**
                 * 应用可处理自己的交互
                 */
                updateFaceStatus(status);
            }
        });
    }


    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!change) {
            if (!this.click) {
                this.mSharedPreferences.saveIntValue(SharedPreferencesHelper.KEY_SATURATION_PARAM_FIRST, progress);
                C.mCurVar1 = (float) (progress / 100.0);
                Log.i(TAG, "var1 =" + C.mCurVar1 + " var2 = " + C.mCurVar2 + " click=" + this.click);
            } else if (this.click) {
                this.mSharedPreferences.saveIntValue(SharedPreferencesHelper.KEY_SATURATION_PARAM_SECOND, progress);
                C.mCurVar2 = (float) (progress / 100.0);
                Log.i(TAG, "var1 =" + C.mCurVar1 + " var2 = " + C.mCurVar2 + " click=" + this.click);
            }
        } else {
            change = false;
        }
        mPictureManager.requestRender(mGLSurfaceView);
    }

    SeekBar.OnSeekBarChangeListener mSeekBar1ChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            float p = seekBar.getProgress() / 100.0f;// - 1.0f;
            if (seekBar.getId() == R.id.seekBar1) {
                C.mCurVarExtra = p;
                mSharedPreferences.saveIntValue(SharedPreferencesHelper.KEY_SATURATION_PARAM_THIRD, seekBar.getProgress());
                Log.i(TAG, " =" + C.mCurVarExtra + " var2 = " + seekBar.getProgress() + " line:686");
            }
            mPictureManager.requestRender(mGLSurfaceView);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.d(TAG, "onStopTrackingTouch");
        }

    };

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    public void onClick(View view) {
        int ID = view.getId();
        if (ID == R.id.next) {
            if (!click) {
                mNext.setText("Back");
                int process = mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_SECOND);
                C.mCurVar1 = (float) (mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_FIRST) / 100.f);
                C.mCurVar2 = (float) (process / 100.f);
                int proc1 = (int) (C.mCurVar2 * 100.f);
                Log.i(TAG, "var1 =" + C.mCurVar1 + " var2 = " + C.mCurVar2 + " proc1: " + proc1);
                change = true;
                seekBar.setProgress(proc1);
                click = true;
                seekBar1.setVisibility(View.INVISIBLE);
            } else if (click) {
                mNext.setText("Next");
                int process = mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_FIRST);
                C.mCurVar2 = (float) (mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_SECOND) / 100.f);
                C.mCurVar1 = (float) (process / 100.f);
                int proc2 = (int) (C.mCurVar1 * 100.f);
                Log.i(TAG, "var1 =" + C.mCurVar1 + " var2 = " + C.mCurVar2 + " proc2: " + proc2);
                change = true;
                seekBar.setProgress(proc2);
                click = false;
                seekBar1.setVisibility(View.VISIBLE);
            }
            mPictureManager.requestRender(mGLSurfaceView);
        }
    }

    @Override
    protected void onDestroy() {
        if (null != mPictureManager) {
            mPictureManager.onDestory();
            mPictureManager = null;
        }
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
        mPictureManager.onPause(mGLSurfaceView);
    }

    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
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
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        mPictureManager.onResume(mGLSurfaceView);
    }


    private void updateFaceStatus(boolean tracking) {
//        Log.d(TAG, "updateFaceStatus");
        final boolean track_status = tracking;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!track_status) {
                    mImageButtonTrackingStatus.setBackgroundResource(R.drawable.tracking_no);
                } else {
                    mImageButtonTrackingStatus.setBackgroundResource(R.drawable.tracking_yes);
                }
            }
        });
        mInTracking = tracking;

    }


}
