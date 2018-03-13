package com.obroom.filenamesort.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件名称采用windows资源管理器的排序算法排列
 * <p>
 * 参考了网络上别人研究的文章，但没发现可以完全解决Windows文件名的排序
 * 后面自己对规则做了大量研究，特别是特殊字符的排序处理
 * 目前只把中英文(全半角)所有键盘能直接打出来的特殊字符做了处理
 * 未对其他语言做测试
 * 中英文的文件名目前测试结果完全一致
 *
 * @author ob
 * @since 20180202
 */
public class FileNameComparator implements Comparator<String> {
    private static final Pattern DIGIT_PATTERN = Pattern.compile("^\\d*\\d$");//数字匹配
    private static final Pattern LETTER_PATTERN = Pattern.compile("^\\w");//字母匹配
    private static final Pattern SPLIT_PATTERN = Pattern.compile("-{0,}\\d{1,}|-{0,}\\D{1}");
    private static final String SPECIAL_STRING = " !\"#$%()*,、.-。/:;?@[\\]^`{|}~‘’“”《》￥【】+<=>×…";//特殊字符(全半角)正则匹配
    private static Map<String, Integer> sortMap = new HashMap<>();//特殊字符(中英文)内部排序号

    static {
        String[] specials = SPECIAL_STRING.split("");
        for (int i = 0, j = 10; i < specials.length; ++i, j += 10) {
            sortMap.put(specials[i], j);
        }
        sortMap.put("\\d", 1000);
    }

    /**
     * 字符串排序
     *
     * @param str1
     * @param str2
     * @return
     */
    @Override
    public int compare(String str1, String str2) {
        int result = 0;
        //先把字符串全角(如果有)转成半角
        String str3 = AsciiUtil.full2half(str1);
        String str4 = AsciiUtil.full2half(str2);
        //按正则切割字符
        Iterator<String> iter1 = splitString(str3).iterator();
        Iterator<String> iter2 = splitString(str4).iterator();
        //进入比对
        while (true) {
            if (!iter1.hasNext() && !iter2.hasNext()) {
                //全半角字符比对
                Iterator<String> iter3 = splitString(str1.replace("－", "")).iterator();
                Iterator<String> iter4 = splitString(str2.replace("－", "")).iterator();
                while (true) {
                    if (!iter3.hasNext() && !iter4.hasNext()) {
                        if (str1.contains("-") && str2.contains("－")) {
                            return -1;
                        } else if (str1.contains("－") && str2.contains("-")) {
                            return 1;
                        } else {
                            //减号位数和位置比对
                            Iterator<String> iter5 = splitString(str3).iterator();
                            Iterator<String> iter6 = splitString(str4).iterator();
                            while (true) {
                                if (!iter5.hasNext() && !iter6.hasNext()) {
                                    return result;
                                }
                                if (!iter5.hasNext() && iter6.hasNext()) {
                                    return -1;
                                }
                                if (iter5.hasNext() && !iter6.hasNext()) {
                                    return 1;
                                }
                                String str9 = iter5.next();
                                String str10 = iter6.next();
                                if (str9.contains("-") && !str10.contains("-")) {
                                    return 1;
                                } else if (!str9.contains("-") && str10.contains("-")) {
                                    return -1;
                                } else if (str9.contains("-") && str10.contains("-")) {
                                    result = str9.length() - str10.length();
                                    if (result != 0) {
                                        return result;
                                    }
                                }
                            }
                        }
                    }
                    if (!iter3.hasNext() && iter4.hasNext()) {
                        return -1;
                    }
                    if (iter3.hasNext() && !iter4.hasNext()) {
                        return 1;
                    }
                    String str7 = iter3.next().replace("-", "").replace("－", "");
                    String str8 = iter4.next().replace("-", "").replace("－", "");
                    if (str7.length() == str8.length() && str7.length() == 1) {
                        char c1 = str7.charAt(0);
                        char c2 = str8.charAt(0);
                        if (AsciiUtil.full2half(c1) == AsciiUtil.full2half(c2)) {
                            if (AsciiUtil.isHalfChar(c1) && AsciiUtil.isFullChar(c2)) {
                                return -1;
                            } else if (AsciiUtil.isFullChar(c1) && AsciiUtil.isHalfChar(c2)) {
                                return 1;
                            }
                        }
                    }
                }
            }
            if (!iter1.hasNext() && iter2.hasNext()) {
                return -1;
            }
            if (iter1.hasNext() && !iter2.hasNext()) {
                return 1;
            }
            String str5 = iter1.next().replace("-", "");
            String str6 = iter2.next().replace("-", "");
            try {
                //如果都是数字，按大小比对
                result = Long.compare(Long.valueOf(str5), Long.valueOf(str6));
                //如果数值相等，按位数大小比对
                if (result == 0) {
                    if (str3.contains(str5) && !str4.contains(str6)) {
                        result = -1;
                    } else if (!str3.contains(str5) && str4.contains(str6)) {
                        result = -1;
                    } else {
                        result = str6.length() - str5.length();
                    }
                }
            } catch (NumberFormatException ex) {
                //非数字比对，判断是否特殊字符，采用特殊字符排序，否则采用常规字符排序
                boolean isDigit1 = DIGIT_PATTERN.matcher(str5).find();
                boolean isDigit2 = DIGIT_PATTERN.matcher(str6).find();
                if (isDigit1 && sortMap.containsKey(str6)) {
                    result = 1;
                } else if (isDigit2 && sortMap.containsKey(str5)) {
                    result = -1;
                } else if (sortMap.containsKey(str5) && sortMap.containsKey(str6)) {
                    result = sortMap.get(str5) - sortMap.get(str6);
                } else {
                    //字母比对
                    boolean isLetter1 = LETTER_PATTERN.matcher(str5).find();
                    boolean isLetter2 = LETTER_PATTERN.matcher(str6).find();
                    if (isLetter1 && sortMap.containsKey(str6)) {
                        result = 1;
                    } else if (isLetter2 && sortMap.containsKey(str5)) {
                        result = -1;
                    } else {
                        //如果是中文汉字，采用拼音顺序比对
                        if (isChinese(str5) && isChinese(str6)) {
                            result = str6.compareToIgnoreCase(str5);
                        } else {
                            result = str5.compareToIgnoreCase(str6);
                        }
                    }
                }
            }
            if (result != 0) {
                return result;
            }
        }
    }

    /**
     * 字符串按特殊字符正则切割
     *
     * @param str
     * @return
     */
    public static List<String> splitString(String str) {
        Matcher matcher = SPLIT_PATTERN.matcher(str);
        List<String> list = new ArrayList<>();
        int pos = 0;
        while (matcher.find()) {
            list.add(matcher.group());
            pos = matcher.end();
        }
        list.add(str.substring(pos));
        return list;
    }

    /**
     * 判断字符串是否包含中文字符
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        if (str == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FBF]+");
        return pattern.matcher(str.trim()).find();
    }
}