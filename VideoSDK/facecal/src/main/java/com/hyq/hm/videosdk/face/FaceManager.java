package com.hyq.hm.videosdk.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hyq.hm.videosdk.camera.CameraOverlap;
import com.hyq.hm.videosdk.file.SharedPreferencesHelper;
import com.hyq.hm.videosdk.gles.GLRenderer;
import com.hyq.hm.videosdk.gles.GlesManager;
import com.hyq.hm.videosdk.impl.OnAuthCallListener;
import com.hyq.hm.videosdk.impl.OnFaceSListener;
import com.hyq.hm.videosdk.impl.OnNetAuthCalListener;
import com.hyq.hm.videosdk.impl.OnUpdateFaceStatus;
import com.hyq.hm.videosdk.impl.SurfaceStatusCallback;
import com.hyq.hm.videosdk.net.NetManager;
import com.hyq.hm.videosdk.util.C;
import com.hyq.videoFilter.VideoFilter;

import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import zeusees.tracking.Face;

/**
 * @author hxk <br/>
 * 功能：
 * 创建日期   2019/7/2
 * 修改者：
 * 修改日期：
 * 修改内容:
 */
public class FaceManager {
    private static final String TAG = "FaceManager";
    private Context mContext;
    private PictureRender mPictureRender;
    private FaceOverlap mFaceOverlap;
    private Bitmap mBitmap;
    private GLSurfaceView mGLSurfaceView;
    private GlesManager mGlesManager;
    private FaceCalManager mFaceCalManager;
    private OnFaceSListener mOnFaceSListener;
    private CameraOverlap mCameraOverlap;
    private NetManager mNetManager;
    private SharedPreferencesHelper mSharedPreferences;
    private VideoFilter mVideoFilter;
    private GLRenderer mGLenderer;
    private IjkMediaPlayer mIjkMediaPlayer;
    private SurfaceView mSurfaceView;

    //picture
    public FaceManager(Context context, String password) {
        Log.d(TAG, "PictureManager");
        this.mContext = context;
        mFaceOverlap = new FaceOverlap();
        mCameraOverlap = new CameraOverlap(context);
        mGlesManager = GlesManager.getInstance(context);
        mFaceCalManager = FaceCalManager.getInstance(context);
        mFaceOverlap.setFaceListener(mOnFaceListener);
        mPictureRender = new PictureRender(mContext, mGlesManager, mFaceOverlap, mCameraOverlap);
        mNetManager = new NetManager();
        mNetManager.init(mContext.getPackageName(), password);
        mSharedPreferences = new SharedPreferencesHelper(context);

    }

    //video
    public FaceManager(Context context, IjkMediaPlayer ijkplayer, SurfaceView surfaceview, String password, final SurfaceStatusCallback sscallback) {
        Log.d(TAG, "VideoManager");
        this.mContext = context;
        mIjkMediaPlayer = ijkplayer;
        mSurfaceView = surfaceview;
        mSharedPreferences = new SharedPreferencesHelper(context);
        mFaceOverlap = new FaceOverlap();
        mCameraOverlap = new CameraOverlap(context);
        mGLenderer = new GLRenderer();
        mVideoFilter = new VideoFilter(mOvflitener);
        mNetManager = new NetManager();
        mNetManager.init(mContext.getPackageName(), password);
        mSurfaceView.getHolder().
                addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        mVideoFilter.initGLES(holder.getSurface());
                        if (null != sscallback) {
                            sscallback.surfaceCreated(holder);
                        }
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                        if (null != sscallback) {
                            sscallback.surfaceChanged(holder, format, width, height);
                        }
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        if (null != sscallback) {
                            sscallback.surfaceDestroyed(holder);
                        }

                    }
                });


    }

    private Camera.PreviewCallback pCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            if (null != mFaceOverlap) {
                mFaceOverlap.faceTrack1(bytes);
            }
        }
    };

    VideoFilter.OnVideoFilterListener mOvflitener = new VideoFilter.OnVideoFilterListener() {
        @Override
        public void onSurface(final Surface surface) {
            mGLenderer.initShader();
            if (null != mFaceOverlap) {
                mFaceOverlap.initSurfaceTexture();
            }
            if (null != mIjkMediaPlayer) {
                mIjkMediaPlayer.setSurface(surface);
                mIjkMediaPlayer.start();
            }
            if (null != mCameraOverlap) {
                mCameraOverlap.openCamera(mFaceOverlap.getSurfaceTexture());
                mCameraOverlap.setPreviewCallback(pCallback);
            }

        }

        @Override
        public void onDraw(int textureId) {
            int videoWidth = 0, videoHeight = 0;
            if (videoWidth != mIjkMediaPlayer.getVideoWidth() || videoHeight != mIjkMediaPlayer.getVideoHeight()) {
                videoWidth = mIjkMediaPlayer.getVideoWidth();
                videoHeight = mIjkMediaPlayer.getVideoHeight();
                mGLenderer.setViewportSize(videoWidth, videoHeight, mSurfaceView.getWidth(), mSurfaceView.getHeight());
            }
            mGLenderer.drawFrame(textureId);
        }

        @Override
        public void onDelete() {
            if (null != mIjkMediaPlayer) {
                mIjkMediaPlayer.setSurface(null);
            }
            if (null != mFaceOverlap) {
                mFaceOverlap.deleteTexture();
            }
            if (null != mCameraOverlap) {
                mCameraOverlap.release();
            }
        }

        @Override
        public int onWidth() {
            if (null != mIjkMediaPlayer) {
                return mIjkMediaPlayer.getVideoWidth();
            }
            return 0;
        }

        @Override
        public int onHeight() {
            if (null != mIjkMediaPlayer) {
                return mIjkMediaPlayer.getVideoHeight();
            }
            return 0;
        }
    };

    public void start(final OnUpdateFaceStatus onupdatefacestatus) {
        Log.d(TAG, "start");
        mFaceOverlap.setFaceListener(new FaceOverlap.OnFaceListener() {
            @Override
            public void onFace(List<Face> faces) {
                if (faces.size() == 0) {
                    mVideoFilter.setR(0);
                    if (null != onupdatefacestatus) {
                        onupdatefacestatus.onFail();
                    }
                } else {
                    float wh = CameraOverlap.PREVIEW_WIDTH * 1.0f / CameraOverlap.PREVIEW_HEIGHT;
                    for (Face r : faces) {
                        for (int i = 0; i < 106; i++) {
                            int x = (int) (r.landmarks[69 * 2] * wh - (CameraOverlap.PREVIEW_WIDTH - CameraOverlap.PREVIEW_HEIGHT));
                            int y = (int) (r.landmarks[69 * 2 + 1]);
                            float cx = CameraOverlap.PREVIEW_WIDTH / 2.0f;
                            float red = x / cx - 1.0f;
                            if (red > 1.0) red = 1.0f;
                            else if (red < -1.0) red = -1.0f;
                            mVideoFilter.setR(red);
                            if (null != onupdatefacestatus) {
                                onupdatefacestatus.onSucess();
                            }
                        }
                    }
                }

            }
        });
    }


    public void ondeletegles() {
        Log.d(TAG, "ondeletegles");
        if (null != mVideoFilter) {
            mVideoFilter.deleteGLES();
        }
    }

    public void onRelease() {
        Log.d(TAG, "onRelease");
        if (null != mVideoFilter) {
            mVideoFilter.release();
        }
        if (null != mFaceCalManager) {
            mFaceOverlap.release();
        }
    }

    public void setBitmap(Bitmap bitmap) {
        Log.d(TAG, "setBitmap");
        this.mBitmap = bitmap;
        if (null != mPictureRender) {
            mPictureRender.setBitmap(bitmap);
        }
    }

    public void SetR(float R) {
        Log.d(TAG, "setR:" + R);
        if (null != mVideoFilter)
            mVideoFilter.setR(R);

    }

    public void SetG(float G) {
        Log.d(TAG, "setG:" + G);
        if (null != mVideoFilter)
            mVideoFilter.setG(G);

    }

    public void SetB(float B) {
        Log.d(TAG, "setB:" + B);
        if (null != mVideoFilter)
            mVideoFilter.setB(B);

    }

    FaceOverlap.OnFaceListener mOnFaceListener = new FaceOverlap.OnFaceListener() {
        @Override
        public void onFace(List<Face> faces) {
            if (null != mFaceCalManager) {
                mFaceCalManager.faceCalculate(faces, mGLSurfaceView);
            }
        }
    };

    public void auth(final OnAuthCallListener onAuthCallListener) {
        Log.d(TAG, "auth");
        if (mSharedPreferences.getIntValueByKey(C.KEY_AUTH_STATUS) == 1) {
            Log.d(TAG, "already auth sucess");
            return;
        }
        mNetManager.Auth(new OnNetAuthCalListener() {
            @Override
            public void onSucess(int code, String boundleid, int count) {
                Log.d(TAG, "code:" + code + ",count:" + count + ",boundleid:" + boundleid);
                if (null != onAuthCallListener) {
                    onAuthCallListener.onAuthSucess();
                    if (count <= 0) {
                        if (null != onAuthCallListener) {
                            onAuthCallListener.onAuthFail(0);
                        }
                        if (mFaceOverlap != null) {
                            mFaceOverlap.release();
                            mFaceOverlap = null;
                        }
                        if (mCameraOverlap != null) {
                            mCameraOverlap.release();
                            mCameraOverlap = null;
                        }
                    } else {
                        mSharedPreferences.saveIntValue(C.KEY_AUTH_STATUS, 1);
                    }
                }
            }

            @Override
            public void onFail(int status) {
                Log.e(TAG, "onFail:status:" + status);
                if (null != onAuthCallListener) {
                    onAuthCallListener.onAuthFail(status);
                }

            }
        });

    }

    public void init(final GLSurfaceView glSurfaceView) {
        Log.d(TAG, "init");
        mGLSurfaceView = glSurfaceView;
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(mPictureRender);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void requestRender(GLSurfaceView glsurfaceview) {
        Log.d(TAG, "requestRender");
        if (null != glsurfaceview)
            glsurfaceview.requestRender();

    }

    public void onPause(GLSurfaceView glsurfaceview) {
        Log.d(TAG, "onPause");
        if (null != glsurfaceview)
            glsurfaceview.onPause();
    }

    public void onResume(GLSurfaceView glsurfaceview) {
        Log.d(TAG, "onResume");
        if (null != glsurfaceview)
            glsurfaceview.onResume();
    }

    public void onDestory() {
        Log.d(TAG, "onDestory()");
        if (mFaceOverlap != null) {
            mFaceOverlap.deleteTexture();
            mFaceOverlap.release();
            mFaceOverlap = null;
        }
        if (mCameraOverlap != null) {
            mCameraOverlap.release();
            mCameraOverlap = null;
        }
    }

    public void setFaceSListener(OnFaceSListener ofclistener) {
        Log.d(TAG, "setFaceListener");
        this.mOnFaceSListener = ofclistener;
        mFaceCalManager.setFaceMListener(new FaceCalManager.OnFaceCalListener() {
            @Override
            public void onFaceStatus(boolean status) {
                if (null != mOnFaceListener) {
//                    Log.d(TAG, "onFaceStatus:" + status);
                    mOnFaceSListener.onFaceStatus(status);
                }
            }
        });

    }


}
