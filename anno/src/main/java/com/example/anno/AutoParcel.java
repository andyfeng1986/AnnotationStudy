package com.example.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // on class level
@Retention(RetentionPolicy.SOURCE)
public @interface AutoParcel {
    int version() default 0;
}