package top.bilibililike.subtitle.utils;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Util{


    /**
     * 对字符串md5加密(小写+字母)
     *
     * @param str 传入要加密的字符串
     * @return  MD5加密后的字符串
     */
    public static String getMD5(String str) {
        //Log.d("LoginModel md5Str = ",str);
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            String result = new BigInteger(1, md.digest()).toString(16);
            //  Log.d("LoginModel md5Sign = ",result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 对字符串md5加密(大写+数字)
     *
     * @param s 传入要加密的字符串
     * @return  MD5加密后的字符串
     */

    public static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String test = "APP_KEY=1d8b6e7d45233436&BUILD=5410000&CHANNEL=bili&MOBI_APP=android&password=l6SrdDiOHONk93gik1r+r9n0iE7GVvE50go3pzkt2ckRXwQeqUY4BtZtm2%2FVA%2FMUgSI72gTGp44vURuUcJ%2FvNVhaQu0T70O2NpcjJ6HdO4Xw1p7jlOYyo8MkSYce1ht7wi%2F36kaCaqk9LyTT75Bip8tUYTiuGJ7E6sGphdXPTBo%3D&PLATFORM=android&ts=1564418193&username=17670530020560c52ccd288fed045859ed18bffd973";
        System.out.println(getMD5(data1+ApiRepo.APP_SECRET));
        System.out.println(getMD5(data2+ApiRepo.APP_SECRET));
        System.out.println(getMD5(data3+ApiRepo.APP_SECRET));

    }


    static String data1 = "access_key=e089ec788fc0cd8c8e9355972aa47a81&ad_extra=0&aid=62579255&APP_KEY=1d8b6e7d45233436&autoplay=0&BUILD=5410000&CHANNEL=bili&fnval=16&fnver=0&force_host=0&fourk=0&from=7&from_spmid=tm.recommend.0.0&MOBI_APP=android&plat=0&PLATFORM=android&qn=32&spmid=0&STATISTICS=%7B%22appId%22%3A1%2C%22platform%22%3A3%2C%22version%22%3A%225.41.0%22%2C%22abtest%22%3A%22%22%7D&trackid=all_16.shylf-ai-recsys-87.1564645385020.392&ts=1565204459";

    static String result1 = "cc1afb9a0c08992ac3c3ecbfbb6d4550";


    static String data2 = "APP_KEY=1d8b6e7d45233436&BUILD=5410000&CHANNEL=bili&MOBI_APP=android&PLATFORM=android&STATISTICS=%7B%22appId%22%3A1%2C%22platform%22%3A3%2C%22version%22%3A%225.41.0%22%2C%22abtest%22%3A%22%22%7D&ts=1564458461";
    static String result2 = "4d56ac05164408b88f58102b4258d076";

    static String data3 = "APP_KEY=1d8b6e7d45233436&BUILD=5410000&CHANNEL=bili&MOBI_APP=android&password=YewI0mO3%2FlEgtJ3gqwukeMWjf5l0rao%2BxdMOxJdaE0ojXoKFC7xtECGEYWjAk%2F25rGTokTooNkJn%0Atq1WlqGAxSWXrO3FtrhvYM2vRZUgoxorWKVcWdkn9TEQYqSX%2FsSplAMTuQLlWOD8W5QINISbDOnS%0Ar2Nn8mS8Moa78FjjI%2BE%3D%0A&PLATFORM=android&STATISTICS=%7B%22appId%22%3A1%2C%22platform%22%3A3%2C%22version%22%3A%225.41.0%22%2C%22abtest%22%3A%22%22%7D&ts=1560147559&username=17670530020";
    static String result3 = "8e17332b2a3954613eb452ea48fe800e";

}
