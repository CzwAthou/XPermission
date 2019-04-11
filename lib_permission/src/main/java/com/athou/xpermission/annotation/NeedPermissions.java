package com.athou.xpermission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author athoucai
 * @date 2019/3/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedPermissions {

    NeedPermission[] value();

    /**
     * 权限请求结果的回调方法名
     * @return
     */
    String permissionResult() default "";
}