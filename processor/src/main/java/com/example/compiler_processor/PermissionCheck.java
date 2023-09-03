package com.example.compiler_processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by hxb on 2019-07-01.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface PermissionCheck {
//    @Ignore Object oj();
//    @IdRes int value();

}
