package com.hyq.hm.videosdk.gles;

import android.content.Context;
import android.opengl.GLES20;

/**
 * @author hxk <br/>
 * 功能：
 * 创建日期   2019/6/17
 * 修改者：
 * 修改日期：
 * 修改内容:Gles功能 需要加密
 */
public class GlesManager {
    private static GlesManager mGlesManager;
    private Context mContext;

    public GlesManager(Context context) {
        mContext = context;
    }

    public static GlesManager getInstance(Context context) {
        if (mGlesManager == null) {
            mGlesManager = new GlesManager(context);
        }
        return mGlesManager;
    }

    public int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
