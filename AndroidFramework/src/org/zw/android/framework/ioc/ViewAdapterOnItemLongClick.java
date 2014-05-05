package org.zw.android.framework.ioc;

import java.lang.reflect.Method;

import android.view.View;
import android.widget.AdapterView;

public final class ViewAdapterOnItemLongClick extends EventListener implements AdapterView.OnItemLongClickListener{

	protected ViewAdapterOnItemLongClick(Object token, String method) {
		super(token, method);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
		
		if(token == null || methodName == null) return false ;
		
		try{  
			
			Method method = token.getClass().getDeclaredMethod(methodName,
					AdapterView.class,
					View.class,
					int.class,
					long.class);
			
			Object result = null ;
			
			if(method != null){
				result = method.invoke(token, parent,view,position,id);
			}
			
			if(result instanceof Boolean){
				return (Boolean) result ;
			}
			
			return false ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return false;
	}

}
