package org.zw.android.framework.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.zw.android.framework.IExecuteAsyncTask.IAsyncTask;
import org.zw.android.framework.log.Debug;

/**
 * Sync task 
 * @author zhouwei
 *
 */
public final class Worker implements Runnable {
	
	private static final String TAG = "Worker" ;
	
	private final AtomicBoolean mCancelled = new AtomicBoolean();
	private IAsyncTask 		task ;
	private ExecuteAsyncTaskImpl executor ;
	
	protected Worker(ExecuteAsyncTaskImpl executor,IAsyncTask task){
		this.task 		= task ;
		this.executor	= executor ;
	}
	
	public void onCancelled(){
		mCancelled.set(true);
	}
	
	public boolean isCancelled(){
		return mCancelled.get() ;
	}
	
	private void onException(Throwable exception){
		
		if(exception != null){
			exception.printStackTrace() ;
		}
		
		task.onException() ;
	}
	
	private void onCancel(){
		task.onCancel() ;
	}
	
	private void removeWorker(){
		// remove from queue
		executor.removeWorker(this);
	}

	@Override
	public void run() {
		
		boolean stop = false ;
		Object value = null ;
		
		final IAsyncTask processe = task ;
		
		if(processe != null){
			
			try{
				
				if(isCancelled()){
					onCancel() ;
					Debug.d(TAG, "1. Worker Task cancelled ");
					return ;
				}
				
				Debug.d(TAG, "Worker Task start ");
				
				// start processing
				stop = processe.onStart() ;
				
				if(!stop){ 
					Debug.w(TAG, "Worker Task exit");
					return ;
				}
				
				if(isCancelled()){
					onCancel() ;
					Debug.d(TAG, "2. Worker Task cancelled ");
					return ;
				}
				
				// processing
				value = processe.onProcessing();
				
				if(isCancelled()){
					onCancel() ;
					Debug.d(TAG, "3. Worker Task cancelled ");
					return ;
				}
				
				// end
				processe.onFinish(value) ;
				
				Debug.d(TAG, "Worker Task finish ");
				
			} catch(Exception e){
				onException(e);
			} finally {
				removeWorker() ;
			}
		}
	}
}
