package com.base.and.api;

import java.io.Serializable;

public class BaseData<T> implements Serializable {
    //根据服务器返回数据结构定义
    public String message;
    public int code;
    //泛型指定返回的数据结构 如自定义bean、List、String[]等任意
    public T data;
}
