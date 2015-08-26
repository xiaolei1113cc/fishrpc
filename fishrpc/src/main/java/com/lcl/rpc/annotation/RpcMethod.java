package com.lcl.rpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RpcMethod
 * @author lichunlei
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target( { ElementType.METHOD }) 
public @interface RpcMethod {
	String name();
}
