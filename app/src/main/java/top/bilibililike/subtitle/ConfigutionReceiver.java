package top.bilibililike.subtitle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.WindowManager;

import top.bilibililike.subtitle.utils.ConfigurationChangedListener;

public class ConfigutionReceiver extends BroadcastReceiver {
    ConfigurationChangedListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        listener.configurationChanged(windowManager.getDefaultDisplay().getRotation() * 90);
    }

    void bindListener(ConfigurationChangedListener listener){
        this.listener = listener;
    }


}
