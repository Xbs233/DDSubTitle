package top.bilibililike.subtitle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    final static Pattern PATTERN= Pattern.compile("http://(.*).m3u8");
    public static void main(String[] args) throws JSONException {

        final String bigoHtml = "<script>\n" +
                "var CONFIG = {\n" +
                "    ignoreUids: \"1578156944\",\n" +
                "    uid: \"1540965042\",\n" +
                "    roomid: \"6341420486268646454\",\n" +
                "    nickName: \"꧁꧂\",\n" +
                "    bigoID: \"45756823\",\n" +
                "    videoSrc: \"http://183.236.60.82:7783/list_3457843305_2137879606_0.m3u8\",\n" +
                "    data5: \"http://esx.bigo.sg/live/g1/M0B/02/97/LXz8F1u3n7uIThEuAABNeO_xeXoAAbZqAIW2KAAAE2Q769.jpg\",\n" +
                "    bigoRoomChatValueUrl: \"\",\n" +
                "    roomStatus: \"2\",\n" +
                "    countryCode: \"\",\n" +
                "    sharerCode: \"\",\n" +
                "    wsUrl: \"ws://183.236.60.82:7783/wsconnect?2137879606&3457843305&7236&0\",\n" +
                "    cover: \"http://esx.bigo.sg/live/g1/M0B/02/97/LXz8F1u3n7uIThEuAABNeO_xeXoAAbZqAIW2KAAAE2Q769.jpg\"\n" +
                "}\n" +
                "</script>\n";
        //System.out.println(bigoHtml.matches("videoSrc:(.*),"));
        Matcher matcher = PATTERN.matcher(bigoHtml);
        if (matcher.find()) {
            String collegeId = matcher.group(0);
            System.out.println(collegeId);
        }
    }
}
