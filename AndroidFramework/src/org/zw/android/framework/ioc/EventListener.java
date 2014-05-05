package org.zw.android.framework.ioc;

public abstract class EventListener {
	
	protected Object token ;
	protected String methodName ;

	protected EventListener(Object token , String method){
		this.token		= token ;
		this.methodName = method ;
	}
}
