package top.bilibililike.subtitle;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.Bugly;

public class MyApp extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Bugly.init(mContext,"f040e49a29",true);
    }

    public static Context getContext(){
        return mContext;
    }
}
