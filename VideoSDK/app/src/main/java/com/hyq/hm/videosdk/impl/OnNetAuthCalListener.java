package com.hyq.hm.videosdk.impl;

public interface OnNetAuthCalListener {
    void onSucess(int code, String boundleid, int count);

    void onFail(int status);
}
