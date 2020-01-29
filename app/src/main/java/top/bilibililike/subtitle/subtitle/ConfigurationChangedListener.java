package top.bilibililike.subtitle.subtitle;


/**
 * @author Xbs
 * @date 2020年1月15日17:14:38
 */
public interface ConfigurationChangedListener {
    /**
     * 屏幕转动回调
     * @param angle 转动角度
     */
    void configurationChanged(int angle);
}
