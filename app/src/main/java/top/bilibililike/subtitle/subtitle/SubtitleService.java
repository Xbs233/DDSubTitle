package top.bilibililike.subtitle.subtitle;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

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
public class SubtitleService extends Service implements DanmakuCallBack,ConfigurationChangedListener {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    SubTitleView subtitleView;
    WindowManager windowManager;
    SocketDataThread dataThread;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        ConfigurationReceiver receiver = new ConfigurationReceiver();
        receiver.bindListener(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        subtitleView = (SubTitleView) LayoutInflater.from(this).inflate(R.layout.layout_subtitle_view, null, false);
        dataThread = new SocketDataThread();
        dataThread.bind(this);
        return new LocalBinder();
    }

    public void linkStart(String roomId){

        //面包狗 21421141  aqua 14917277
        dataThread.start(roomId);
        executorService.execute(dataThread);
        addVerticalLayout();
    }

    /**
     * 横向布局 对应屏幕90度 270度
     */
    private void addHorizontalLayout(){
        if (subtitleView.isShown()){
            windowManager.removeViewImmediate(subtitleView);
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams.width = windowManager.getDefaultDisplay().getWidth();
        layoutParams.height = 160;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.y = windowManager.getDefaultDisplay().getHeight() - layoutParams.height;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.alpha = 0.8f;
        windowManager.addView(subtitleView, layoutParams);
    }

    /**
     * 竖直布局，对应屏幕0度 180度
     */
    private void addVerticalLayout(){
        if (subtitleView.isShown()){
            windowManager.removeViewImmediate(subtitleView);
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = 160;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.y = windowManager.getDefaultDisplay().getHeight() - layoutParams.height;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.alpha = 0.8f;
        windowManager.addView(subtitleView, layoutParams);
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
        if (angle == 90 || angle == 270){
            addVerticalLayout();
        }else if (angle == 180 || angle == 0 || angle == 360){
            addHorizontalLayout();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        windowManager.removeViewImmediate(subtitleView);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams.verticalMargin = 100;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams.width = 1800;
        layoutParams.height = 160;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.y = windowManager.getDefaultDisplay().getHeight() / 10 * 9;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.alpha = 0.8f;
        windowManager.addView(subtitleView, layoutParams);

    }
}