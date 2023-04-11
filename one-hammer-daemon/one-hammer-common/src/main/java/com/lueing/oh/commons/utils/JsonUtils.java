/*
 *
 *  * Copyright (c) 2022. Jiangsu Hongwangweb Technology Co.,Ltd.
 *  * Licensed under the private license, you may not use this file except you get the License.
 *
 */

package com.lueing.oh.commons.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 陈虎
 */
public class JsonUtils {
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private JsonUtils() {
    }

    /**
     * 将集合对象转换为指定的bean集合
     *
     * @param list  原数据
     * @param clazz bean类型
     * @param <T>   泛型
     * @return bean集合
     */
    public static <T> List<T> convertMapList2BeanList(List<Object> list, Class<T> clazz) {
        return list.stream().map(e -> GSON.fromJson(GSON.toJson(e), clazz)).collect(Collectors.toList());
    }
}
