package com.hyq.hm.videosdk.util;

/**
 * @author hxk <br/>
 * 功能：
 * 创建日期   2019/6/21
 * 修改者：
 * 修改日期：
 * 修改内容: Face功能，需要加密
 */
public class C {
    public static String mModuleName = VSUtils.getModel();
    public static int mVar1;//R分量在filter中的地址，把mCurVar1赋值给 ,来自Slider
    public static int mVar2;//R分量,来自Slider
    public static int mVar3;
    public static int mVar4;
    public static int mVar5;
    public static int mVar6;
    public static int mVar7;
    public static int mVar8;
    public static int mVar9;
    public static int mVar10;
    public static float mCurVar1; //R分量,来自Slider
    public static float mCurVar2; //G分量，来自Slider
    public static float mCurVar3; //B分量，来自人脸坐标
    public static float mCurVar4; //R分量,来自Slider
    public static float mCurVar5; //G分量，来自Slider
    public static float mCurVar6; //B分量，来自人脸坐标  W*H
    public static float mCurVar7; //B分量，来自人脸坐标  W*H
    public static float mCurVar8; //B分量，来自人脸坐标  W*H
    public static float mCurVar9; //G分量，来自Slider
    public static float mCurVar10; //B分量，来自人脸坐标  W*H
    public static final float[] VERTEX = {
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
    };
    public static final float[] UV_TEX_VERTEX = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
    };
}
