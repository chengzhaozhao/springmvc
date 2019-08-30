package com.czz.myspringmvc.annocation;

import java.lang.annotation.*;

/**
 * @author chengzhzh@datangmobile.com
 * @create 2019-08-29 17:24
 * @Documented
 * @Target(ElementType.TYPE)
 * @Retention(RetentionPolicy.RUNTIME)
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    public String value();
}
