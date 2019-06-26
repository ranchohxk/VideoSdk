package com.hyq.videoFilter;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Surface;


/**
 * Created by 海米 on 2018/12/6.
 */

public class VideoFilter {
    private static final int Init = 0;
    private static final int Delete = 100;
    private static final int Draw = 200;
    static {
        System.loadLibrary("video_filter");
    }
    private long model = -1;
    private HandlerThread mThread;
    private Handler mHandler;
    private EGLUtils mEGL;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private OnVideoFilterListener listener;
    private float[] mSTMatrix = new float[16];

    private boolean isDelete = false;
    public VideoFilter(OnVideoFilterListener listener){
        this.listener = listener;
        model = createModel();
        mThread = new HandlerThread("VideoSDK");
        mThread.start();
        mHandler = new Handler(mThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case Init:
                        init((Surface) msg.obj);
                        break;
                    case Delete:
                        delete();
                        break;
                    case Draw:
                        draw();
                        break;
                    default:
                        super.handleMessage(msg);
                        break;
                }
            }
        };
        mEGL = new EGLUtils();
    }

    public void setListener(OnVideoFilterListener listener) {
        this.listener = listener;
    }

    public void release(){
        if(mSurfaceTexture != null){
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if(mSurface != null){
            mSurface.release();
            mSurface = null;
        }
        release(model);
        mThread.quit();
    }
    public void initGLES(Surface surface){
        isDelete = false;
        Message message = new Message();
        message.what = Init;
        message.obj = surface;
        mHandler.sendMessage(message);
    }
    private void init(Surface surface){
        mEGL.initEGL(surface);
        initGLES(model);
        if(mSurfaceTexture != null){
            mSurfaceTexture.release();
        }
        if(mSurface != null){
            mSurface.release();
        }
        mSurfaceTexture = new SurfaceTexture(getTextureId(model));
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                Message message = new Message();
                message.what = Draw;
                mHandler.sendMessage(message);
            }
        });
        mSurface = new Surface(mSurfaceTexture);
        if(listener != null){
            listener.onSurface(mSurface);
        }
    }
    public void deleteGLES(){
        isDelete = true;
        mHandler.removeMessages(Draw);
        Message message = new Message();
        message.what = Delete;
        mHandler.sendMessage(message);
    }
    private void delete(){
        deleteGLES(model);
        if(listener != null){
            listener.onDelete();
        }
        mEGL.release();
    }
    private void draw(){
        if(!isDelete){
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mSTMatrix);
            if(listener != null){
                listener.onDraw(drawGLES(mSTMatrix,listener.onWidth(),listener.onHeight(),model));
            }
            mEGL.swap();
        }
    }
    public void setS(float S){
        setS(S,model);
    }
    public void setH(float H){
        setH(H,model);
    }
    public void setL(float L){
        setL(L,model);
    }
    public void setR(float R){
        setR(R,model);
    }
    public void setG(float G){
        setG(G,model);
    }
    public void setB(float B){
        setB(B,model);
    }

    private native long createModel();
    private native void release(long model);
    private native void initGLES(long model);
    private native int drawGLES(float[] stMatrix,int videoWidth, int videoHeight,long model);
    private native void deleteGLES(long model);
    private native int getTextureId(long model);

    private native void setS(float S,long model);
    private native void setH(float H,long model);
    private native void setL(float L,long model);
    private native void setR(float R,long model);
    private native void setG(float G,long model);
    private native void setB(float B,long model);


    public interface OnVideoFilterListener{
        void onSurface(Surface surface);
        void onDraw(int textureId);
        void onDelete();
        int onWidth();
        int onHeight();
    }
}
