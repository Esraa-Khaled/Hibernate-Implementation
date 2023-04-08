package com.example.demo.utils;

import java.lang.reflect.Method;

public class FieldInfo {

    public int fieldType;
    public String fieldName;
    public String dbFieldName;
    public boolean isPrimary;
    public Method setMethod;
    public Method getMethod;

    public FieldInfo() {
    }

}
