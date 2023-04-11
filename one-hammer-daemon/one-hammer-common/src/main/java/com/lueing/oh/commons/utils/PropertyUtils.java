/*
 *
 *  * Copyright (c) 2022. Jiangsu Hongwangweb Technology Co.,Ltd.
 *  * Licensed under the private license, you may not use this file except you get the License.
 *
 */

package com.lueing.oh.commons.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 陈虎
 * @since 2022-06-02 15:36
 */
public class PropertyUtils {
    private PropertyUtils() {
    }

    /**
     * 复制属性 忽略空值
     *
     * @param source 元对象
     * @param target 目标对象
     */
    public static void copyNotNullProperty(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper wrapper = new BeanWrapperImpl(source);
        Set<String> names = new HashSet<>();
        for (PropertyDescriptor pd : wrapper.getPropertyDescriptors()) {
            if (wrapper.getPropertyValue(pd.getName()) == null) {
                names.add(pd.getName());
            }
        }
        String[] results = new String[names.size()];
        return names.toArray(results);
    }
}
