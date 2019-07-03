package com.hyq.hm.videosdk.net;

import android.util.Log;

import com.google.gson.Gson;
import com.hyq.hm.videosdk.bean.Data;
import com.hyq.hm.videosdk.bean.JsonRootBean;
import com.hyq.hm.videosdk.impl.OnNetAuthCalListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author hxk <br/>
 * 功能：
 * 创建日期   2019/6/26
 * 修改者：
 * 修改日期：
 * 修改内容:
 */
public class NetManager {
    private static final String TAG = "OkhttpUtils";
    private OkHttpClient mOkHttpClient;
    public String mChangeUserCountUrl = "http://1.movie.applinzi.com/home/index/changeUserCount";
    private Request mRequest;
    private Call mCall;
    private Response mResponse;
    private RequestBody mFormBody;
    private int mCount;
    private String mBoundleId;

    public NetManager() {
        mOkHttpClient = new OkHttpClient();
    }

    public void init(String packagename, String password) {
        mFormBody = new FormBody.Builder()
                .add("boundleId", packagename == null ? "com.hyq.hm.videosdk" : packagename)
                .add("password", password == null ? "111111" : password)
                .build();
        mRequest = new Request.Builder()
                .url(mChangeUserCountUrl)
                .post(mFormBody)
                .build();
        mCall = mOkHttpClient.newCall(mRequest);
    }


    public void Auth(final OnNetAuthCalListener onnectclistener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mResponse = mCall.execute();
                    if (mResponse.isSuccessful()) {
                        JsonRootBean rootbean = null;
                        try {
                            String result = mResponse.body().string();
                            Log.d(TAG, "result:" + result);
                            rootbean = new Gson().fromJson(result, JsonRootBean.class);
                            int code = rootbean.getCode();
                            Data data = rootbean.getData();
                            mCount = Integer.parseInt(data.getCount());
                            mBoundleId = data.getBoundleid();
                            Log.d(TAG, "code:" + code + ",mCount:" + mCount + ",boundleid:" + mBoundleId);
                            if (null != onnectclistener) {
                                onnectclistener.onSucess(code, mBoundleId, mCount);
                            }
                        } catch (Exception e) {
                            if (null != onnectclistener) {
                                onnectclistener.onFail(-1);
                            }
                            e.printStackTrace();
                        }
                    } else {
                        if (null != onnectclistener) {
                            onnectclistener.onFail(mResponse.code());
                        }
                    }
                } catch (IOException e) {
                    if (null != onnectclistener) {
                        onnectclistener.onFail(-1);
                    }
                    e.printStackTrace();
                }
            }
        }).start();


    }


}

