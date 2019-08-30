package com.czz.myspringmvc.annocation;

import java.lang.annotation.*;

/**
 * @author chengzhzh@datangmobile.com
 * @create 2019-08-29 17:26
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    public String value();
}
