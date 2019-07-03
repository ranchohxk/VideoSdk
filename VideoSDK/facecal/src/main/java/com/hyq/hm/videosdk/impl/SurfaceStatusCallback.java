package com.hyq.hm.videosdk.impl;

import android.view.SurfaceHolder;

public interface SurfaceStatusCallback {
    void surfaceCreated(SurfaceHolder holder);

    void surfaceChanged(SurfaceHolder holder, int format, int width, int height);

    void surfaceDestroyed(SurfaceHolder holder);
}