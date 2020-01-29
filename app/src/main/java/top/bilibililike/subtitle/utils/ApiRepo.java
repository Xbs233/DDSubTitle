package top.bilibililike.subtitle.utils;

import java.util.HashMap;

public class ApiRepo {
    public static final String LOGIN_HOST = "https://passport.bilibili.com";
    public static final String APP_HOST = "https://app.bilibili.com";
    public static final String API_HOST = "https://api.bilibili.com";
    public static final String BILIPLUS_HOST = "https://www.biliplus.com";

    public static final String APP_SECRET = "560c52ccd288fed045859ed18bffd973";
    public static final String VIDEO_SECRET = "aHRmhWMLkdeMuILqORnYZocwMBpMEOdt";

    public static final String VIDEO_APPKEY = "iVGUTjsxvpLeuDCf";

    private static final String ACTION_KEY = "appkey";
    private static final String APP_KEY = "1d8b6e7d45233436";
    private static final String BUILD = "5410000";
    private static final String CHANNEL = "bili";
    private static final String DEVICE = "android";
    private static final String MOBI_APP = "android";
    private static final String PLATFORM = "android";
    private static final String STATISTICS = "{\"appId\":1,\"PLATFORM\":3,\"version\":\"5.41.0\",\"abtest\":\"\"}";
    private static HashMap<String,String> params ;

    public static HashMap<String,String> getParams(){
        if (params != null){
            params.clear();
        }else {
            params = new HashMap<>();
        }
        params.put("appkey",APP_KEY);
        params.put("actionKey",ACTION_KEY);
        params.put("build",BUILD);
        params.put("channel",CHANNEL);
        params.put("device",DEVICE);
        params.put("mobi_app",MOBI_APP);
        params.put("platform",PLATFORM);
        params.put("statistics",STATISTICS);
        params.put("ts", String.valueOf(System.currentTimeMillis()).substring(0,10));
        return params;
    }


}
