package com.hyq.hm.videosdk.gles;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.hyq.hm.videosdk.R;
import com.hyq.hm.videosdk.util.C;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

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

    public void onDrawFrameImpl(GL10 gl, int Program, int PositionHandle, FloatBuffer mVertexBuffer, int TexCoorHandle, FloatBuffer mUvTexVertexBuffer,
                                int TexSamplerHandle, int Saturation, float CurSaturation, int VarExtra, float CurVarExtra

    ) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(Program);
        GLES20.glEnableVertexAttribArray(PositionHandle);
        GLES20.glVertexAttribPointer(PositionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(TexCoorHandle);
        GLES20.glVertexAttribPointer(TexCoorHandle, 2, GLES20.GL_FLOAT, false, 0, mUvTexVertexBuffer);
        GLES20.glUniform1i(TexSamplerHandle, 0);
        GLES20.glUniform1f(Saturation, CurSaturation);
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
        GLES20.glUniform1f(VarExtra, CurVarExtra);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(PositionHandle);
        GLES20.glDisableVertexAttribArray(TexCoorHandle);
    }

}
