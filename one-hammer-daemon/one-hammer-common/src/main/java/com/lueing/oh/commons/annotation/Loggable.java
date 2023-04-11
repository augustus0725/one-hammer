package com.lueing.oh.commons.annotation;

import java.lang.annotation.*;

/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
@Target(value = ElementType.METHOD)
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Loggable {
}
