package top.bilibililike.subtitle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
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

import top.bilibililike.subtitle.WebSocket.DanmakuCallBack;
import top.bilibililike.subtitle.WebSocket.SocketDataThread;
import top.bilibililike.subtitle.utils.ConfigurationChangedListener;


public class MainActivity extends AppCompatActivity implements DanmakuCallBack, ConfigurationChangedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    TextView textView;

    private static final String APP_PACKAGE_NAME = "tv.danmaku.bili";

    View subtitleView;
    WindowManager windowManager;

    //   bilibili://live/14917277
    //面包狗 21421141  aqua 14917277  星街 190577 coco 21752686  高槻律 947447
    private static final String ROOMID = "14917277";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        textView.setOnClickListener(v -> {
            Intent intent = null;
            try {
                intent = Intent.parseUri("bilibili://live/" + ROOMID, Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "应用未安装", Toast.LENGTH_SHORT).show();
            }
            startActivity(intent);
        });
        linkStart();
        showWindow();
        ConfigutionReceiver receiver = new ConfigutionReceiver();
        IntentFilter filter = new IntentFilter("android.intent.action.CONFIGURATION_CHANGED");
        registerReceiver(receiver, filter);
        receiver.bindListener(this);


    }

    @Override
    public void onShow(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (subtitleView instanceof SubTitleView) {
                    ((SubTitleView) subtitleView).addSubtitle(str);
                }
            }
        });
    }


    private void showWindow() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        subtitleView = LayoutInflater.from(this).inflate(R.layout.layout_subtitle_view, null, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        if (windowManager == null){
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        int height = windowManager.getDefaultDisplay().getHeight();
        int width = windowManager.getDefaultDisplay().getWidth();
        layoutParams.height = (int) (height/12);
        layoutParams.width = width;
        layoutParams.alpha = 0.8f;
        if (width > height){
            layoutParams.width = layoutParams.width ^ layoutParams.height;
            layoutParams.height = layoutParams.width ^ layoutParams.height;
            layoutParams.width =    layoutParams.width ^ layoutParams.height;
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.y = windowManager.getDefaultDisplay().getHeight() / 5 * 4;
        windowManager.addView(subtitleView, layoutParams);



    }

    private void linkStart() {
        SocketDataThread dataThread = new SocketDataThread();
        dataThread.bind(this);

        dataThread.start(ROOMID, false);
        executorService.execute(dataThread);
    }

    @Override
    public void configurationChanged(int angle) {
        Log.d(TAG, "旋转角度：" + angle);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        if (windowManager == null){
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        windowManager.removeViewImmediate(subtitleView);
        int height = windowManager.getDefaultDisplay().getHeight();
        int width = windowManager.getDefaultDisplay().getWidth();
        switch (angle) {
            default:
            case 0:
            case 180:

                layoutParams.height = (int) (height/12);
                layoutParams.width = width;
                if (width > height){
                    //异或存值交换
                    Log.d(TAG,"0度交换前宽度 = " + layoutParams.width + "\n高度 = " + layoutParams.height);
                    layoutParams.width = layoutParams.width ^ layoutParams.height;
                    layoutParams.height = layoutParams.width ^ layoutParams.height;
                    layoutParams.width =    layoutParams.width ^ layoutParams.height;
                    Log.d(TAG,"0度交换后宽度 = " + layoutParams.width + "\n高度 = " + layoutParams.height);
                }else {
                    Log.d(TAG,"0度未交换宽度 = " + layoutParams.width + "\n高度 = " + layoutParams.height);
                }
                layoutParams.y = windowManager.getDefaultDisplay().getHeight() / 5 * 4;
                break;
            case 90:
            case 270:
                layoutParams.height = (int) (height/12);
                layoutParams.width = height;
                if (width > height){
                    //异或存值交换
                    Log.d(TAG,"90度交换前宽度 = " + layoutParams.width + "\n高度 = " + layoutParams.height);
                    layoutParams.width = layoutParams.width ^ layoutParams.height;
                    layoutParams.height = layoutParams.width ^ layoutParams.height;
                    layoutParams.width =    layoutParams.width ^ layoutParams.height;
                    Log.d(TAG,"90度交换后宽度 = " + layoutParams.width + "\n高度 = " + layoutParams.height);
                }else {
                    Log.d(TAG,"90度未交换宽度 = " + layoutParams.width + "\n高度 = " + layoutParams.height);
                }
                layoutParams.y = height / 7 * 6;

                break;

        }
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.alpha = 0.8f;
        Log.d(TAG,"height = " + windowManager.getDefaultDisplay().getHeight() + "\nwidth = " + windowManager.getDefaultDisplay().getWidth());
        windowManager.addView(subtitleView, layoutParams);



    }


}
