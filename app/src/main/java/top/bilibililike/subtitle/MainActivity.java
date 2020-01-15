package top.bilibililike.subtitle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import top.bilibililike.subtitle.utils.ConfigurationChangedListener;


public class MainActivity extends AppCompatActivity implements DanmakuCallBack, ConfigurationChangedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    TextView textView;

    private static final String APP_PACKAGE_NAME = "tv.danmaku.bili";

    View subtitleView;
    WindowManager windowManager;

    //   bilibili://live/14917277


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        textView.setOnClickListener( v -> {
            Intent intent = null;
            try {
                intent = Intent.parseUri("bilibili://live/14917277",Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"应用未安装",Toast.LENGTH_SHORT).show();
            }
            startActivity(intent);
        });
        linkStart();
        showWindow();
        ConfigutionReceiver receiver = new ConfigutionReceiver();
        IntentFilter filter = new IntentFilter("android.intent.action.CONFIGURATION_CHANGED");
        registerReceiver(receiver,filter);
        receiver.bindListener(this);


    }

    @Override
    public void onShow(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (subtitleView instanceof SubTitleView){
                    ((SubTitleView) subtitleView).addSubtitle(str);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*windowManager.removeViewImmediate(subtitleView);

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
        windowManager.addView(subtitleView, layoutParams);*/

    }

    private void showWindow(){
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        subtitleView = LayoutInflater.from(this).inflate(R.layout.layout_subtitle_view, null, false);
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
    }

    private void linkStart(){
        SocketDataThread dataThread = new SocketDataThread();
        dataThread.bind(this);
        //面包狗 21421141  aqua 14917277  星街190577
        dataThread.start("14917277", false);
        executorService.execute(dataThread);
    }

    @Override
    public void configurationChanged(int angle) {
        Log.d(TAG,"旋转角度："+angle);
    }

    //跳转页面的方法
    private void launchapp(Context context) {
        //判断当前手机是否有要跳入的app
        if (isAppInstalled(context,APP_PACKAGE_NAME)){
            //如果有根据包名跳转
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(APP_PACKAGE_NAME));
        }else{
            //如果没有，走进入系统商店找到这款APP，提示你去下载这款APP的程序
            goToMarket(context, APP_PACKAGE_NAME);
        }
    }
    //这里是进入应用商店，下载指定APP的方法。
    private void goToMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (Exception e) {
        }
    }
    //这里是判断APP中是否有相应APP的方法
    private boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName,0);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
