package com.healthmarketscience.core;

public class NamingConversionUtils {

    /**
     * 驼峰命名法转下划线命名法
     * @param camelCase 驼峰命名的字符串
     * @return 转换后的下划线命名法字符串
     */
    public static String camelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }
        StringBuilder result = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append("_").append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * 下划线命名法转驼峰命名法
     * @param snakeCase 下划线命名的字符串
     * @return 转换后的驼峰命名法字符串
     */
    public static String snakeToCamel(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    result.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return result.toString();
    }

}
