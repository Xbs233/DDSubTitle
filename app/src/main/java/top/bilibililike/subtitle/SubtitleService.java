package top.bilibililike.subtitle;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.AndroidException;
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
import top.bilibililike.subtitle.WebSocket.DanmakuCallBack;
import top.bilibililike.subtitle.WebSocket.SocketDataThread;
import top.bilibililike.subtitle.danmuSocket.DanmakuCallBack;
import top.bilibililike.subtitle.danmuSocket.SocketDataThread;

/**
 * @author Xbs
 */
public class SubtitleService extends Service implements DanmakuCallBack {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    View subtitleView;
    WindowManager windowManager;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        SocketDataThread dataThread = new SocketDataThread();
        dataThread.bind(this);
        //面包狗 21421141  aqua 14917277
        dataThread.start("14917277");
        executorService.execute(dataThread);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        subtitleView = LayoutInflater.from(this).inflate(R.layout.layout_subtitle, null, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams.width = 1800;
        layoutParams.height = 160;
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.y = windowManager.getDefaultDisplay().getHeight() / 10 * 9;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.alpha = 0.8f;
        layoutParams.verticalMargin = 100;
        windowManager.addView(subtitleView, layoutParams);
        return new LocalBinder();
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
                        TextView subtitle1 = subtitleView.findViewById(R.id.tv_sub1);
                        TextView subtitle2 = subtitleView.findViewById(R.id.tv_sub2);
                        if (subtitle1 != null && subtitle2 != null) {
                            subtitle2.setText(subtitle1.getText().toString());
                            subtitle1.setText(str.substring(1, str.length() - 1));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        /*TextView subtitle1 = subtitleView.findViewById(R.id.tv_sub1);
        TextView subtitle2 = subtitleView.findViewById(R.id.tv_sub2);
        if (subtitle1 != null && subtitle2 != null) {
            subtitle2.setText(subtitle1.getText().toString());
            subtitle1.setText(str.substring(1, str.length() - 1));
        }*/

        /*Intent intent = new Intent();
        intent.putExtra("danmu",str.substring(1, str.length() - 1));
        sendBroadcast(intent);*/
    }


    public class LocalBinder extends Binder {
        public SubtitleService getService() {
            return SubtitleService.this;
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