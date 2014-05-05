package org.zw.android.framework.ioc;

import java.lang.reflect.Method;

import android.view.View;
import android.widget.AdapterView;

public final class ViewAdapterOnItemClick extends EventListener implements AdapterView.OnItemClickListener{

	protected ViewAdapterOnItemClick(Object token, String method) {
		super(token, method);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		
		if(token == null || methodName == null) return;
		
		try{  
			
			Method method = token.getClass().getDeclaredMethod(methodName,
					AdapterView.class,
					View.class,
					int.class,
					long.class);
			
			if(method != null){
				method.invoke(token, parent,view,position,id);
			}
			
		} catch(Exception e){
			e.printStackTrace() ;
		}
	}

}
