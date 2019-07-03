package com.hyq.hm.videosdk.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hyq.hm.videosdk.R;
import com.hyq.hm.videosdk.face.FaceManager;
import com.hyq.hm.videosdk.file.FileUtils;
import com.hyq.hm.videosdk.impl.OnAuthCallListener;
import com.hyq.hm.videosdk.impl.OnUpdateFaceStatus;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends Activity {

    private SurfaceView surfaceView;
    private View videoPlayerView;
    private SeekBar seekBar;

    private TextView videoTime;
    private TextView videoDuration;

    private IjkMediaPlayer player;

    private Handler mainHandler;
    private ImageButton mImageButtonTrackingStatusmain;

    private FaceManager mVideoManager;

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
        videoPlayerView = findViewById(R.id.video_player);
        mainHandler = new Handler();
        videoTime = findViewById(R.id.video_time);
        surfaceView = findViewById(R.id.surface_view);
        mImageButtonTrackingStatusmain = (ImageButton) findViewById(R.id.id_tracking_status_main);
        mImageButtonTrackingStatusmain.setBackgroundResource(R.drawable.tracking_no);
        seekBar = findViewById(R.id.seek_bar);
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
            }
        });

        player = new IjkMediaPlayer();
        String videoPath = FileUtils.AppPath + "/" + FileUtils.PathMain + "/" + FileUtils.PathVideo + "/test.mp4";
        try {
            player.setDataSource(videoPath);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();
        mVideoManager = new FaceManager(getApplicationContext(), player, surfaceView, null, null);
        mVideoManager.auth(new OnAuthCallListener() {
            @Override
            public void onAuthSucess() {
                Log.d("test", "onSucess");
            }

            @Override
            public void onAuthFail(int code) {
                Log.d("test", "onFail");

            }
        });
        mVideoManager.start(new OnUpdateFaceStatus() {
            @Override
            public void onSucess() {
                updateFaceStatus(true);

            }

            @Override
            public void onFail() {
                updateFaceStatus(false);

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
                        mVideoManager.SetR(p);
                        break;
                    case R.id.seek_bar_g:
                        mVideoManager.SetG(p);
                        break;
                    case R.id.seek_bar_b:
                        mVideoManager.SetB(p);
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
        mVideoManager.ondeletegles();
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
        mVideoManager.onRelease();
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
