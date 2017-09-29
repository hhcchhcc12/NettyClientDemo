package xiejie.com.myapplication.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhaiydong on 2017/4/26.
 */

public class TextUtil {
    public static String auth_key;

    /**
     * 将字符串转成MD5值
     *
     * @param string 需要转换的字符串
     * @return 字符串的MD5值
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }


    public static String createAuthKey() {
        String base = creteBaseString();
        String md5 = stringToMD5(base + "#" + "7Ng2Zh7Zn1Lf");
        return base + "#" + md5;
    }

    public static String creteBaseString() {
        return "mpoc" + "#" + System.currentTimeMillis() / 1000 + "#" + "v1.0" + "#" + "t-f44a4241-b940-45dd-8052-cd9666c1901b";
    }

    public static byte[] toHex(int size) {
        String str = Integer.toHexString(size).toUpperCase();
        StringBuffer buff = new StringBuffer();
        for (int i = str.length(); i > 0; i -= 2) {
            buff.append(str.substring(i - 2, i));
        }
        return buff.toString().getBytes();
    }


}
