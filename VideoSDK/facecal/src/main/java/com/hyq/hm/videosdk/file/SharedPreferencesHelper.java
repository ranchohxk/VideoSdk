package com.hyq.hm.videosdk.file;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * @author hxk <br/>
 * 功能：
 * 创建日期
 * 修改者：
 * 修改日期：2019/6/17
 * 修改内容:
 */

public class SharedPreferencesHelper {
    private Context ctx;
    private String FileName = "videosdk";
    public static final String KEY_SATURATION_PARAM_FIRST = "saturation_param_first";
    public static final String KEY_SATURATION_PARAM_SECOND = "saturation_param_second";
    public static final String KEY_SATURATION_PARAM_THIRD = "saturation_param_third";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context ctx) {
        this.ctx = ctx;
    }


    public SharedPreferencesHelper(Context context, String FILE_NAME) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveIntValue(String key, int value) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void put(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 获取保存的数据
     */
    public Object getSharedPreference(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否存在
     */
    public Boolean contain(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    public void removeSharePreferences(String key) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePre.edit();
        editor.remove(key);
        editor.commit();
    }

    public Integer getIntValueByKey(String key) {
        SharedPreferences sharePre = ctx.getSharedPreferences(FileName,
                Context.MODE_PRIVATE);
        return sharePre.getInt(key, -1);
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

}
