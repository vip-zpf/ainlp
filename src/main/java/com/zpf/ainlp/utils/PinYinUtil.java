package com.zpf.ainlp.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PinYinUtil {

    public static boolean checkDuoYin(String str){
        char[] chars = str.toCharArray();
        for (char aChar : chars) {
            if (hasMultiPinyin(aChar)) return true;
        }
        return false;
    }

    public static boolean hasMultiPinyin(char c) {
        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c);
        return pinyinArray != null && pinyinArray.length > 1;
    }

    public static String getPinYinStr(String text) {
        Pattern pattern = Pattern.compile("([a-z]+[1-5]{1})");
        Matcher matcher = pattern.matcher(text);
        List<String> result = new LinkedList<>();
        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                result.add(matcher.group(i));
            }
        }
        return StringUtils.join(result,",");
    }
}
