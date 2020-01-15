package top.bilibililike.subtitle;

public interface DanmakuCallBack {
    /**
     * 来了他来了，同传man来了
     * @param str 同传弹幕（去除【】括号）
     */
    void onShow(String str);
}
