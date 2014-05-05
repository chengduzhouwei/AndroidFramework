package org.zw.android.framework.impl;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.IBitmapDownloader;
import org.zw.android.framework.IExecuteAsyncTask;
import org.zw.android.framework.IFrameworkFacade;
import org.zw.android.framework.ILocationProxy;
import org.zw.android.framework.ioc.InjectCore;

import android.content.Context;

/**
 * Android Service Common Facade
 * 
 * @author zhouwei
 *
 */
public final class FrameworkFacade implements IFrameworkFacade {
	
	private static FrameworkFacade _instance ;
	
	private FrameworkConfig				mConfig ;
	private IBitmapDownloader 			mDownloader ;
	private IExecuteAsyncTask		 	mExecutor ;
	private Context 		  			mContext ;
	private ILocationProxy	  			mLocation ;
	
	private String mDefaultDbName;
	
	private FrameworkFacade(FrameworkConfig config){
		
		if(config == null){
			throw new RuntimeException("Framework Facade config is null");
		}
		
		mConfig		= config ;
		mContext 	= config.getContext() ;
		
		if(mContext == null){
			throw new RuntimeException("FrameworkFacade Context is null");
		}
		
		// image cache
		mDownloader		= new BitmapDownloaderImpl(mContext,
									config.getCacheName(),
									config.getCachePercent(),
									config.getMaxWidth(),
									config.getMaxHeight());
		
		// default database name
		mDefaultDbName = config.getDatabaseName() != null ? config.getDatabaseName() : mContext.getPackageName() ;
		
		// thread executor
		mExecutor		= ExecuteAsyncTaskImpl.defaultSyncExecutor()  ;
		
		// IOC Core class
		InjectCore.initInjectCore(mContext);
	}
	
	/** Create Framework instance by Context */
	public static IFrameworkFacade create(Context context){
		return create(FrameworkConfig.defaultConfig(context)) ;
	}
	
	/** Create Framework instance by Framework Config */
	public static IFrameworkFacade create(FrameworkConfig config){
		
		if(_instance == null){
			_instance = new FrameworkFacade(config);
		}
		
		return _instance ;
	}
	
	/** get Framework instance. Note : Please call create() before this method*/
	public static IFrameworkFacade getFrameworkFacade(){
		return _instance ;
	}
	
	@Override
	public IAccessDatabase openDefaultDatabase() {
		return openDatabaseByName(mDefaultDbName,mConfig.getDatabaseVersion());
	}

	@Override
	public IAccessDatabase openDatabaseByName(String dbName, int version) {

		final DatabaseFractory fc = DatabaseFractory.defaultFactory();

		return fc.openDatabase(mContext, dbName, version);
	}

	@Override
	public void removeDefaultDatabase() {
		removeDatabaseByName(mDefaultDbName);
	}

	@Override
	public void removeDatabaseByName(String dbName) {
		
		final DatabaseFractory fc = DatabaseFractory.defaultFactory();
		
		fc.removeDatabase(dbName);
	}

	@Override
	public Context getContext() {
		return mContext;
	}
	
	@Override
	public IBitmapDownloader getBitmapDownloader() {
		return mDownloader ;
	}

	@Override
	public IExecuteAsyncTask getAsyncExecutor() {
		return mExecutor;
	}

	@Override
	public ILocationProxy getLocationProxy() {
		
		if(mLocation == null){
			mLocation = new LocationProxyImpl(mContext);
		}
		
		return mLocation ;
	}
}
