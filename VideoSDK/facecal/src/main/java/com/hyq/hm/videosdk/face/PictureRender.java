package com.hyq.hm.videosdk.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.hyq.hm.videosdk.camera.CameraOverlap;
import com.hyq.hm.videosdk.gles.GlesManager;
import com.hyq.hm.videosdk.gles.SaturationShader;
import com.hyq.hm.videosdk.gles.VertexShader;
import com.hyq.hm.videosdk.util.C;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author hxk <br/>
 * 功能：
 * 创建日期   2019/6/26
 * 修改者：
 * 修改日期：
 * 修改内容: 加密
 */
public class PictureRender implements GLSurfaceView.Renderer {
    private static final String TAG = "FaceRender";
    private GlesManager mGlesManager;
    private FaceOverlap mFaceOverlap;
    private OnFaceRenderListener mOnFaceRenderListener;
    private int mTexCoorHandle;
    private int mTexSamplerHandle;
    private int mSaturation;
    private int mVarExtra;
    private int mProgram;
    private int mPositionHandle;
    private final FloatBuffer mVertexBuffer = ByteBuffer.allocateDirect(C.VERTEX.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().put(C.VERTEX);
    private FloatBuffer mUvTexVertexBuffer;
    private float mCurSaturation = 1.0f;
    private Bitmap mBitmap;
    private CameraOverlap mCameraOverlap;
    private Context mContext;

    public PictureRender(Context context, GlesManager glesmanager, FaceOverlap faceoverlap,CameraOverlap cameraoverlap) {
        this.mContext = context;
        this.mFaceOverlap = faceoverlap;
        this.mGlesManager = glesmanager;
        this.mCameraOverlap = cameraoverlap;
        if (mFaceOverlap == null || mGlesManager == null || mCameraOverlap == null) {
            Log.e(TAG, "the constructor can not be null.check");
            return;
        }
        mUvTexVertexBuffer = ByteBuffer.allocateDirect(C.UV_TEX_VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(C.UV_TEX_VERTEX);
        mUvTexVertexBuffer.position(0);
        mVertexBuffer.position(0);
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public void setSurfaceStateistener(OnFaceRenderListener ofclistener) {
        this.mOnFaceRenderListener = ofclistener;

    }

    public interface OnFaceRenderListener {
        void onSurfaceCreated();

        void onSurfaceChanged();

        void onDrawFrame();
    }

    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            mFaceOverlap.faceTrack1(bytes);
        }
    };

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        mFaceOverlap.initSurfaceTexture();
        mCameraOverlap.openCamera(mFaceOverlap.getSurfaceTexture());
        Log.d(TAG, "camera is opened");
        mCameraOverlap.setPreviewCallback(mPreviewCallback);
        if (null != mOnFaceRenderListener) {
            Log.d(TAG, "onSurfaceCreated");
            mOnFaceRenderListener.onSurfaceCreated();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged:width:+" + width + "height:" + height);
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
        if (mBitmap != null && !mBitmap.isRecycled()) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        }
        mProgram = GLES20.glCreateProgram();
        int vertexShader = mGlesManager.loadShader(GLES20.GL_VERTEX_SHADER, VertexShader.VERTEX_SHADER);
        int saturateShader = mGlesManager.loadShader(GLES20.GL_FRAGMENT_SHADER, SaturationShader.frame_shader_saturation_two_parameters);
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, saturateShader);
        GLES20.glLinkProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
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
        if (null != mOnFaceRenderListener) {
            Log.d(TAG, "onSurfaceChanged");
            mOnFaceRenderListener.onSurfaceChanged();
        }

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mTexCoorHandle);
        GLES20.glVertexAttribPointer(mTexCoorHandle, 2, GLES20.GL_FLOAT, false, 0, mUvTexVertexBuffer);
        GLES20.glUniform1i(mTexSamplerHandle, 0);
        GLES20.glUniform1f(mSaturation, mCurSaturation);
        GLES20.glUniform1f(C.mVar1, C.mCurVar1);
        GLES20.glUniform1f(C.mVar2, C.mCurVar2);
        GLES20.glUniform1f(C.mVar3, C.mCurVar3);
        GLES20.glUniform1f(C.mVar4, C.mCurVar4);
        GLES20.glUniform1f(C.mVar5, C.mCurVar5);
        GLES20.glUniform1f(C.mVar6, C.mCurVar6);
        GLES20.glUniform1f(C.mVar7, C.mCurVar7);
        GLES20.glUniform1f(C.mVar8, C.mCurVar8);
        GLES20.glUniform1f(C.mVar9, C.mCurVar9);
        GLES20.glUniform1f(C.mVar10, C.mCurVar10);
        GLES20.glUniform1f(mVarExtra, C.mCurVarExtra);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoorHandle);
        if (null != mOnFaceRenderListener) {
            Log.d(TAG, "onDrawFrame");
            mOnFaceRenderListener.onDrawFrame();
        }

    }


}

