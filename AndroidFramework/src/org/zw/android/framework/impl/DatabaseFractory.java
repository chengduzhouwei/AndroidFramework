package org.zw.android.framework.impl;

import java.util.HashMap;
import java.util.Map;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.log.Debug;
import org.zw.android.framework.util.StringUtils;

import android.content.Context;

/**
 * Database Factory
 * 
 * @author zhouwei
 *
 */
public final class DatabaseFractory {
	
	static String TAG = "DatabaseFractory" ;
	
	private static DatabaseFractory _instance ;
	
	private Map<String, IAccessDatabase> DbCache = new HashMap<String, IAccessDatabase>();
	
	private DatabaseFractory(){
		DbCache.clear() ;
	}
	
	public static DatabaseFractory defaultFactory(){
		
		if(_instance == null){
			_instance	= new DatabaseFractory() ;
		}
		
		return _instance ;
	}
	
	public synchronized IAccessDatabase openDatabase(Context context,String dbName,int version){
		
		if(context == null || StringUtils.isEmpty(dbName)){
			
			Debug.e(TAG, "database name is null");
			
			return null ;
		}
		
		IAccessDatabase db = DbCache.get(dbName);
		
		if(db == null){
			
			db	= new AccessDatabaseImpl(context,dbName,version);
			
			DbCache.put(dbName, db);
		}
		
		return db ;
	}
	
	public synchronized void removeDatabase(String dbName){
		
		IAccessDatabase db = DbCache.get(dbName);
		
		if(db != null){
			
			DbCache.remove(dbName);
			
			db.removeDatabse() ;
		}
	}
}
