package com.hyq.hm.videosdk.util;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;


/**
 * Save Data To SharePreference Or Get Data from SharePreference
 * <p>
 * 通过SharedPreferences来存储数据，自定义类型
 */
public class SharedUtil {
    private Context ctx;
    private String FileName = "megvii";

    public static final String isStartRecorder = "isStartRecorder";
    public static final String is3DPose = "is3DPose";
    public static final String isdebug = "isdebug";
    public static final String ROIDetect = "ROIDetect";
    public static final String is106Points = "is106Points";
    public static final String isBackCamera = "isBackCamera";
    public static final String minFaceSize = "minFaceSize";
    public static final String interval = "interval";
    public static final String resolution = "resolution";
    public static final String resolutionW = "resolutionW";
    public static final String resolutionH = "resolutionH";
    public static final String isFaceProperty = "isFaceProperty";
    public static final String isOneFaceTrackig = "isOneFaceTrackig";
    public static final String trackModel = "trackModel";

    public static final String isSaved = "isSaved";

    public static final int playing_mode = -1;
    public static final String KEY_SETTING_DECODER_TYPE = "key_listpreference_setting_decoder_type";
    public static final int KEY_SETTING_DECODER_TYPE_SOFT = 0;
    public static final int KEY_SETTING_DECODER_TYPE_HW = 1;

    public static final String LOCAL_AUDIO_UI = "local_audio_ui";
    public static final String NET_VIDEO_UI = "net_video_ui";
    public static final String KEY_ENABLE_VERTICAL_PLAYBACK = "feature_enable_vertical_playback";
    public static final String KEY_ENABLE_FACEPLUS_MODULE = "enable_faceplus_module";

    public static final String KEY_SATURATION_PARAM_FIRST = "saturation_param_first";
    public static final String KEY_SATURATION_PARAM_SECOND = "saturation_param_second";
    public static final String KEY_SATURATION_PARAM_THIRD = "saturation_param_third";

    public static final int DEFAULT_FPARAM_VALUE = 100;
    public static final int DEFAULT_SPARAM_VALUE = 100;

    public SharedUtil(Context ctx) {
        this.ctx = ctx;
    }

    public void saveIntValue(String key, int value) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void saveLongValue(String key, long value) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void writeDownStartApplicationTime() {
        SharedPreferences sp = ctx.getSharedPreferences(FileName, Context.MODE_PRIVATE);
        long now = System.currentTimeMillis();
        //		Calendar calendar = Calendar.getInstance();
        //Date now = calendar.getTime();
        //		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:hh-mm-ss");
        SharedPreferences.Editor editor = sp.edit();
        //editor.putString("启动时间", now.toString());
        editor.putLong("nowtimekey", now);
        editor.commit();

    }

    public void saveBooleanValue(String key, boolean value) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void removeSharePreferences(String key) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.remove(key);
        editor.commit();
    }

    public boolean contains(String key) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        return sharePre.contains(key);
    }

    public Map<String, Object> getAllMap() {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        return (Map<String, Object>) sharePre.getAll();
    }

    public Integer getIntValueByKey(String key) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        return sharePre.getInt(key, -1);
    }

    public Long getLongValueByKey(String key) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        return sharePre.getLong(key, -1);
    }

    public void saveStringValue(String key, String value) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getStringValueByKey(String key) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        return sharePre.getString(key, null);
    }

    public Boolean getBooleanValueByKey(String key) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        return sharePre.getBoolean(key, false);
    }

    public Integer getIntValueAndRemoveByKey(String key) {
        Integer value = getIntValueByKey(key);
        removeSharePreferences(key);
        return value;
    }

    public void setUserkey(String userkey) {
        this.saveStringValue("params_userkey", userkey);
    }

    public String getUserkey() {
        return this.getStringValueByKey("params_userkey");
    }

}

