package com.github.son_daehyeon.common.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON을 다루기 위한 유틸리티 클래스입니다.
 */
public class JsonMapper {

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    /**
     * 객체를 JSON 문자열로 변환합니다.
     *
     * @param object 변환할 객체
     *
     * @return JSON 문자열
     */
    public static String toJson(Object object) {

        return gson.toJson(object);
    }

    /**
     * JSON 문자열을 객체로 변환합니다.
     *
     * @param json JSON 문자열
     *
     * @return 변환된 객체
     */
    public static Map<String, Object> fromJson(String json) {

        TypeToken<Map<String, Object>> typeToken = new TypeToken<>() {

        };

        //noinspection unchecked
        return (Map<String, Object>) normalize(gson.fromJson(json, typeToken.getType()));
    }

    /**
     * 일반화된 객체를 정규화합니다.
     *
     * @param value 변환할 객체
     *
     * @return 정규화된 객체
     */
    public static Object normalize(Object value) {

        switch (value) {
            case Map<?, ?> originalMap -> {
                Map<Object, Object> newMap = new LinkedHashMap<>();

                for (Map.Entry<?, ?> entry : originalMap.entrySet()) {

                    newMap.put(entry.getKey(), normalize(entry.getValue()));
                }

                return newMap;
            }
            case List<?> originalList -> {
                List<Object> newList = new ArrayList<>();

                for (Object item : originalList) {

                    newList.add(normalize(item));
                }

                return newList;
            }
            case Double d -> {
                if (d % 1 == 0) {
                    return d.intValue();
                }
                return d;
            }
            case null, default -> {
                return value;
            }
        }
    }
}
