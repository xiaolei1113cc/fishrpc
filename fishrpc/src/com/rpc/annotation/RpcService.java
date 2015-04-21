package com.rpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RpcService
 * @author lichunlei
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME) 
@Target( { ElementType.TYPE })  
public @interface RpcService {

	String name();
}
