package top.bilibililike.subtitle.utils;

import android.os.Looper;
import android.widget.Toast;

import top.bilibililike.subtitle.MyApp;

/**
 * @author Xbs
 */
public class ToastUtil {
    public static void show(String str){
        Toast toast = Toast.makeText(MyApp.getContext(),null,Toast.LENGTH_SHORT);
        toast.setText(str);
        toast.show();
    }
}
