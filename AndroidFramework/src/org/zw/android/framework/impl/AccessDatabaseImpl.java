package org.zw.android.framework.impl;

import java.util.List;

import org.zw.android.framework.IAccessDatabase;
import org.zw.android.framework.db.core.ObjectReflectUtil;
import org.zw.android.framework.db.core.PropertySQL;
import org.zw.android.framework.db.core.PropertySchema;
import org.zw.android.framework.db.core.SQLiteExecutor;
import org.zw.android.framework.db.core.SQLiteExecutorImpl;
import org.zw.android.framework.log.Debug;
import org.zw.android.framework.util.StringUtils;

import android.content.Context;
import android.database.Cursor;

/**
 * Database implements
 * 
 * @author zhouwei
 *
 */
public final class AccessDatabaseImpl implements IAccessDatabase {
	
	private static final String TAG = "AccessDatabaseImpl" ;
	private static final int DISPOSE_SAVE		= 1 ;
	private static final int DISPOSE_UPDATE		= 2 ;
	private static final int DISPOSE_DELETE		= 3 ;
	
	private Context 		mContext ;
	private SQLiteExecutor 	mExecutor ;
	private final Object    mLock = new Object() ;
	
	protected AccessDatabaseImpl(Context context,String name,int dbVersion){
		
		mContext	= context ;
		
		String dbName = name != null ? name : context.getPackageName() ;
		
		mExecutor	= SQLiteExecutorImpl.createExcutor(mContext, dbName, dbVersion <= 0 ? 0 : dbVersion) ;
		
		Debug.d(TAG, mContext.getPackageName()) ;
	}

	@Override
	public String getDatabasePath() {
		return mExecutor.getDatabasePath();
	}

	@Override
	public String createTable(Class<?> objClass) {
		return createTableIfNotExist(objClass);
	}

	@Override
	public boolean deleteTable(Class<?> objClass) {
		
		final PropertySchema schema = PropertySchema.create(objClass,true);
		
		if(schema == null){
			
			Debug.e(TAG, "deleteTable Create PropertySchema error");
			
			return false ;
		}
		
		String sql = schema.deleteTable() ;
		
		return mExecutor.execute(sql) ;
	}

	@Override
	public boolean deleteAll(Class<?> objClass) {
		
		final PropertySchema schema = PropertySchema.create(objClass,true);
		
		if(schema == null){
			
			Debug.e(TAG, "deleteTable Create PropertySchema error");
			
			return false ;
		}
		
		String sql = schema.deleteAll() ;
		
		return mExecutor.execute(sql) ;
	}

	@Override
	public boolean updateTable(Class<?> objClass) {
		
		if(objClass == null){
			
			Debug.e(TAG, "updateTable Class is null");
			
			return false ;
		}
		
		final SQLiteExecutor 	executor 	= mExecutor ;
		
		// http://www.cnblogs.com/08shiyan/archive/2011/04/29/2032988.html
		// synchronized
		synchronized (mLock) {
			
			// start
			executor.beginTransaction() ;
			
			try{
				
				final PropertySchema schema = PropertySchema.create(objClass,true);
				
				if(schema == null){
					
					Debug.e(TAG, "saveObject Create PropertySchema error");
					
					return false ;
				}
				
				String tableName = schema.getTable() ;
				String tempTable = "__temp__" + tableName ;
				
				// rename to temp table
				executor.execute("ALTER TABLE " + tableName + "  RENAME TO " + tempTable) ;
				
				// delete old table
				executor.execute(schema.deleteTable()) ;
				
				// create new table
				executor.execute(schema.createTable());
				
				// select * from temp table
				List<?> list = queryObjects("SELECT * FROM " + tempTable, objClass) ;
				
				// save list
				for(Object bean : list){
					insert(bean,schema);
				}
				
				// import data
				//executor.execute("INSERT INTO " + tableName + " SELECT ") ;
				
				// delete temp table
				executor.execute("DROP TABLE " + tempTable) ;
				
				// commit
				executor.endTransaction() ;
				
				return true ;
			}catch(Exception e){
				e.printStackTrace() ;
			}
		}
		
		return false ;
	}
	
	private String createTableIfNotExist(Class<?> cls){
		
		// 1. check object is table
		String table = ObjectReflectUtil.getTableNameByClass(cls) ;
		
		if(StringUtils.isEmpty(table)){
			
			Debug.e(TAG, "createTableIfNotExist() The Object is not table");
			
			return null ;
		}
		
		// synchronized
		synchronized (mLock) {
			
			final SQLiteExecutor 	executor 	= mExecutor ;
			
			final PropertySchema schema = PropertySchema.create(cls,true);
			
			if(schema == null){
				
				Debug.e(TAG, "createTableIfNotExist Create PropertySchema error");
				
				return null ;
			}
			
			// 2. table is exist
			boolean exist = executor.checkTableExist(table) ;
			
			// not exist
			if(!exist){
				
				boolean create = executor.execute(schema.createTable());
				
				return create ? table : null ;
			} 
		}
		
		return table ;
	}
	
	private String loadClassTable(Class<?> cls){
		
		// 1. check object
		if (cls == null) {
			
			Debug.e(TAG, "loadClassTable() obj == null");
			
			return null;
		}
		
		String table = createTableIfNotExist(cls) ;

		// 2. table
		if(table == null){
			
			Debug.e(TAG, "loadClassTable() Check Object to Table failed");
			
			return null ;
		}
		
		return table ;
	}
	
	@Override
	public boolean beginTransaction() {
		return mExecutor.beginTransaction();
	}

	@Override
	public boolean endTransaction() {
		return mExecutor.endTransaction();
	}

	@Override
	public int saveObject(Object obj) {
		
		if(obj == null){
			return -1 ;
		}
		
		// 1. check Object table and Property
		
		String table = loadClassTable(obj.getClass()) ;
		
		if(table == null){
			
			Debug.e(TAG, "saveObject Check Object to Table failed");
			
			return -1 ;
		}
		
		final PropertySchema schema = PropertySchema.create(obj.getClass(),true);
		
		if(schema == null){
			
			Debug.e(TAG, "saveObject Create PropertySchema error");
			
			return -1 ;
		}
		
		// insert to table
		return insert(obj,schema);
	}
	
	private int insert(Object obj,PropertySchema schema){
		
		final SQLiteExecutor 	executor 	= mExecutor ;
		
		// 1. create sql
		PropertySQL sql 	= schema.insertObject(obj) ;
		
		if(sql == null){
			Debug.e(TAG, "Error : insert() PropertySQL is null");
			return -1 ;
		}
		
		// 2. execute insert SQL
		int row = executor.insert(sql.getTable(),sql.getNullColumnHack(),sql.getContentValues()) ;
		
		if(row <= 0){
			
			Debug.e(TAG, "insert Object error");
			
			return -1;
		}
		
		// 3. fill primary key
		boolean success = fillPrimaryKey(schema,obj);
		
		if(!success){
			
			Debug.e(TAG, "insert fillPrimaryKey failed");
		}
		
		return row ;
	}
	
	@Override
	public int saveOrUpdataObject(Object obj) {
		
		if(obj == null){
			return -1 ;
		}
		
		// 1. check Object table and Property
		String table = loadClassTable(obj.getClass()) ;
		
		if(table == null){
			
			Debug.e(TAG, "saveOrUpdataObject Check Object to Table failed");
			
			return -1 ;
		}
		
		final PropertySchema schema = PropertySchema.create(obj.getClass(),true);
		
		if(schema == null){
			
			Debug.e(TAG, "saveOrUpdataObject Create PropertySchema error");
			
			return -1 ;
		}
		
		PropertySQL sql = schema.queryWithPrimaryKey(obj); 
		
		if(sql == null){
			
			Debug.e(TAG, "saveOrUpdataObject PropertySQL is null");
			
			return -1;
		}
		
		Object bean = queryObject(sql.getSql(), sql.getParams(), obj.getClass()) ;
		
		if(bean == null){
			return insert(obj, schema) ;
		} else {
			return update(obj,schema);
		}
	}

	@Override
	public int saveObjectList(List<?> list) {
		return dispostList(list,DISPOSE_SAVE);
	}

	@Override
	public int updateObjectList(List<?> list) {
		return dispostList(list,DISPOSE_UPDATE);
	}

	@Override
	public int deleteObjectList(List<?> list) {
		return dispostList(list,DISPOSE_DELETE);
	}
	
	private int dispostList(List<?> list,int type){
		
		if(list == null || list.isEmpty()){
			return 0 ;
		}
		
		final SQLiteExecutor 	executor 	= mExecutor ;
		
		Class<?> cls = list.get(0).getClass() ;
		
		// 1. check Object table and Property
		final String table = loadClassTable(cls);

		if (table == null) {

			Debug.e(TAG, "dispostList Check Object to Table failed");

			return -1;
		}
		
		final PropertySchema schema = PropertySchema.create(cls,true);
		
		if(schema == null){
			
			Debug.e(TAG, "dispostList Create PropertySchema error");
			
			return -1 ;
		}
		
		try{
			
			// begin Transaction
			executor.beginTransaction() ;
			
			int sum		= 0 ;
			
			for(Object bean : list){
				
				// save object
				if(type == DISPOSE_SAVE){
					sum += insert(bean,schema);
				} else if(type == DISPOSE_UPDATE){
					sum += update(bean,schema);
				} else if(type == DISPOSE_DELETE){
					sum += delete(bean,schema);
				}
			}
			
			// commit
			executor.setTransactionSuccessful() ;
			
			return sum ;
		} catch(Exception e){
			e.printStackTrace() ;
			executor.endTransaction() ;
		}
		
		return 0 ;
	}

	@Override
	public int updateObject(Object obj) {
		
		if(obj == null){
			return 0 ;
		}
		
		String table = loadClassTable(obj.getClass()) ;
		
		if(table == null){
			
			Debug.e(TAG, "updateObject Check Object to Table failed");
			
			return 0 ;
		}
		
		final PropertySchema schema = PropertySchema.create(obj.getClass(),true);
		
		if(schema == null){
			
			Debug.e(TAG, "updateObject Create PropertySchema error");
			
			return 0 ;
		}
		
		return update(obj,schema) ;
	}
	
	private int update(Object obj,PropertySchema schema){
		
		PropertySQL sql = schema.updateObject(obj);
		
		if(sql == null){
			
			Debug.e(TAG, "update Create PropertySQL error");
			
			return 0 ;
		}
		
		final SQLiteExecutor 	executor 	= mExecutor ;
		
		return executor.update(sql.getTable(),sql.getContentValues(),sql.getWhere(),sql.getParams()) ;
	}

	@Override
	public int deleteObject(Object obj) {
		
		if(obj == null){
			return 0 ;
		}
		
		String table = loadClassTable(obj.getClass()) ;
		
		if(table == null){
			
			Debug.e(TAG, "deleteObject Check Object to Table failed");
			
			return 0 ;
		}
		
		final PropertySchema schema = PropertySchema.create(obj.getClass(),true);
		
		if(schema == null){
			
			Debug.e(TAG, "deleteObject Create PropertySchema error");
			
			return 0 ;
		}
		
		return delete(obj,schema);
	}
	
	private int delete(Object obj,PropertySchema schema) {
		
		final SQLiteExecutor 	executor 	= mExecutor ;
		
		final PropertySQL sql = schema.queryWithPrimaryKey(obj);
		
		return executor.delete(sql.getTable(), sql.getWhere(), sql.getParams());
	}

	@Override
	public <T> T queryObject(String sql, String[] params, Class<T> objClass) {
		
		List<T> list = queryObjects(sql,params,objClass);
		
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	@Override
	public <T> List<T> queryObjects(String sql, Class<T> objClass) {
		return queryObjects(sql, null, objClass);
	}

	@Override
	public <T> List<T> queryObjects(String sql, String[] params,Class<T> objClass) {
		
		final SQLiteExecutor executor 	= mExecutor ;
		
		return executor.query(sql, params, objClass);
	}
	
	private boolean fillPrimaryKey(PropertySchema schema,Object bean){
		
		final SQLiteExecutor executor 	= mExecutor ;
		
		// create Property SQL
		final PropertySQL sql 	= schema.queryOutsidePrimaryKey(bean) ;
		
		if(sql == null){
			return false ;
		}
		
		// select from table
		final List<?> list = executor.query(sql.getSql(), sql.getParams(), bean.getClass());
		
		if(list != null && !list.isEmpty()){
			
			Object src = list.get(list.size() - 1) ;
			
			return PropertySchema.copyPrimaryKey(bean, src); 
		}
		
		return false ;
	}

	@Override
	public void execute(String sql, String[] params) {
		
		final SQLiteExecutor executor 	= mExecutor ;
		
		executor.execute(sql, params) ;
	}
	

	@Override
	public Cursor executeQuerySQL(String sql, String[] params) {

		final SQLiteExecutor executor 	= mExecutor ;
		
		return executor.executeQuerySQL(sql, params) ;
	}

	@Override
	public boolean removeDatabse() {
		
		final SQLiteExecutor executor 	= mExecutor ;
		
		executor.deleteDatabse();
		
		return true ;
	}
}
