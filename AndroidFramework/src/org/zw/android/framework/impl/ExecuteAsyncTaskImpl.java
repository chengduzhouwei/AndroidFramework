package org.zw.android.framework.impl;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.zw.android.framework.IExecuteAsyncTask;

import android.util.Log;

/**
 * 
 * @author zhouwei
 *
 */
public final class ExecuteAsyncTaskImpl implements IExecuteAsyncTask {
	
	static final String TAG = "ExecuteAsyncTaskImpl" ;
	
	/** wait queue */
	private static final ArrayList<Worker> mQueue = new ArrayList<Worker>() ;
	
	/** core thread pool */
	private static final int CORE_POOL_SIZE = 3 ;

	/** thread factory */
	private final ThreadFactory mThreadFactory = new ThreadFactory() {
		//
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			final Thread thread = new Thread(r, "#Async Task Thread" + mCount.getAndIncrement());
			thread.setDaemon(true);// set the thread is daemon thread
			return thread;
		}
	};
	
	/** server thread pool instance */
	private ExecutorService mExecutorService;
	private ExecutorService mSingleService;
	
	private static IExecuteAsyncTask _instance ;
	
	private ExecuteAsyncTaskImpl() {
		mQueue.clear() ;
	}
	
	/** default async execute */
	public static IExecuteAsyncTask defaultSyncExecutor(){
		
		if(_instance == null){
			_instance = new ExecuteAsyncTaskImpl() ;
		}
		
		return _instance ;
	}

	@Override
	public Worker executeTask(IAsyncTask task) {
		
		if(task == null){
			Log.e(TAG, "Task is null") ;
			return null ;
		}
		
		final Worker worker 			= new Worker(this,task) ;
		final ArrayList<Worker> queue 	= mQueue;
		
		// if thread pool is null
    	if(mExecutorService == null){
    		mExecutorService = Executors.newFixedThreadPool(CORE_POOL_SIZE, mThreadFactory);;
    	}
    	
    	final ExecutorService	executor = mExecutorService ;
    	
    	// put worker in queue
		synchronized (queue) {
			queue.add(worker);
		}
		
		// submit worker
		executor.execute(worker);
		
		return worker ;
	}
	
	@Override
	public Worker executeTaskInNewThread(IAsyncTask task) {
		
		if(task == null){
			Log.e(TAG, "Task is null") ;
			return null ;
		}
		
		final Worker worker 			= new Worker(this,task) ;
		final ArrayList<Worker> queue 	= mQueue;
		
		// put worker in queue
		synchronized (queue) {
			queue.add(worker);
		}
		
		Thread t = new Thread(worker) ;
		t.setDaemon(true);
		t.setName("#Async Task for New Thread");
		t.start() ;
		
		return worker;
	}

	@Override
	public Worker executeSingleTask(IAsyncTask task) {
		
		if(task == null){
			Log.e(TAG, "Task is null") ;
			return null ;
		}
		
		final Worker worker 			= new Worker(this,task) ;
		final ArrayList<Worker> queue 	= mQueue;
		
		// if thread pool is null
		if(mSingleService == null){
    		mSingleService = Executors.newSingleThreadExecutor();
    	}
    	
    	final ExecutorService	executor = mSingleService ;
    	
    	// put worker in queue
		synchronized (queue) {
			queue.add(worker) ;
		}
		
		// submit worker
		executor.execute(worker);
		
		return worker ;
	}

	@Override
	public void shutdown() {
		
		if(mExecutorService != null) {
    		mExecutorService.shutdownNow() ;
    		mExecutorService = null ;
    	}
    	
    	if(mSingleService != null){
    		mSingleService.shutdownNow() ;
    		mSingleService	= null ;
    	}
    	
    	cancelAll() ;
	}

	@Override
	public void cancelAll() {
		
		final ArrayList<Worker> queue = mQueue;
		
		synchronized (queue) {
			
			for(Worker worker : queue){
				worker.onCancelled() ;
			}
			
			queue.clear() ;
		}
	}
	
	protected void removeWorker(Worker worker){
		
		if(worker == null){
			return ;
		}
		
		synchronized (mQueue) {
			mQueue.remove(worker) ;
		}
	}

}
