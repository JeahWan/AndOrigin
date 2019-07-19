package com.base.and.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class StringUtils {

    public static boolean empty(String text) {
        if (TextUtils.isEmpty(text)) {
            return true;
        } else return text.length() == 4
                && text.toUpperCase().equals("NULL");
    }

    public static boolean equal(String text1, String text2) {
        if (empty(text1) || empty(text2))
            return false;
        else return text1.equals(text2);
    }

    public static boolean isEmail(String text) {
        if (empty(text)) return false;
        return text.matches(Patterns.EMAIL_ADDRESS.pattern());
    }

    public static boolean isMobile(String text) {
        if (empty(text)) return false;
        return text.matches("^(13|14|15|16|17|18|19)\\d{9}$");
    }

    public static boolean isWebSite(String text) {
        if (empty(text)) return false;
        return text.matches(Patterns.WEB_URL.pattern());
    }

    public static boolean isNumber(String text) {
        if (empty(text)) return false;
        try {
            Double.parseDouble(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否有此文件
     *
     * @param path
     * @return
     */
    public static boolean hasFile(String path) {
        return getFile(path) != null;
    }

    /**
     * 获取是否有此文件
     *
     * @param text
     * @return
     */
    public static File getFile(String text) {
        if (empty(text)) return null;
        File file = new File(text);
        if (file != null && file.exists()) {
            return file;
        }
        return null;
    }

    public static String getFloat(float number, int index) {
        return getDouble(number, index);
    }

    public static String getDouble(double number, int index) {
        String str = "##0";
        if (index > 0) {
            str += ".";
            for (int i = 0; i < index; i++) {
                str += "0";
            }
        }
        DecimalFormat fnum = new DecimalFormat(str);
        return fnum.format(number);
    }

    public static double getDouble(String text) {
        if (isNumber(text)) {
            return Double.parseDouble(text);
        }
        return 0;
    }

    public static int getInt(String text) {
        return getInt(text, 0);
    }

    public static int getInt(String text, int def) {
        if (isNumber(text)) {
            return Integer.parseInt(text);
        }
        return def;
    }

    public static long getLong(String text) {
        if (isNumber(text)) {
            return Long.parseLong(text);
        }
        return 0;
    }

    public static Uri getUri(String text) {
        if (!empty(text)) {
            return Uri.parse(text);
        }
        return null;
    }

    /**
     * 加密
     **/
    public static String encryptPassword(String clearText, String password) {
        try {
            DESKeySpec keySpec = new DESKeySpec(password.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            String encrypedPwd = Base64.encodeToString(cipher.doFinal(clearText.getBytes(StandardCharsets.UTF_8)), Base64.DEFAULT);
            return encrypedPwd;
        } catch (Exception e) {
        }
        return clearText;
    }

    /**
     * 解密
     **/
    public static String decryptPassword(String encryptedPwd, String password) {
        try {
            DESKeySpec keySpec = new DESKeySpec(password.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);
            byte[] encryptedWithoutB64 = Base64.decode(encryptedPwd, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        } catch (Exception e) {
        }
        return encryptedPwd;
    }
}
