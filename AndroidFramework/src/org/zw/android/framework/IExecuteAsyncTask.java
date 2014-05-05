package org.zw.android.framework;

import org.zw.android.framework.impl.Worker;



/**
 * Execute Async Task
 * 
 * @author zhouwei
 *
 */
public interface IExecuteAsyncTask {
	
	/** Async Task*/
	public static abstract class IAsyncTask {
		
		/** start */
		public boolean onStart() {
			return true ;
		}
		
		/** process */
		public abstract Object onProcessing() ;
		
		/** finish */
		public void onFinish(Object value){
			
		}
		
		/** exception */
		public void onException(){
			
		}
		
		/** cancel */
		public void onCancel(){
			
		}
	}
	
	public Worker executeTask(IAsyncTask task);
	
	public Worker executeTaskInNewThread(IAsyncTask task) ;
	
	public Worker executeSingleTask(IAsyncTask task);
	
	public void cancelAll() ;
	
	public void shutdown() ;
}
