package org.zw.android.framework.db.core;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

public interface SQLiteExecutor {
	
	public void deleteDatabse();
	
	public String getDatabasePath();
	
	public boolean checkTableExist(String table) ;
	
	public boolean beginTransaction();
	
	public boolean setTransactionSuccessful() ;
	
	public boolean endTransaction();
	
	public boolean execute(String sql) ;
	
	public boolean execute(String sql,String[] params) ;
	
	public Cursor executeQuerySQL(String sql, String[] params) ;

	public int insert(String table,String nullColumnHack,ContentValues values) ;
	
	public int update(String table,ContentValues values,String where,String[] params) ;
	
	public int delete(String table, String where,String[] params) ;
	
	public <T> List<T> query(String sql,String[] params,Class<T> objClass) ;
}
