package test;

import org.zw.android.framework.IFrameworkFacade;
import org.zw.android.framework.impl.FrameworkConfig;
import org.zw.android.framework.impl.FrameworkFacade;

import test.service.WebServiceImpl;

import android.app.Application;

public final class TestApplication extends Application {
	
	private IFrameworkFacade framework ;

	@Override
	public void onCreate() {
		super.onCreate();
		
		// 初始化框架
		FrameworkConfig config = FrameworkConfig.defaultConfig(this) ;
		framework = FrameworkFacade.create(config);
		
		// 初始化业务
		WebServiceImpl.initWebService() ;
	}
	
	public IFrameworkFacade getFrameworkFacade(){
		return framework ;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

}
