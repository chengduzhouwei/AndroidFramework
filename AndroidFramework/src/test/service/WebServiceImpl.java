package test.service;

import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;

import test.User;

public class WebServiceImpl {

	private static WebServiceImpl _instance ;
	
	private HttpExecutor 	mHttpExecutor ;
	private ResultObject	mResultObject ;
	
	private WebServiceImpl(){
		mHttpExecutor = new HttpExecutor() ;
		mResultObject = new ResultObject() ;
	}
	
	public static void initWebService(){
		
		if(_instance == null){
			_instance	= new WebServiceImpl() ;
		}
	}
	
	public static WebServiceImpl getWebService(){
		return _instance ;
	}
	
	public void login(String username,String password,AppHanlder handler){
		
		// 异步任务
		ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(new BaseTask(handler) {
			
			@Override
			public void processing() {
				
				// 网络操作
				boolean error = false ;
				ResultObject result = mResultObject.clone() ;
				
				String url = "http://www.baidu.com";
				
				error = mHttpExecutor.doGet(url, result);
				
				// 解析对象
				User user = null ;
				
				if(!error){
					sendMessage(MSG_TASK_LOGIN_SUCCESS,user) ;
				} else {
					sendMessage(MSG_TASK_LOGIN_FAILED,result) ;
				}
				
			}
		}) ;
		
	}
}
