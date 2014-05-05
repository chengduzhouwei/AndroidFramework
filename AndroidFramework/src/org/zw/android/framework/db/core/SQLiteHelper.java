package org.zw.android.framework.db.core;

import org.zw.android.framework.log.Debug;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Android SQlite database Helper
 * 
 * @author wei.zhou
 *
 */
public final class SQLiteHelper{

    /** for debug*/
    private static final String TAG = "SQLiteHelper";

    /** context for android application*/
    private Context mContext;

    /** database name*/
    private String mDatabaseName;

    /** database*/
    private DataBaseHelper mSQLiteDatabaseHelper;

    /** read database*/
    private SQLiteDatabase mSQLiteDatabaseRead;

    /** write database*/
    private SQLiteDatabase mSQLiteDatabaseWrite;

    /** inner class. database helper*/
    private class DataBaseHelper extends SQLiteOpenHelper {
    	
    	private SQLiteUpdateListener listener ;

        public DataBaseHelper(int version,SQLiteUpdateListener l) {
            super(mContext, mDatabaseName, null, version <= 0 ? 1 : version);
            listener = l ;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	
        	Debug.d(TAG, "Database from " + oldVersion + " update to " + newVersion);
        	
        	if(listener != null){
        		listener.updateDatabase(oldVersion, newVersion);
        	}
        }
    }

    /**
     * private constructor
     * @param context
     * @param databaseName
     */
    protected SQLiteHelper(Context context, String databaseName,int version,SQLiteUpdateListener listener) {
    	
    	Debug.d(TAG, "Create DatabaseHelper : database name=" + databaseName);
    	
        mContext 				= context;
        mDatabaseName 			= databaseName;
        mSQLiteDatabaseHelper 	= new DataBaseHelper(version,listener);
        mSQLiteDatabaseRead 	= mSQLiteDatabaseHelper.getReadableDatabase();
        mSQLiteDatabaseWrite 	= mSQLiteDatabaseHelper.getWritableDatabase();
    }
    
    public String getDatabasePath(){
    	return mContext.getDatabasePath(mDatabaseName).getAbsolutePath() ;
    }
    
    public SQLiteDatabase getReadDatabase() {
        return mSQLiteDatabaseRead;
    }

    public SQLiteDatabase getWriteDatabase() {
        return mSQLiteDatabaseWrite;
    }

    public void deleteDatabse() {
    	
    	// close
    	mSQLiteDatabaseRead.close() ;
    	mSQLiteDatabaseWrite.close() ;
    	
    	// delete file
        mContext.deleteDatabase(mDatabaseName);
    }
}
