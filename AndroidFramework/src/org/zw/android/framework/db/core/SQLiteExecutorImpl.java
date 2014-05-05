package org.zw.android.framework.db.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zw.android.framework.db.ColumnBinary;
import org.zw.android.framework.db.ColumnBoolean;
import org.zw.android.framework.db.ColumnByte;
import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnDouble;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnLong;
import org.zw.android.framework.db.ColumnShort;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.ColumnText;
import org.zw.android.framework.db.ColumnTimeStamp;
import org.zw.android.framework.util.DateUtils;
import org.zw.android.framework.util.StringUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public final class SQLiteExecutorImpl implements SQLiteExecutor {
	
	private final String SQL_Check_Table = "SELECT name FROM sqlite_master WHERE type='table' AND name= ? " ;
	
	private SQLiteHelper helper ;
	
	private SQLiteExecutorImpl(Context context, String dbName,int version){
		
		if(context == null) {
    		throw new RuntimeException("ERROR: Create DatabaseHelper context is null") ;
    	}
		
		helper	= new SQLiteHelper(context, dbName, version, null) ;
	}
	
	@Override
	public String getDatabasePath() {
		return helper.getDatabasePath();
	}

	public static SQLiteExecutor createExcutor(Context context, String dbName,int version){
		return new SQLiteExecutorImpl(context, dbName, version) ;
	}

	@Override
	public boolean execute(String sql) {
		return execute(sql,new String[]{});
	}

	@Override
	public boolean beginTransaction() {
		
		try{
    		
    		final SQLiteDatabase db = helper.getWriteDatabase() ; 
			
    		db.beginTransaction() ;
    		
    		db.setTransactionSuccessful() ;
    		
			return true ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return false;
	}

	@Override
	public boolean endTransaction() {
		
		try{
    		
    		final SQLiteDatabase db = helper.getWriteDatabase() ; 
			
    		db.endTransaction() ;
    		
			return true ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return false;
	}

	@Override
	public boolean setTransactionSuccessful() {
		
		try{
    		
    		final SQLiteDatabase db = helper.getWriteDatabase() ; 
			
    		db.setTransactionSuccessful() ;
    		
			return true ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return false;
	}

	@Override
	public boolean execute(String sql, String[] params) {
    	
    	if(sql == null || params == null){
			return false ;
		}
    	
    	try{
    		
    		final SQLiteDatabase db = helper.getWriteDatabase() ; 
			
			db.execSQL(sql, params) ;
			
			return true ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
    	
		return false;
	}

	@Override
	public Cursor executeQuerySQL(String sql, String[] params) {
		
		if(sql == null){
			return null ;
		}
		
		final SQLiteDatabase db = helper.getReadDatabase() ;
		
		return db.rawQuery(sql, params);
	}

	@Override
    public int insert(String table, String nullColumnHack,ContentValues values){
		
		if(StringUtils.isEmpty(table)
				|| values == null ){
			return -1 ;
		}
		
		try{
    		
    		final SQLiteDatabase db 	= helper.getWriteDatabase() ; 
    		
    		return (int) db.insert(table, nullColumnHack, values) ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return -1 ;
	}
	
    @Override
	public int delete(String table, String where,String[] params){
    	
    	if(StringUtils.isEmpty(table)
				|| StringUtils.isEmpty(where) 
				|| params == null){
			return 0 ;
		}
		
		try{
    		
    		final SQLiteDatabase db 	= helper.getWriteDatabase() ; 
    		
    		return (int) db.delete(table, where, params) ;
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return 0 ;
	}
    
	@Override
	public <T> List<T> query(String sql,String[] params,Class<T> objClass) {
		
		if(StringUtils.isEmpty(sql) || objClass == null){
			return null ;
		}
		
		Cursor cursor = null ;
		
		try{
			
			final PropertySchema schema = PropertySchema.create(objClass,false);
			
			if(schema == null){
				return null ;
			}
			
			String className = objClass.getName() ;
			
			final SQLiteDatabase db 	= helper.getReadDatabase() ;
			cursor		 				= db.rawQuery(sql, params);
			List<PropertyField> fields 	= schema.getPropertyFields() ;
			
			if(cursor != null){
				
				List<T> list = new ArrayList<T>() ;
				
				while(cursor.moveToNext()){
					
					T t = mapping(cursor,fields,className);
					
					if(t != null){
						list.add(t) ;
					}
				}
				
				return list ;
			}
		} catch(Exception e){
			e.printStackTrace() ;
		} finally{
			
			if(cursor != null){
				cursor.close() ;
			}
			
			cursor	= null ;
		}
		
		return null ;
	}

	@Override
	public int update(String table,ContentValues values,String where,String[] params){
		
		if(StringUtils.isEmpty(table)
				|| values == null ){
			return 0 ;
		}
		
		try{
    		
    		final SQLiteDatabase db 	= helper.getWriteDatabase() ; 
    		
    		return (int) db.update(table, values, where, params) ;
    		
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return 0 ;
	}

	@Override
	public boolean checkTableExist(String table) {
		
		if(StringUtils.isEmpty(table)){
			return false ;
		}
		
		Cursor cursor = null ;
		
		try{
			
			final SQLiteDatabase db = helper.getReadDatabase() ;
			
			cursor = db.rawQuery(SQL_Check_Table, new String[] { table });
			
			if (cursor != null) {

				while (cursor.moveToNext()) {
					return true ;
				}
			}
			
		}catch(Exception e){
			e.printStackTrace() ;
		} finally{
			
			if(cursor != null){
				cursor.close() ;
			}
			
			cursor	= null ;
		}
		
		return false;
	}

	@SuppressWarnings("unchecked")
	private static <T> T mapping(Cursor cursor, List<PropertyField> propertys ,String className) {
		
		if(cursor == null 
				|| StringUtils.isEmpty(className) 
				|| propertys == null){
			return null ;
		}
		
		Object obj = null ;
		
		try{
			
			obj = Class.forName(className).newInstance();
			
			if(obj != null){
				
				for(PropertyField field : propertys){
					
					String name 		= field.getPropertyName() ;
					Class<?> type 		= field.getPropertyType() ;
					Object	value		= null ;
					Class<?> outType	= null ;
					
					if(StringUtils.isEmpty(name) || type == null){
						continue ;
					}
					
					int columnIndex = cursor.getColumnIndex(name) ;
					
					if(columnIndex < 0){
						continue ;
					}
					
					if (type == ColumnByte.class) {
						outType	= byte.class ;
						value 	= Integer.valueOf(cursor.getShort(columnIndex)).byteValue() ;
					} else if (type == ColumnShort.class) {
						outType	= short.class ;
						value 	= Integer.valueOf(cursor.getShort(columnIndex)).shortValue() ;
					} else if (type == ColumnInt.class) {
						outType	= int.class ;
						value 	= Integer.valueOf(cursor.getInt(columnIndex)).intValue() ;
					} else if (type == ColumnLong.class) {
						outType	= long.class ;
						value 	= Long.valueOf(cursor.getLong(columnIndex)).longValue();
					} else if (type == ColumnFloat.class) {
						outType	= float.class ;
						value 	= Float.valueOf(cursor.getFloat(columnIndex)).floatValue();
					} else if (type == ColumnDouble.class) {
						outType	= double.class ;
						value 	= Double.valueOf(cursor.getDouble(columnIndex)).doubleValue();
					} else if (type == ColumnBoolean.class) {
						outType	= boolean.class ;
						
						String v = cursor.getString(columnIndex) ;
						
						if(v.equals("1")){
							value	= true ;
						} else if(v.equals("0")){
							value	= false ;
						} else {
							value 	= Boolean.valueOf(v).booleanValue();
						}
					} else if (type == ColumnString.class) {
						outType	= String.class ;
						value 	= cursor.getString(columnIndex) ;
					} else if (type == ColumnText.class) {
						outType	= String.class ;
						value 	= cursor.getString(columnIndex) ;
					} else if (type == ColumnBinary.class) {
						
						value 	= cursor.getBlob(columnIndex);
						
						if(value != null){
							outType	= byte[].class ;
						}
					} else if (type == ColumnDate.class 
								|| type == ColumnTimeStamp.class) {
						
						String date = cursor.getString(columnIndex);
						value 	= DateUtils.toDate(date);
						
						if(value != null){
							outType	= Date.class ;
						}
					}
					
					// setterXX
					if(value != null && outType != null){
						ObjectReflectUtil.setter(obj, name, value, outType) ;
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		return (T) obj ;
	}

	@Override
	public void deleteDatabse() {
		helper.deleteDatabse() ;
	}
}
