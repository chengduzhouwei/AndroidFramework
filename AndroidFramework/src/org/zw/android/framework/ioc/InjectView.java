package org.zw.android.framework.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Android View
 * @author zhouwei
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME) 
public @interface InjectView {
	
	/** 父容器 */
	public String parent() default InjectCore.DEFAULT_STRING ;
	
	/** view 的layout */
	public int layout() default InjectCore.DEFAULT_INTEGER ;
	
	/** view 的id */
	public int id() default InjectCore.DEFAULT_INTEGER ;	
	
	/** View 的onClick 事件实现对象: this|类事例对象 */
	public String onClick() default InjectCore.DEFAULT_STRING ;
	
	/** View 的onLongClick 事件 */
	public String onLongClick() default InjectCore.DEFAULT_STRING ;
	
	/** AdapterView 的onItemClickListener 事件 */
	public String onItemClickListener() default InjectCore.DEFAULT_STRING ;
	
	/** AdapterView 的onItemLongClickListener 事件 */
	public String onItemLongClickListener() default InjectCore.DEFAULT_STRING ;
}
