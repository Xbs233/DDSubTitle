package top.bilibililike.subtitle.subtitle;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import top.bilibililike.subtitle.R;
import top.bilibililike.subtitle.subtitle.WebSocket.DanmakuCallBack;
import top.bilibililike.subtitle.subtitle.WebSocket.SocketDataThread;

/**
 * @author Xbs
 */
public class SubtitleService extends Service implements DanmakuCallBack, ConfigurationChangedListener {
    public static final String TAG = SubtitleService.class.getSimpleName();
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private SubtitleView subtitleView;
    private WindowManager windowManager;
    private SocketDataThread dataThread;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        IntentFilter filter = new IntentFilter("android.intent.action.CONFIGURATION_CHANGED");
        ConfigurationReceiver receiver = new ConfigurationReceiver();
        registerReceiver(receiver, filter);
        receiver.bindListener(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        subtitleView = (SubtitleView) LayoutInflater.from(this).inflate(R.layout.layout_subtitle_view, null, false);

        return new LocalBinder();
    }

    public void linkStart(String roomId) {
        if (dataThread != null) {
            dataThread.stop();
            dataThread = null;
        }
        dataThread = new SocketDataThread();
        dataThread.bind(this);
        dataThread.start(roomId);
        executorService.execute(dataThread);
        updateSubtitleView(0);
    }



    @Override
    public void onShow(String str) {
        Observable.just("0")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        subtitleView.addSubtitle(str);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public class LocalBinder extends Binder {
        public SubtitleService getService() {
            return SubtitleService.this;
        }
    }

    @Override
    public void configurationChanged(int angle) {
        subtitleView.setVisibility(View.INVISIBLE);
        Log.d(TAG, "旋转角度：" + angle);
        if (angle == 90 || angle == 270) {
            updateSubtitleView(90);
        } else if (angle == 180 || angle == 0 || angle == 360) {
            updateSubtitleView(0);
        }
        subtitleView.setVisibility(View.VISIBLE);
    }

    private void updateSubtitleView(int gravity) {
        final float heightParam;
        if (gravity == 0) {
            //竖直
            heightParam = 0.083f;
        } else {
            heightParam = 0.148f;
        }
        if (dataThread == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //WindowManager.LayoutParams.TYPE_PHONE
        //TYPE_APPLICATION_OVERLAY
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams.width = windowManager.getDefaultDisplay().getWidth();
        layoutParams.height = (int) (windowManager.getDefaultDisplay().getHeight() * heightParam);
        layoutParams.height *= 1.5;
        layoutParams.gravity = Gravity.END;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.y = windowManager.getDefaultDisplay().getHeight() - layoutParams.height * 2;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.alpha = 0.8f;
        if (subtitleView.isAttachedToWindow()) {
            windowManager.updateViewLayout(subtitleView, layoutParams);
        } else {
            windowManager.addView(subtitleView, layoutParams);
        }
    }

}