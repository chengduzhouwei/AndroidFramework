package test;

import org.zw.android.framework.http.HttpAsyncTask;
import org.zw.android.framework.http.IObjectWrapper;

public class LoginTask extends HttpAsyncTask implements IObjectWrapper {

	public LoginTask(){
		setUrl("http://img10.3lian.com/d0214/file/2011/11/25/89a92fa59782aa6eb6bf106912a29fab.jpg");
		setMethodType(MethodType.GET);
		addProperty("Content-Type", "application/json");
		setObjectWrapper(this);
	}

	@Override
	public Object wrapper(Object value) {
		
		System.out.println("------------------");
		
		return value ;
	}
	
	
}
