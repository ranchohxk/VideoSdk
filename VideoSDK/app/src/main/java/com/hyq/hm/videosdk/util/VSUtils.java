package com.hyq.hm.videosdk.util;

import android.os.Build;
import android.util.Log;

import com.hyq.hm.videosdk.impl.OnAuthStateListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author hxk <br/>
 * 功能：
 * 创建日期   2019/6/21
 * 修改者：
 * 修改日期：
 * 修改内容: Face功能，需要加密
 */
public class VSUtils {
    private String TAG = "VsUtils";

    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            return model.trim();
        } else {
            return "";
        }
    }

    public String getSdkName() {
        return "com.hyq.hm.videosdk";
    }

    public String getPassword() {
        return "111111";
    }


    public void AuthactivationInfo(String urlPath, OnAuthStateListener authStateListener) {
        Log.d(TAG, "AuthactivationInfo");
        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(urlPath);
            if (url == null) {
                return;
            }
            conn = (HttpURLConnection) url.openConnection();
            if (conn == null) {
                return;
            }
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("boundleId ", getSdkName());
            conn.setRequestProperty("password", getPassword());
            conn.setUseCaches(false);
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "auth videosdk sucess.");
                if (null != authStateListener) {
                    authStateListener.onSucess();
                }
            } else {
                Log.d(TAG, "auth videosdk fail.");
                if (null != authStateListener) {
                    authStateListener.onFail();
                }
            }
        } catch (IOException e) {
            if (null != authStateListener) {
                Log.e(TAG, "auth videosdk error.");
                authStateListener.onError();
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }
}
