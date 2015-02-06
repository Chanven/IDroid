package com.andy.demo.zxing.activity;

import java.io.IOException;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.andy.android.util.DLog;
import com.andy.demo.R;
import com.andy.demo.activity.BaseActivity;
import com.andy.demo.utils.ScreenUtils;
import com.andy.demo.zxing.camera.CameraManager;
import com.andy.demo.zxing.decoding.CaptureActivityHandler;
import com.andy.demo.zxing.decoding.InactivityTimer;
import com.andy.demo.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

public class CaptureActivity extends BaseActivity implements Callback{
    
    private static final String TAG = "CaptureActivity";
    
    private ViewfinderView viewfinderView;
    private ImageView qcode_scan_middle_line_iv;
    SurfaceView surfaceView;
    
    // 方框镜头的Rect
    private Rect frame;
    
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_layout);
        initView();
    }
    
    private void initView() {
        CameraManager.init(this);
        
        qcode_scan_middle_line_iv = findView(R.id.qcode_scan_middle_line_iv);
        viewfinderView = findView(R.id.viewfinder_view);
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;    
        
        frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            // 第一次进入时frame为空，要延迟执行，等ViewfinderView调用onDraw方法getFramingRect才有值
            new Handler().postDelayed(new Runnable(){                
                @Override
                public void run() {
                    frame = CameraManager.get().getFramingRect();
                    if (frame != null) {
                        setImageViewScanAnimation();
                    }                    
                }
            }, 500);
        } else {
            frame = CameraManager.get().getFramingRect();
            setImageViewScanAnimation();
        }        
    }
    
    /**
     * 设置ImageView的扫描动画
     */
    private void setImageViewScanAnimation() {
        if (qcode_scan_middle_line_iv.getVisibility() == View.VISIBLE) {
            qcode_scan_middle_line_iv.clearAnimation();
            int margintTop = ScreenUtils.px2dip(this, 10.0f);
            int marginBottom = ScreenUtils.px2dip(this, 40.0f);
            TranslateAnimation animation =
                            new TranslateAnimation(0, 0, frame.top - margintTop, frame.bottom - marginBottom);
            animation.setDuration(3000);    // 设置动画持续时间
            animation.setRepeatCount(-1);   // 设置重复次数
            animation.setInterpolator(new LinearInterpolator());    //匀速
            qcode_scan_middle_line_iv.startAnimation(animation);
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();  
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }
    
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        DLog.i(TAG, "surfaceCreated");
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }
    
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }
    
    /**
     * Handler scan result
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        final String resultString = result.getText();
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(CaptureActivity.this, "扫码失败", Toast.LENGTH_SHORT).show();
        } else {
            // handler result
            handleResult(resultString);
        }  
    }
    
    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
    
    private void handleResult(String result) {
        Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(null);
        dialog.setMessage(result);
        dialog.setPositiveButton("OK", new OnClickListener(){
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resumeCamera();
            }
        });
        dialog.show();
    }
    
    /**恢复相机可扫描*/
    private void resumeCamera() {
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        handler = null;
        initCamera(surfaceHolder);
    }

}
