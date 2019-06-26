package com.hyq.hm.videosdk.face;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.hyq.hm.videosdk.camera.CameraOverlap;
import com.hyq.hm.videosdk.util.C;
import com.hyq.hm.videosdk.util.VSUtils;

import java.util.List;

import zeusees.tracking.Face;

/**
 * @author hxk <br/>
 * 功能：
 * 创建日期   2019/6/21
 * 修改者：
 * 修改日期：
 * 修改内容: Face功能，需要加密
 */
public class FaceManager {
    private static FaceManager mFaceManager;
    private Context mContext;
    private OnFaceCalListener mFaceCalListener;
    private long mLastDetectTime = 0;


    public FaceManager(Context context) {
        mContext = context;
    }

    public static FaceManager getInstance(Context context) {
        if (mFaceManager == null) {
            mFaceManager = new FaceManager(context);
        }
        return mFaceManager;
    }

    public void setFaceMListener(OnFaceCalListener ofclistener) {
        this.mFaceCalListener = ofclistener;

    }

    public void faceCalculate(List<Face> faces, boolean inTracking, GLSurfaceView glSurfaceView) {
        if (faces.size() == 0) {
            long now = System.currentTimeMillis();
            long diff = now - mLastDetectTime;
            if (diff > 100 && inTracking == true) {
                if (null != mFaceCalListener) {
                    mFaceCalListener.onFaceStatus(false);
                }
            }
        } else {
            float wh = CameraOverlap.PREVIEW_WIDTH * 1.0f / CameraOverlap.PREVIEW_HEIGHT;
            for (Face f : faces) {
                mLastDetectTime = System.currentTimeMillis();
                if (inTracking == false) {
                    if (null != mFaceCalListener) {
                        mFaceCalListener.onFaceStatus(true);
                    }
                }
                for (int i = 0; i < 106; i++) {
                    int x = (int) (f.landmarks[69 * 2] * wh - (CameraOverlap.PREVIEW_WIDTH - CameraOverlap.PREVIEW_HEIGHT));
                    int y = (int) (f.landmarks[69 * 2 + 1]);
                    float cx = CameraOverlap.PREVIEW_WIDTH / 2.0f;
                    float red = x / cx - 1.0f;
                    if (red > 1.0) red = 1.0f;
                    else if (red < -1.0) red = -1.0f;
                    if (C.mModuleName.equals("HUAWEI MATE20")) {
                        C.mCurVar3 = (float) red;
                        C.mCurVar4 = 0.1f;
                        C.mCurVar5 = 0.1f;
                        C.mCurVar6 = 0.1f;
                        C.mCurVar9 = 0.1f;
                        C.mCurVar10 = 0.0f;
                        glSurfaceView.requestRender();

                    } else if (C.mModuleName.equals("HUAWEI MATE21")) {
                        C.mCurVar3 = (float) red;
                        C.mCurVar4 = 0.1f;
                        C.mCurVar5 = 0.1f;
                        C.mCurVar6 = 0.1f;
                        C.mCurVar9 = 0.1f;
                        C.mCurVar10 = 0.0f;
                        glSurfaceView.requestRender();

                    } else {
                        C.mCurVar3 = (float) red;
                        C.mCurVar4 = 0.1f;
                        C.mCurVar5 = 0.1f;
                        C.mCurVar6 = 0.1f;
                        C.mCurVar9 = 0.1f;
                        C.mCurVar10 = 0.0f;
                        glSurfaceView.requestRender();
                    }
                }
            }
        }

    }

    public interface OnFaceCalListener {
        void onFaceStatus(boolean status);
    }

}
