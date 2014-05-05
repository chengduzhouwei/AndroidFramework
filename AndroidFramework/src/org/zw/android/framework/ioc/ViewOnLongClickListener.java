package org.zw.android.framework.ioc;

import java.lang.reflect.Method;

import android.view.View;

public final class ViewOnLongClickListener extends EventListener implements View.OnLongClickListener {

	protected ViewOnLongClickListener(Object token, String method) {
		super(token, method);
	}

	@Override
	public boolean onLongClick(View v) {
		
		if(token == null || methodName == null) return false;
		
		try{  
			
			Method method = token.getClass().getDeclaredMethod(methodName,View.class);
			Object reslut = null ;
			
			if(method != null){
				reslut = method.invoke(token, v);
			}
			
			if(reslut instanceof Boolean){
				return (Boolean)reslut ;
			}
			
			return false ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return false;
	}

}
