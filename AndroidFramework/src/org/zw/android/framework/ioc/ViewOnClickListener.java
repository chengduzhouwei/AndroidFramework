package org.zw.android.framework.ioc;

import java.lang.reflect.Method;

import android.view.View;

public final class ViewOnClickListener extends EventListener implements View.OnClickListener{

	protected ViewOnClickListener(Object token, String method) {
		super(token, method);
	}

	@Override
	public void onClick(View v) {
		
		if(token == null || methodName == null) return;
		
		try{  
			
			Method method = token.getClass().getDeclaredMethod(methodName,View.class);
			
			if(method != null){
				method.invoke(token, v);
			}
			
		} catch(Exception e){
			e.printStackTrace() ;
		}
	}

}
