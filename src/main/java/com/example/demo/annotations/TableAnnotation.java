package com.example.demo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) //specify exactly which kind of java element this annotations is valid to be on (class, method, field.)
@Retention(RetentionPolicy.RUNTIME)
/*
* SOURCE: means that java will get rid of this annotation before it even starts code compilation (like suppress warning annotation) >> only matters before compilation
* CLASS: means that java will keep this annotation around through compilation but once the program actually starts up at runtime it will get rid of it
* RUNTIME: means java keeps this annotation around throughout the actual running of the program, so we can access it will program is running using reflection
* */
public @interface TableAnnotation {
    String tableName();
}
