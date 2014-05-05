package org.zw.android.framework.db.core;


/**
 * Database update listener
 * 
 * @author zhouwei
 *
 */
public interface SQLiteUpdateListener {

	public void updateDatabase(int oldVersion, int newVersion) ;
}
