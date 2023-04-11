package com.lueing.oh.commons.utils;


import com.lueing.oh.commons.exception.BusinessException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 陈虎
 * @date 2022-06-15 13:38
 */
public class JsonUtilsTest {

    @Test
    public void convertMapList2BeanList() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("code", 200);
        map.put("message", "OK");
        List<Object> list = Collections.singletonList(map);
        List<BusinessException> exceptions = JsonUtils.convertMapList2BeanList(list, BusinessException.class);
        Assert.assertEquals(map.get("message"), exceptions.get(0).getMessage());
    }
}
