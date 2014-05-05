package org.zw.android.framework.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Android Resource
 * 
 * @author zhouwei
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface InjectResource {
	
	public int dimen() default InjectCore.DEFAULT_INTEGER ;	
	
	public int color() default InjectCore.DEFAULT_INTEGER ;	
	
	public int drawable() default InjectCore.DEFAULT_INTEGER ;	
	
	public int string() default InjectCore.DEFAULT_INTEGER ;	
	
	public int stringArray() default InjectCore.DEFAULT_INTEGER ;	
}
