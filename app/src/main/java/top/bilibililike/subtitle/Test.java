package top.bilibililike.subtitle;

import org.json.JSONException;
import org.json.JSONObject;

public class Test {
    public static void main(String[] args) throws JSONException {


        final String jsonData = "{\"uid\":5053396,\"roomid\":14917277}";


        System.out.println(Integer.toHexString(jsonData.getBytes().length));

    }
}
