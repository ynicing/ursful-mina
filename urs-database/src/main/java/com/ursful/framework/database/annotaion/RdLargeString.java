package com.ursful.framework.database.annotaion;

import java.lang.annotation.*;

/**
 * 对String进行标注， mysql : text,  oracle : clob sql server:ntext
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RdLargeString {	
	String name() default "";
}
