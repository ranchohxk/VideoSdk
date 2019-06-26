package com.hyq.hm.videosdk.activity;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.hyq.hm.videosdk.R;
import com.hyq.hm.videosdk.camera.CameraOverlap;
import com.hyq.hm.videosdk.face.FaceManager;
import com.hyq.hm.videosdk.face.FaceOverlap;
import com.hyq.hm.videosdk.file.SharedPreferencesHelper;
import com.hyq.hm.videosdk.gles.GlesManager;
import com.hyq.hm.videosdk.gles.SaturationShader;
import com.hyq.hm.videosdk.gles.VertexShader;
import com.hyq.hm.videosdk.util.C;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import zeusees.tracking.Face;


public class WdseActivity extends Activity implements
        View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "WdseActivity";
    private float mCurSaturation;
    private float mCurVarExtra; //开发者说明： 这个控制B分量，来自人脸坐标  W*H
    private SharedPreferencesHelper mSharedPreferences;
    private MyRender render;
    private Bitmap bitmap;
    private boolean mInTracking = false;
    private long mLastDetectTime = 0;
    private PopupWindow popupWindow;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private GLSurfaceView mGLSurfaceView;
    private final FloatBuffer mVertexBuffer = ByteBuffer.allocateDirect(C.VERTEX.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(C.VERTEX);
    private int mPositionHandle;
    private int mProgram;
    FloatBuffer mUvTexVertexBuffer;
    private int mTexCoorHandle;
    private int mTexSamplerHandle;
    private int mSaturation;
    private int mVarExtra;
    private ImageButton mImageButtonTrackingStatus;
    private ImageView imageView;
    private Button mNext;
    private SeekBar seekBar;
    private SeekBar seekBar1;
    private Boolean click;
    private Boolean change;
    private Boolean isTracing;
    private FaceOverlap faceOverlap;
    private CameraOverlap cameraOverlap;
    private GlesManager mGlesManager;
    private FaceManager mFaceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFaceManager = FaceManager.getInstance(getApplicationContext());
        mGlesManager = GlesManager.getInstance(getApplicationContext());
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
        //Adjust pictures data
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.saturation);
        Log.d(TAG, "decoderResource");
        mImageButtonTrackingStatus = (ImageButton) findViewById(R.id.id_tracking_status);
        mImageButtonTrackingStatus.setBackgroundResource(R.drawable.tracking_no);
        isTracing = false;
        mImageButtonTrackingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTracing) {
                    mImageButtonTrackingStatus.setBackgroundResource(R.drawable.tracking_yes);
                    isTracing = true;
                    Log.d(TAG, "is no Tracing!");
                } else {
                    mImageButtonTrackingStatus.setBackgroundResource(R.drawable.tracking_no);
                    isTracing = false;
                    if (popupWindow != null && popupWindow.isShowing())
                        popupWindow.dismiss();
                }
            }
        });

        click = false;
        change = false;
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);
        mVertexBuffer.position(0);
        mUvTexVertexBuffer = ByteBuffer.allocateDirect(C.UV_TEX_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(C.UV_TEX_VERTEX);
        mUvTexVertexBuffer.position(0);
        cameraOverlap = new CameraOverlap(this);
        faceOverlap = new FaceOverlap();
        faceOverlap.setFaceListener(mOnFaceListener);
        //get value from cache
        int process = mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_FIRST);
        int process1 = mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_THIRD);// 取新值
        mCurSaturation = 1.0f;
        C.mCurVar1 = (float) (process / 100.f);
        C.mCurVar2 = (float) (mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_SECOND) / 100.f);//by AB
        mCurVarExtra = (float) (mSharedPreferences.getIntValueByKey(SharedPreferencesHelper.KEY_SATURATION_PARAM_THIRD) / 100.f);//by AB 20190123
        Log.i(TAG, "var1 =" + C.mCurVar1 + " var2 = " + C.mCurVar2 + " line:271");
        if (render == null)
            render = new MyRender();
        if (mGLSurfaceView == null) {
            mGLSurfaceView = (GLSurfaceView) findViewById(R.id.surfaceView);
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView.setRenderer(render);
            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(process);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar1.setProgress(process1);
        seekBar1.setOnSeekBarChangeListener(mSeekBar1ChangeListener);
        mNext = (Button) findViewById(R.id.next);
        mNext.setOnClickListener(this);
//        verifyStoragePermissions(this);
//        InitModelFiles();
        mFaceManager.setFaceMListener(new FaceManager.OnFaceCalListener() {

            @Override
            public void onFaceStatus(boolean status) {
                Log.d(TAG, "onFaceStatus.status:" + status);
                updateFaceStatus(status);
            }
        });
    }

    void InitModelFiles() {
        String assetPath = "AndroidOS";
        String sdcardPath = Environment.getExternalStorageDirectory()
                + File.separator + assetPath;
        Log.d(TAG, "sdcardPath:" + sdcardPath);
        copyFilesFromAssets(this, assetPath, sdcardPath);

    }

    public void copyFilesFromAssets(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                // directory
                File file = new File(newPath);
                if (!file.mkdir()) {
                    Log.d(TAG, "can't make folder");
                }
                for (String fileName : fileNames) {
                    copyFilesFromAssets(context, oldPath + "/" + fileName,
                            newPath + "/" + fileName);
                }
            } else {
                // file
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};


    public static void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            Log.d(TAG, "permission:" + permission);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        this.mGLSurfaceView.requestRender();
    }

    SeekBar.OnSeekBarChangeListener mSeekBar1ChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            float p = seekBar.getProgress() / 100.0f;// - 1.0f;
            if (seekBar.getId() == R.id.seekBar1) {
                mCurVarExtra = p;
                mSharedPreferences.saveIntValue(SharedPreferencesHelper.KEY_SATURATION_PARAM_THIRD, seekBar.getProgress());
                Log.i(TAG, " =" + mCurVarExtra + " var2 = " + seekBar.getProgress() + " line:686");
            }
            mGLSurfaceView.requestRender();
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


    FaceOverlap.OnFaceListener mOnFaceListener = new FaceOverlap.OnFaceListener() {
        @Override
        public void onFace(List<Face> faces) {
            if (null != mFaceManager) {
                mFaceManager.faceCalculate(faces, mInTracking, mGLSurfaceView);
            }
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
            this.mGLSurfaceView.requestRender();
        }
    }

    @Override
    protected void onDestroy() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if (faceOverlap != null) {
            faceOverlap.deleteTexture();
            faceOverlap.release();
        }
        if (cameraOverlap != null) {
            cameraOverlap.release();
        }
        super.onDestroy();
    }

    class MyRender implements GLSurfaceView.Renderer {
        public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
            GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
            faceOverlap.initSurfaceTexture();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cameraOverlap.openCamera(faceOverlap.getSurfaceTexture());
                    Log.d(TAG, "camera is opened");
                    cameraOverlap.setPreviewCallback(mPreviewCallback);
                }
            });
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            C.mCurVar7 = width;
            C.mCurVar8 = height;
            int[] mTexNames = new int[1];
            GLES20.glGenTextures(1, mTexNames, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexNames[0]);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            if (bitmap != null && !bitmap.isRecycled()) {
                Log.d(TAG, "bitmap.recycles");
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            } else {
                Log.d(TAG, "decodeResource1");
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.saturation);
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            }
            bitmap.recycle();
            mProgram = GLES20.glCreateProgram();
            int vertexShader = mGlesManager.loadShader(GLES20.GL_VERTEX_SHADER, VertexShader.VERTEX_SHADER);
            int saturateShader = mGlesManager.loadShader(GLES20.GL_FRAGMENT_SHADER, SaturationShader.frame_shader_saturation_two_parameters);
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, saturateShader);
            GLES20.glLinkProgram(mProgram);
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");//NO.2 use
            mTexCoorHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
            mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram, "s_texture");
            mSaturation = GLES20.glGetUniformLocation(mProgram, "saturation");
            C.mVar1 = GLES20.glGetUniformLocation(mProgram, "mVar1");
            C.mVar2 = GLES20.glGetUniformLocation(mProgram, "mVar2");
            C.mVar3 = GLES20.glGetUniformLocation(mProgram, "mVar3");
            C.mVar4 = GLES20.glGetUniformLocation(mProgram, "mVar4");
            C.mVar5 = GLES20.glGetUniformLocation(mProgram, "mVar5");
            C.mVar6 = GLES20.glGetUniformLocation(mProgram, "mVar6");
            C.mVar7 = GLES20.glGetUniformLocation(mProgram, "mVar7");
            C.mVar8 = GLES20.glGetUniformLocation(mProgram, "mVar8");
            C.mVar9 = GLES20.glGetUniformLocation(mProgram, "mVar9");
            C.mVar10 = GLES20.glGetUniformLocation(mProgram, "mVar10");
            mVarExtra = GLES20.glGetUniformLocation(mProgram, "mVarExtra");
        }

        public void onDrawFrame(GL10 gl) {
            mGlesManager.onDrawFrameImpl(gl, mProgram, mPositionHandle, mVertexBuffer, mTexCoorHandle, mUvTexVertexBuffer,
                    mTexSamplerHandle, mSaturation, mCurSaturation, mVarExtra, mCurVarExtra);
        }
    }

    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
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
        mGLSurfaceView.onResume();
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            faceOverlap.faceTrack1(bytes);
        }
    };

    private void updateFaceStatus(boolean tracking) {

        final boolean track_status = tracking;
        Log.e(TAG, "updateFaceStatus=" + tracking);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!track_status) {
                    Log.d(TAG, "School center");
                    mImageButtonTrackingStatus.setBackgroundResource(R.drawable.tracking_no);
                } else {
                    Log.d(TAG, "tracking");
                    mImageButtonTrackingStatus.setBackgroundResource(R.drawable.tracking_yes);
                }

            }
        });
        mInTracking = tracking;
    }


}
