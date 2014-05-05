package org.zw.android.framework.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Android Root Layout
 * @author zhouwei
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME) 
public @interface InjectLayout {
	
	/** root layout */
	public int layout() default InjectCore.DEFAULT_INTEGER ;
	
}
