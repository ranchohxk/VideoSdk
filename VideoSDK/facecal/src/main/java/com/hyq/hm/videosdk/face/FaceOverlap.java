package com.hyq.hm.videosdk.face;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.hyq.hm.videosdk.camera.CameraOverlap;

import java.util.List;

import zeusees.tracking.Face;
import zeusees.tracking.FaceTracking;

/**
 * Created by a4220 on 2018/12/8.
 */

public class FaceOverlap {

    private FaceTracking mMultiTrack106 = null;
    private boolean mTrack106 = false;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private byte[] mNv21Data;

    public FaceOverlap() {

        String modelPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidOS/models";
        mMultiTrack106 = new FaceTracking(modelPath);
        mNv21Data = new byte[CameraOverlap.PREVIEW_WIDTH * CameraOverlap.PREVIEW_HEIGHT * 2];
        mHandlerThread = new HandlerThread("DrawFacePointsThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());


//        mMultiTrack106 = new FaceTracking(FileUtils.AppPath+"/"+FileUtils.PathMain+"/"+FileUtils.PathModel);
//        mNv21Data = new byte[CameraOverlap.PREVIEW_WIDTH * CameraOverlap.PREVIEW_HEIGHT * 2];
//        mHandlerThread = new HandlerThread("DrawFacePointsThread");
//        mHandlerThread.start();
//        mHandler = new Handler(mHandlerThread.getLooper());
    }

    private final Object lockObj = new Object();

    private OnFaceListener faceListener;

    public void setFaceListener(OnFaceListener faceListener) {
        this.faceListener = faceListener;
    }

    public void faceTrack1(byte[] data) {
        synchronized (lockObj) {
            mNv21Data = rotateYUV420Degree90(data, CameraOverlap.PREVIEW_WIDTH, CameraOverlap.PREVIEW_HEIGHT);
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mTrack106) {
                    mMultiTrack106.FaceTrackingInit(mNv21Data, CameraOverlap.PREVIEW_WIDTH, CameraOverlap.PREVIEW_HEIGHT);
                    mTrack106 = false;
                } else {
                    mMultiTrack106.Update(mNv21Data, CameraOverlap.PREVIEW_WIDTH, CameraOverlap.PREVIEW_HEIGHT);
                }
                if (mMultiTrack106 != null) {
                    List<Face> faceActions = mMultiTrack106.getTrackingInfo();
                    if (faceListener != null) {
                        faceListener.onFace(faceActions);
                    }
                }

            }
        });
    }

    private int[] textures = new int[1];
    private SurfaceTexture mSurfaceTexture;

    /**
     * 初始化surfaceTexture
     */
    public void initSurfaceTexture() {
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
        }
        mSurfaceTexture = new SurfaceTexture(textures[0]);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void deleteTexture() {
        GLES20.glDeleteTextures(1, textures, 0);
    }

    public void release() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mMultiTrack106 != null) {
            mMultiTrack106 = null;
        }
        mHandlerThread.quit();
    }

    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth)
                        + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    public interface OnFaceListener {
        void onFace(List<Face> faces);
    }
}
