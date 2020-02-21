package top.bilibililike.subtitle.subtitle;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
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
    public static final String TAG = SubtitleService.class.getSimpleName();
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    SubTitleView subtitleView;
    WindowManager windowManager;
    SocketDataThread dataThread;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        IntentFilter filter = new IntentFilter("android.intent.action.CONFIGURATION_CHANGED");
        ConfigurationReceiver receiver = new ConfigurationReceiver();
        registerReceiver(receiver, filter);
        receiver.bindListener(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        subtitleView = (SubTitleView) LayoutInflater.from(this).inflate(R.layout.layout_subtitle_view, null, false);

        return new LocalBinder();
    }

    public void linkStart(String roomId){
        if (dataThread != null){
            dataThread.stop();
            dataThread = null;
        }
        //面包狗 21421141  aqua 14917277
        dataThread = new SocketDataThread();
        dataThread.bind(this);
        dataThread.start(roomId);
        executorService.execute(dataThread);
        addVerticalLayout();
    }

    /**
     * 横向布局 对应屏幕90度 270度
     */
    private void addHorizontalLayout(){
        if (dataThread == null){
            return;
        }
/*        if (subtitleView.isShown()){
            windowManager.removeViewImmediate(subtitleView);
        }*/
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams.width = windowManager.getDefaultDisplay().getWidth();
        layoutParams.height = (int) (windowManager.getDefaultDisplay().getHeight() * 0.148);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.y = windowManager.getDefaultDisplay().getHeight() - layoutParams.height;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.alpha = 0.8f;
        if (subtitleView.isShown()){
            windowManager.updateViewLayout(subtitleView,layoutParams);
        }else {
            if (!subtitleView.isAttachedToWindow()){
                windowManager.addView(subtitleView, layoutParams);
            }
        }
        Log.d(TAG,"90度/270度width = " + layoutParams.width + "\theight = " + layoutParams.height);
    }

    /**
     * 竖直布局，对应屏幕0度 180度
     */
    private void addVerticalLayout(){
        if (dataThread == null){
            return;
        }
 /*       if (subtitleView.isShown()){
            windowManager.removeViewImmediate(subtitleView);
        }*/
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams.width = windowManager.getDefaultDisplay().getWidth();
        layoutParams.height = (int) (windowManager.getDefaultDisplay().getHeight() * 0.083);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.y = windowManager.getDefaultDisplay().getHeight() - layoutParams.height;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.alpha = 0.8f;
        if (subtitleView.isShown()){
            windowManager.updateViewLayout(subtitleView,layoutParams);
        }else {
            if (!subtitleView.isAttachedToWindow()){
                windowManager.addView(subtitleView, layoutParams);
            }
        }

        Log.d(TAG,"0度/180度width = " + layoutParams.width + "\theight = " + layoutParams.height);
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
        subtitleView.setVisibility(View.VISIBLE);
        Log.d(TAG,"旋转角度：" + angle);
        if (angle == 90 || angle == 270){
            addHorizontalLayout();
        }else if (angle == 180 || angle == 0 || angle == 360){
            addVerticalLayout();
        }
    }

}