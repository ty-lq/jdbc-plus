package com.healthmarketscience.core;

import com.healthmarketscience.template.JdbcPlusTemplateFactory;
import com.healthmarketscience.wrapper.SFunction;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class LambdaUtils {
    private LambdaUtils() {
    }

    public static <T> String extractColumnName(SFunction<T, ?> func) {
        try {
            Method writeReplace = func.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(func);
            String methodName = serializedLambda.getImplMethodName();
            String result;
            if (methodName.startsWith("get")) {
                result = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            } else {
                result = methodName;
            }
            return JdbcPlusTemplateFactory.getJdbcPlusProperties().getMapUnderscoreToCamelCase() ? NamingConversionUtils.camelToSnake(result) : result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract field name from lambda", e);
        }
    }
}
