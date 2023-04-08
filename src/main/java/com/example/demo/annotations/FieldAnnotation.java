package com.example.demo.annotations;

import com.example.demo.utils.FieldTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAnnotation {
    String dbFieldName() default "";
    int fieldType() default FieldTypes.stringType;
    boolean isPrimaryKey() default false;
}
