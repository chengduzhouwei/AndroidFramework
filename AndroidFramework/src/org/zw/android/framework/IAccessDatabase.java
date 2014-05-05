package org.zw.android.framework;

import java.util.List;

import android.database.Cursor;

/**
 * Access Database 
 * 
 * @author zhouwei
 *
 */
public interface IAccessDatabase {
	
	// close database
	public boolean removeDatabse();
	
	// get database path
	public String getDatabasePath();
	
	// table
	public String createTable(Class<?> objClass) ;
	
	public boolean deleteTable(Class<?> objClass);
	
	public boolean updateTable(Class<?> objClass);
	
	// object
	public boolean deleteAll(Class<?> objClass) ;
	
	// execute signal sql
	public void execute(String sql,String[]params);
	
	// execute signal sql
	public Cursor executeQuerySQL(String sql,String[]params);
	
	// transaction
	public boolean beginTransaction();
	
	public boolean endTransaction();
	
	// single object
	public int saveObject(Object obj) ;
	
	public int saveOrUpdataObject(Object obj) ;
	
	public int updateObject(Object obj);
	
	public int deleteObject(Object obj);
	
	// more object
	public int saveObjectList(List<?> list) ;
	
	public int updateObjectList(List<?> list) ;
	
	public int deleteObjectList(List<?> list) ;
	
	// query
	public <T> T queryObject(String sql,String[] params,Class<T> objClass);
	
	public <T> List<T> queryObjects(String sql,Class<T> objClass);
	
	public <T> List<T> queryObjects(String sql,String[] params,Class<T> objClass);
}
