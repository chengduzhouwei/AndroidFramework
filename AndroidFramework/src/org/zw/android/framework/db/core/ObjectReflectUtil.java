package org.zw.android.framework.db.core;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import org.zw.android.framework.db.ColumnBinary;
import org.zw.android.framework.db.ColumnDate;
import org.zw.android.framework.db.ColumnFloat;
import org.zw.android.framework.db.ColumnInt;
import org.zw.android.framework.db.ColumnLong;
import org.zw.android.framework.db.ColumnShort;
import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.db.Table;

/**
 * Object Reflect Util; java Object to SQLite table
 * 
 * @author zhouwei
 *
 */
public final class ObjectReflectUtil {
	
	private static HashMap<Class<?>, String> ClassTableMap 		= new HashMap<Class<?>, String>() ;
	
	/**
	 * clear all temp var
	 */
	protected static void reset(){
		ClassTableMap.clear() ;
	}
	
	/**
	 * Get Table Name by Class Annotation
	 * 
	 * @param objClass
	 * @return
	 */
	protected static String getTableNameByAnnotation(Class<?> objClass) {
		
		if(objClass == null){
			return null ;
		}
		
		final Table table = objClass.getAnnotation(Table.class);
		
		return table != null ? table.TableName() : null ;
	}
	
	/**
	 * delete table
	 * 
	 * @param table
	 */
	protected static void deleteTable(String tableName){
		
	}
	
	/** get the class table name  */
	public static String getTableNameByClass(Class<?> cls){
		
		if(cls == null) return null ;
		
		String table = ClassTableMap.get(cls) ;
		
		if(table != null){
			return table ;
		}
		
		// get table name by Annotation
		String name	= ObjectReflectUtil.getTableNameByAnnotation(cls) ;
		
		if(name != null){
			ClassTableMap.put(cls, name) ;
		}
		
		return name;
	}
	
	public static Class<?> columnTypeToJavaType(Class<?> columnType){
		
		if(columnType == null) return null ;
		
		if (columnType == ColumnShort.class) {
			return short.class ;
		} else if (columnType == ColumnInt.class) {
			return int.class ;
		} else if (columnType == ColumnLong.class) {
			return long.class ;
		} else if (columnType == ColumnFloat.class) {
			return Float.class ;
		} else if (columnType == ColumnString.class) {
			return String.class ;
		} else if (columnType == ColumnBinary.class) {
			return byte[].class ;
		} else if (columnType == ColumnDate.class) {
			return Date.class ;
		}
		
		return null ;
	}
	
	/**
	 * Get Setter Method name
	 * @param attName
	 * @return
	 */
	protected static String getMethodNameSetter(String attName){
		return "set" + attName.substring(0, 1).toUpperCase() + attName.substring(1, attName.length()) ;
	}
	
	/**
	 * Get Setter Method name
	 * @param attName
	 * @return
	 */
	protected static String getMethodNameGetter(String attName){
		return "get" + attName.substring(0, 1).toUpperCase() + attName.substring(1, attName.length()) ;
	}
	
	/** invoke getXX method*/
	protected static Object getter(Object obj, String att) {
    	String methodName  	= "get" + att.substring(0, 1).toUpperCase() + att.substring(1, att.length()) ;
        return invokeGetter(obj,methodName);
    }
	
	/** invoke getXX method*/
	protected static Object getterBoolean(Object obj, String att){
		String methodName  	= "is" + att.substring(0, 1).toUpperCase() + att.substring(1, att.length()) ;
        return invokeGetter(obj,methodName);
	}
    
    /** invoke getXX method*/
	protected static Object invokeGetter(Object obj, String methodName) {
        try {
            Method method 		= obj.getClass().getMethod(methodName);
            return method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null ;
        }
    }
    
    /** invoke setXX method*/
    protected static boolean setter(Object obj, String att, Object value,Class<?> type) {
		String methodName = "set" + att.substring(0, 1).toUpperCase() + att.substring(1, att.length());
		return invokeSetter(obj, methodName, value, type);
	}
 
    /** invoke setXX method*/
    protected static boolean invokeSetter(Object obj, String methodName, Object value, Class<?> type) {
        try {
        	Method method 	   = obj.getClass().getMethod(methodName, type);
            method.invoke(obj, value);
            return true ;
        } catch (Exception e) {
            e.printStackTrace();
            return false ;
        }
    }
    
	private ObjectReflectUtil(){}
}
