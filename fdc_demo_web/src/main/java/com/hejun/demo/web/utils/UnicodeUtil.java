package com.hejun.demo.web.utils;

import com.fdc.platform.common.yfutil.StringUtils;

/**
 * Created by hejun-FDC on 2017/4/13.
 */
public class UnicodeUtil {
    /**
     * 字符串转Unicode
     *
     * @param gbString
     * @return
     */
    public static String encodeUnicode(final String gbString) {
        if(StringUtils.isEmpty(gbString)) {
            return null;
        }
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    /**
     * Unicode转字符串
     *
     * @param dataStr
     * @return
     */
    public static String decodeUnicode(final String dataStr) {
        if(StringUtils.isEmpty(dataStr)) {
            return null;
        }
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }
}
