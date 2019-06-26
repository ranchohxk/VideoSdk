package com.hyq.hm.videosdk.activity;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hyq.hm.videosdk.R;
import com.hyq.hm.videosdk.camera.CameraOverlap;
import com.hyq.hm.videosdk.face.FaceOverlap;
import com.hyq.hm.videosdk.file.FileUtils;
import com.hyq.hm.videosdk.gles.GLRenderer;
import com.hyq.hm.videosdk.util.C;
import com.hyq.videoFilter.VideoFilter;

import java.io.IOException;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import zeusees.tracking.Face;

public class MainActivity extends Activity {

    private SurfaceView surfaceView;
    private View videoPlayerView;
    private SeekBar seekBar;

    private TextView videoTime;
    private TextView videoDuration;

    private IjkMediaPlayer player;

    private Handler mainHandler;


    private VideoFilter videoFilter;

    private GLRenderer renderer;

    private int videoWidth, videoHeight;
    private ImageButton mImageButtonTrackingStatusmain;


    private FaceOverlap faceOverlap;
    private CameraOverlap cameraOverlap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        faceOverlap = new FaceOverlap();
        faceOverlap.setFaceListener(new FaceOverlap.OnFaceListener() {
            @Override
            public void onFace(List<Face> faces) {
                if (faces.size() == 0) {
                    videoFilter.setR(0);
                    updateFaceStatus(false);
                } else {
                    float wh = CameraOverlap.PREVIEW_WIDTH * 1.0f / CameraOverlap.PREVIEW_HEIGHT;
                    for (Face r : faces) {
                        for (int i = 0; i < 106; i++) {
                            int x = (int) (r.landmarks[69 * 2] * wh - (CameraOverlap.PREVIEW_WIDTH - CameraOverlap.PREVIEW_HEIGHT));
                            int y = (int) (r.landmarks[69 * 2 + 1]);
                            float cx = CameraOverlap.PREVIEW_WIDTH / 2.0f;
                            float red = x / cx - 1.0f;
                            if (red > 1.0) red = 1.0f;
                            else if (red < -1.0) red = -1.0f;
                            videoFilter.setR(red);
                            //获取图片的值
                            // Log.d("picture","size:"+C.mCurVar3);
//                            if (C.mModuleName.equals("HUAWEI MATE20")) {
//                                if (C.mCurVar3 != 0) {
//                                    videoFilter.setR(C.mCurVar3);
//                                    videoFilter.setG(0.1f);
//                                    videoFilter.setB(0.1f);
//                                } else {
//                                    videoFilter.setR(red);
//                                }
//
//                            } else if (C.mModuleName.equals("HUAWEI MATE21")) {
//                                if (C.mCurVar3 != 0) {
//                                    videoFilter.setR(C.mCurVar3);
//                                    videoFilter.setG(0.1f);
//                                    videoFilter.setB(0.1f);
//                                } else {
//                                    videoFilter.setR(red);
//                                }
//
//                            } else {
//                                if (C.mCurVar3 != 0) {
//                                     videoFilter.setR(C.mCurVar3);
//                                      videoFilter.setG(0.1f);
//                                      videoFilter.setB(0.1f);
//                                } else {
//                                      videoFilter.setR(red);
//                                }
//                            }
                            updateFaceStatus(true);
                        }
                    }
                }

            }
        });
        cameraOverlap = new

                CameraOverlap(this);

        renderer = new

                GLRenderer();

        videoPlayerView =

                findViewById(R.id.video_player);


        mainHandler = new

                Handler();

        videoTime =

                findViewById(R.id.video_time);


        surfaceView =

                findViewById(R.id.surface_view);
        surfaceView.getHolder().

                addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        videoFilter.initGLES(holder.getSurface());
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {

                    }
                });
        mImageButtonTrackingStatusmain = (ImageButton)

                findViewById(R.id.id_tracking_status_main);
        mImageButtonTrackingStatusmain.setBackgroundResource(R.drawable.tracking_no);

        seekBar =

                findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouch = false;
                player.seekTo(seekBar.getProgress() * player.getDuration() / 100);
//                if(!player.getPlayWhenReady()&&videoPlayerView.getVisibility() == View.VISIBLE){
//                    videoPlayerView.setVisibility(View.INVISIBLE);
//                    player.setPlayWhenReady(true);
//                }
            }
        });
        // Uri url = Uri.parse(FileUtils.AppPath +"/"+FileUtils.PathMain+"/"+FileUtils.PathVideo+"/test.mp4");
        //  DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();


//        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
//        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
//
//        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        player = new

                IjkMediaPlayer();

//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
//                Util.getUserAgent(this, "ExoPlayerTime"), bandwidthMeter);
//        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(url, mainHandler,null);
//        player.prepare(videoSource);

        String videoPath = FileUtils.AppPath + "/" + FileUtils.PathMain + "/" + FileUtils.PathVideo + "/test.mp4";
        try {
            player.setDataSource(videoPath);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();

        videoFilter = new

                VideoFilter(new VideoFilter.OnVideoFilterListener() {
            @Override
            public void onSurface(final Surface surface) {
                renderer.initShader();
                faceOverlap.initSurfaceTexture();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (player != null) {
//                            player.setVideoSurface(surface);
                            player.setSurface(surface);
                            if (isPlayer) {
//                                player.setPlayWhenReady(true);
                                player.start();
                                isPlayer = false;
                            }
                        }
                        cameraOverlap.openCamera(faceOverlap.getSurfaceTexture());
                        cameraOverlap.setPreviewCallback(callback);
                    }
                });

            }

            @Override
            public void onDraw(int textureId) {
                if (videoWidth != player.getVideoWidth() || videoHeight != player.getVideoHeight()) {
                    videoWidth = player.getVideoWidth();
                    videoHeight = player.getVideoHeight();
                    renderer.setViewportSize(videoWidth, videoHeight, surfaceView.getWidth(), surfaceView.getHeight());
                }
                renderer.drawFrame(textureId);
            }

            @Override
            public void onDelete() {
                if (player != null) {
                    player.setSurface(null);
                }
                faceOverlap.deleteTexture();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cameraOverlap.release();
                    }
                });
            }

            @Override
            public int onWidth() {
                if (player != null) {
                    return player.getVideoWidth();
                }
                return 0;
            }

            @Override
            public int onHeight() {
                if (player != null) {
                    return player.getVideoHeight();
                }
                return 0;
            }
        });


        SeekBar seekBarR = findViewById(R.id.seek_bar_r);
        SeekBar seekBarG = findViewById(R.id.seek_bar_g);
        SeekBar seekBarB = findViewById(R.id.seek_bar_b);
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float p = seekBar.getProgress() / 100.0f - 1.0f;
                switch (seekBar.getId()) {
                    case R.id.seek_bar_r:
                        videoFilter.setR(p);
                        break;
                    case R.id.seek_bar_g:
                        videoFilter.setG(p);
                        break;
                    case R.id.seek_bar_b:
                        videoFilter.setB(p);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        seekBarR.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBarG.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBarB.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    private Camera.PreviewCallback callback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            faceOverlap.faceTrack1(bytes);
        }
    };

    public String getTimeString(int time) {
        if (time <= 0) {
            return "00:00";
        }
        int t = time / 60;
        String s = "";
        if (t < 10) {
            s += "0" + t;
        } else {
            s += t;
        }
        s += ":";
        t = time % 60;
        if (t < 10) {
            s += "0" + t;
        } else {
            s += t;
        }
        return s;
    }


    public void playVideo(View view) {
        if (player.getCurrentPosition() >= player.getDuration()) {
            player.seekTo(0);
        }
        videoPlayerView.setVisibility(View.INVISIBLE);
        player.start();
    }

    public void stopVideo(View view) {
        videoPlayerView.setVisibility(View.VISIBLE);
        player.stop();
    }

    private boolean isResume = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int mHideFlags =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(mHideFlags);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        isResume = true;
        isPlayEnd();
    }

    private boolean isPlayer = false;

    @Override
    protected void onPause() {
        super.onPause();
        videoFilter.deleteGLES();
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
                isPlayer = true;
            }
        }
        isResume = false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        videoFilter.release();
        faceOverlap.release();
    }

    private boolean isTouch = false;
    private Handler seekBarHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isResume) {
                return;
            }
            if (player != null) {
                if (videoDuration == null) {
                    videoDuration = findViewById(R.id.video_duration);
                    videoDuration.setText(getTimeString((int) (player.getDuration() / 1000)));
                }
                if (player.isPlaying()) {
                    if (player.getCurrentPosition() < player.getDuration()) {
                        int position = (int) (player.getCurrentPosition() * 100.0f / player.getDuration());
                        videoTime.setText(getTimeString((int) (player.getCurrentPosition() / 1000)));
                        if (!isTouch) {
                            seekBar.setProgress(position);
                        }
                    } else {
                        videoTime.setText(getTimeString((int) (player.getDuration() / 1000)));
                        videoPlayerView.setVisibility(View.VISIBLE);
                        player.stop();
                    }
                }
            }

            isPlayEnd();
        }
    };

    private void isPlayEnd() {
        seekBarHandler.removeMessages(100);
        Message message = seekBarHandler.obtainMessage();
        message.what = 100;
        seekBarHandler.sendMessageDelayed(message, 100);
    }


    public void onHome(View view) {
        finish();
    }

    public void onNot(View view) {

    }

    private void updateFaceStatus(boolean tracking) {

        final boolean track_status = tracking;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!track_status) {

                    mImageButtonTrackingStatusmain.setBackgroundResource(R.drawable.tracking_no);
                } else {

                    mImageButtonTrackingStatusmain.setBackgroundResource(R.drawable.tracking_yes);
                }
            }
        });
    }


}
