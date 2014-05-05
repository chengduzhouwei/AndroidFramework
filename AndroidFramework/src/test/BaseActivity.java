package test;

import org.zw.android.framework.IFrameworkFacade;
import org.zw.android.framework.ioc.InjectCore;

import test.service.AppHanlder;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

	protected IFrameworkFacade 	mFramework ;
	protected AppHanlder		mHandler ;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TestApplication app = (TestApplication)getApplication() ;
		mFramework = app.getFrameworkFacade() ;
		
		// ioc 解析UI
		InjectCore.injectUIProperty(this);
		
		// handler
		mHandler	= getAppHanlder() ;
	}
	
	protected AppHanlder getAppHanlder(){
		return new AppHanlder(this);
	}
}
