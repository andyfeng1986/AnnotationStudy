package com.example.fenglei.annotationstudy.api;

/**
 * Created by li.zhipeng on 2017/3/17.
 *
 *      注入接口
 */

public interface ViewInject<T> {
    void inject(T target, Object source);
}
