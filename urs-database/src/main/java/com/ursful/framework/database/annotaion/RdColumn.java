package com.ursful.framework.database.annotaion;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RdColumn {	
	String name();
	boolean allowNull() default true;
	boolean unique() default false;
}
