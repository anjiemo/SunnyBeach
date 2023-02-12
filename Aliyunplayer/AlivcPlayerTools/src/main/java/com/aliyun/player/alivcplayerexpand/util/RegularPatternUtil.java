package com.aliyun.player.alivcplayerexpand.util;

/**
 * 正则表达式
 */
public class RegularPatternUtil {

    /**
     * 判断输入的字符串是否是数字
     *
     * @param inputText 要判断的字符串
     * @return true:是数字,false:不是数字
     */
    public static boolean isNumber(String inputText) {
        return inputText.matches("^(\\-|\\+)?\\d+(\\.\\d+)?$");
    }
}
