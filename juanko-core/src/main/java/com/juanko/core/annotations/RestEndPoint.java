package com.juanko.core.annotations;

import com.juanko.core.integration.web.WebUtils;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author gaston
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestEndPoint {

    String type() default WebUtils.GET;
    String path() default "/";
}
