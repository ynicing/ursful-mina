package com.ursful.framework.database.annotaion;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RdTable {
	public String name();//user/create  user
}
