package top.bilibililike.subtitle.subtitle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

/**
 * @author Xbs
 */
public class ConfigurationReceiver extends BroadcastReceiver {
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
