package com.obroom.filenamesort.util;

/**
 * 全半角转换工具类
 * 代码复制于：https://segmentfault.com/a/1190000010841143
 * 名称做了一些调整
 */
public class AsciiUtil {
    public static final char SBC_SPACE = 12288; //全角空格
    public static final char DBC_SPACE = 32; //半角空格
    public static final char ASCII_END = 126;//ascii结束
    public static final char UNICODE_START = 65281;//unicode开始
    public static final char UNICODE_END = 65374;//unicode结束
    public static final char DBC_SBC_STEP = 65248; // 全角半角转换间隔

    /**
     * 判断是否全角字符
     *
     * @param c
     * @return
     */
    public static boolean isFullChar(char c) {
        if (c == SBC_SPACE) {
            return true;
        }
        if (c >= UNICODE_START && c <= UNICODE_END) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否半角字符
     *
     * @param c
     * @return
     */
    public static boolean isHalfChar(char c) {
        if (c == DBC_SPACE) {
            return true;
        }
        if (c <= ASCII_END) {
            return true;
        }
        return false;
    }

    /**
     * 字符全角转半角
     *
     * @param src
     * @return
     */
    public static char full2half(char src) {
        if (src == SBC_SPACE) {
            return DBC_SPACE;
        }
        if (src >= UNICODE_START && src <= UNICODE_END) {
            return (char) (src - DBC_SBC_STEP);
        }
        return src;
    }

    /**
     * 字符串全角转半角
     *
     * @param src
     * @return
     */
    public static String full2half(String src) {
        if (src == null) {
            return null;
        }
        char[] c = src.toCharArray();
        for (int i = 0; i < c.length; i++) {
            c[i] = full2half(c[i]);
        }
        return new String(c);
    }
}