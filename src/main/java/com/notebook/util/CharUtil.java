package com.notebook.util;

import java.util.Random;

public final class CharUtil {

    public static String getRandomString(Integer num) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        return getEncryptString(num, base);
    }

    public static String getRandomNum(Integer num) {
        String base = "0123456789";
        return getEncryptString(num, base);
    }

    private static String getEncryptString(Integer length, String base) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
