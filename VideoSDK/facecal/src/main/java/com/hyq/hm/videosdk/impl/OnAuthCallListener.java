package com.hyq.hm.videosdk.impl;

public interface OnAuthCallListener {
    void onAuthSucess();

    void onAuthFail(int code);
}