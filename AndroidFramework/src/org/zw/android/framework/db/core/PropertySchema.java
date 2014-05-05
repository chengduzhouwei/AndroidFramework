package org.zw.android.framework.db.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.zw.android.framework.db.core.ColumnPrimaryKey.PrimaryKeyType;
import org.zw.android.framework.util.DateUtils;
import org.zw.android.framework.util.StringUtils;

import android.content.ContentValues;

public final class PropertySchema {
	
	// sqlite support table column
	static final String COLUMN_DEFINE_BOOLEAN		= " BOOLEAN" ; // short
	static final String COLUMN_DEFINE_SHORT			= " SHORT" ; // short
	static final String COLUMN_DEFINE_INTEGER		= " INTEGER" ; // int
	static final String COLUMN_DEFINE_LONG			= " LONG" ; // long
	static final String COLUMN_DEFINE_FLOAT			= " FLOAT" ;// float
	static final String COLUMN_DEFINE_DOUBLE		= " DOUBLE" ;// double
	// static final String COLUMN_DEFINE_REAL			= " REAL" ; // real
	static final String COLUMN_DEFINE_STRING		= " VARCHAR" ; // varchar
	static final String COLUMN_DEFINE_TEXT			= " TEXT" ; // text
	static final String COLUMN_DEFINE_BIN			= " BLOB" ; // bin
	static final String COLUMN_DEFINE_DATE			= " DATE" ; // date
	static final String COLUMN_DEFINE_TIMESTAMP		= " TIMESTAMP" ; // date
	
	static final String TAG = "PropertySchema" ;
	
	private final static HashMap<Class<?>, PropertySchema> PropertySchemas = new HashMap<Class<?>, PropertySchema>();
	
	private String 	table ;
	
	private List<PropertyField>  primaryKeys ;
	
	private List<PropertyField> propertyFields ;
	
	private PropertySchema(String name,List<PropertyField> list){
		
		table			= name ;
		propertyFields	= list ;
		
		primaryKeys		= new ArrayList<PropertyField>() ;
		
		for(PropertyField field : list){
			if(field.isPrimaryKey()){
				primaryKeys.add(field);
			}
		}
	}
	
	public String getTable() {
		return table;
	}

	public List<PropertyField> getPropertyFields() {
		return propertyFields;
	}
	
	public List<PropertyField> getPrimaryKeys() {
		return primaryKeys;
	}

	public String createTable(){
		
		final List<PropertyField> list = propertyFields ;
		
		final StringBuffer sql = new StringBuffer();
		sql.append("CREATE TABLE IF NOT EXISTS ");
		sql.append(table);
		sql.append(" (");

		for (int index = 0, size = list.size(); index < size; index++) {

			PropertyField pro 	= list.get(index);
			String name 		= pro.getPropertyName();
			String tableColumn 	= pro.getPropertyColumn();

			if (StringUtils.isEmpty(name) || StringUtils.isEmpty(tableColumn)) {
				continue;
			}

			if (index > 0) {
				sql.append(",");
			}

			// primary key
			if (pro.isPrimaryKey()) {
				sql.append(name);
				sql.append(tableColumn);
				sql.append(" PRIMARY KEY");
			}
			// normal column
			else {
				sql.append(name);
				sql.append(tableColumn);
			}
		}

		// end
		sql.append(")");

		return sql.toString();
	}
	
	public String deleteAll(){
		
		final StringBuffer sql = new StringBuffer();
		
		sql.append("DELETE FROM ");
		sql.append(table);
		
		return sql.toString() ;
	}
	
	public String deleteTable(){
		
		final StringBuffer sql = new StringBuffer();
		
		sql.append("DROP TABLE IF EXISTS ");
		sql.append(table);
		
		return sql.toString() ;
	}
	
	public PropertySQL insertObject(Object obj){
		
		final List<PropertyField> list = objectPropertyValues(obj,false) ;
		
		if(list == null || list.isEmpty()){
			return null ;
		}
		
		final PropertySQL psql		= new PropertySQL();
		final ContentValues param	= new ContentValues();
		
		// column
		for(int index = 0, size = list.size(); index < size ; index++){
			
			PropertyField pro		= list.get(index) ;
			String name 			= pro.getPropertyName() ;
			Class<?> type			= pro.getPropertyType() ;
			Object value 			= pro.getPropertyValue() ;
			
			if(StringUtils.isEmpty(name) 
					|| value == null ){
				continue ;
			}
			
			if (type == ColumnByte.class) {
				param.put(name, Byte.valueOf(Integer.valueOf(value.toString()).byteValue()));
			} else if (type == ColumnShort.class) {
				param.put(name, Short.valueOf(Integer.valueOf(value.toString()).shortValue()));
			} else if (type == ColumnInt.class) {
				param.put(name, Integer.valueOf(Integer.valueOf(value.toString()).intValue()));
			} else if (type == ColumnLong.class) {
				param.put(name, Long.valueOf(Integer.valueOf(value.toString()).longValue()));
			} else if (type == ColumnFloat.class) {
				param.put(name, Float.valueOf(value.toString()));
			}  else if (type == ColumnDouble.class) {
				param.put(name, Double.valueOf(value.toString()));
			} else if (type == ColumnBoolean.class) {
				param.put(name, Boolean.valueOf(value.toString()));
			} else if (type == ColumnString.class) {
				param.put(name, String.valueOf(value.toString()));
			} else if (type == ColumnText.class) {
				param.put(name, String.valueOf(value.toString()));
			} else if (type == ColumnBinary.class) {
				if(value instanceof byte[]){
					param.put(name, (byte[])value);
				}
			} else if (type == ColumnDate.class 
					|| type == ColumnTimeStamp.class) {
				
				if(value instanceof Date){
					param.put(name, DateUtils.toDateString((Date)value, DateUtils.DATE_TIME_FORMAT));
				}
			}
		}
		
		// parameters
		psql.setTable(table);
		psql.setContentValues(param);
		
		return psql ;
	}
	
	public PropertySQL updateObject(Object bean){
		
		if(bean == null){
			return null ;
		}
		
		final List<PropertyField> propertys = objectPropertyValues(bean,false) ;
		
		if(propertys == null || propertys.isEmpty()){
			return null ;
		}
		
		final PropertySQL psql 			= new PropertySQL() ;
		
		final StringBuffer where 		= new StringBuffer() ;
		final ContentValues content		= new ContentValues();
		
		// put ContentValues
		for(int index = 0 , size = propertys.size(); index < size ; index++){
			
			PropertyField pro	= propertys.get(index) ;
			String name			= pro.getPropertyName() ;
			Object value		= pro.getPropertyValue() ;
			Class<?> type		= pro.getPropertyType() ;
			
			if(StringUtils.isEmpty(name) 
					|| value == null
					|| pro.isPrimaryKey()){
				continue ;
			}
			
			if (type == ColumnByte.class) {
				content.put(name, Byte.valueOf(Integer.valueOf(value.toString()).byteValue()));
			} else if (type == ColumnShort.class) {
				content.put(name, Short.valueOf(Integer.valueOf(value.toString()).shortValue()));
			} else if (type == ColumnInt.class) {
				content.put(name, Integer.valueOf(Integer.valueOf(value.toString()).intValue()));
			} else if (type == ColumnLong.class) {
				content.put(name, Long.valueOf(Integer.valueOf(value.toString()).longValue()));
			} else if (type == ColumnFloat.class) {
				content.put(name, Float.valueOf(value.toString()));
			}  else if (type == ColumnDouble.class) {
				content.put(name, Double.valueOf(value.toString()));
			} else if (type == ColumnBoolean.class) {
				content.put(name, Boolean.valueOf(value.toString()));
			} else if (type == ColumnString.class
						|| type == ColumnText.class) {
				content.put(name, value.toString());
			} else if (type == ColumnBinary.class) {
				
				if(value instanceof byte[]){
					content.put(name, (byte[])value);
				}
			} else if (type == ColumnDate.class 
					|| type == ColumnTimeStamp.class) {
				
				if(value instanceof Date){
					content.put(name, DateUtils.toDateString((Date)value, DateUtils.DATE_TIME_FORMAT));
				}
			}
		}
		
		// update where 
		final List<PropertyField> primaryKeys = objectPropertyValues(bean,true) ;
		final ArrayList<String> params = new ArrayList<String>() ;
		boolean needAnd = false ;
		
		for(int index = 0 , size = primaryKeys.size(); index < size ; index++){
			
			PropertyField pro	= primaryKeys.get(index) ;
			String name			= pro.getPropertyName() ;
			Object value		= pro.getPropertyValue() ;
			Class<?> type		= pro.getPropertyType() ;
			
			if(StringUtils.isEmpty(name) 
					|| value == null ){
				continue ;
			}
			
			if(pro.isPrimaryKey()){
				
				if(needAnd){
					where.append(" AND ") ;
					needAnd	= false ;
				}
				
				if(type == ColumnByte.class
						|| type == ColumnShort.class
						|| type == ColumnInt.class
						|| type == ColumnLong.class
						|| type == ColumnFloat.class
						|| type == ColumnDouble.class
						|| type == ColumnString.class
						|| type == ColumnDate.class){
					where.append(name) ;
					where.append(" = ? ") ;
					params.add(value.toString());
					needAnd	= true ;
				} else {
					needAnd	= false ;
				}
			}
		}
		
		// end
		psql.setTable(table);
		psql.setContentValues(content);
		
		String[] param = new String[params.size()];
		for(int index = 0 , size = params.size() ; index < size ; index++){
			param[index] = params.get(index) ;
		}
		
		// set where and parameter
		psql.setWhere(where.toString()) ;
		psql.setParams(param);
		
		return psql ;
	}
	
	public PropertySQL queryOutsidePrimaryKey(Object obj){
		
		final List<PropertyField> propertys = objectPropertyValues(obj,false) ;
		
		final List<PropertyField> columns = new ArrayList<PropertyField>() ;
		
		for(PropertyField pro : propertys){
			
			if(!pro.isPrimaryKey()){
				columns.add(pro) ;
			}
		}
		
		return queryObjectByPropertyField(columns);
	}
	
	public PropertySQL queryWithPrimaryKey(Object obj){
		
		final List<PropertyField> propertys = objectPropertyValues(obj,true) ;
		
		final List<PropertyField> columns = new ArrayList<PropertyField>() ;
		
		for(PropertyField pro : propertys){
			
			if(pro.isPrimaryKey()){
				columns.add(pro) ;
			}
		}
		
		return queryObjectByPropertyField(columns);
	}
	
	private PropertySQL queryObjectByPropertyField(List<PropertyField> list){
		
		if(list == null || list.isEmpty()){
			return null ;
		}
		
		final PropertySQL psql 			= new PropertySQL() ;
		final StringBuffer selection 	= new StringBuffer() ;
		final ArrayList<String> params 	= new ArrayList<String>() ;
		boolean hasProperty	= false ;
		
		for(int index = 0 , size = list.size(); index < size ; index++){
			
			PropertyField pro	= list.get(index) ;
			String name			= pro.getPropertyName() ;
			Object value		= pro.getPropertyValue() ;
			Class<?> type		= pro.getPropertyType() ;
			
			if(StringUtils.isEmpty(name) || value == null){
				continue ;
			}
			
			if(index > 0 && type != ColumnBinary.class && hasProperty){
				selection.append(" AND ") ;
			}
			
			if (type == ColumnByte.class) {
				selection.append(name) ;
				selection.append(" = ? ") ;
				params.add(Byte.toString(Short.valueOf(value.toString()).byteValue()));
				hasProperty	= true ;
			} else if (type == ColumnShort.class) {
				selection.append(name) ;
				selection.append(" = ? ") ;
				params.add(Short.toString(Short.valueOf(value.toString()).shortValue()));
				hasProperty	= true ;
			} else if (type == ColumnInt.class) {
				selection.append(name) ;
				selection.append(" = ? ") ;
				params.add(Integer.toString(Integer.valueOf(value.toString()).intValue()));
				hasProperty	= true ;
			} else if (type == ColumnLong.class) {
				selection.append(name) ;
				selection.append(" = ? ") ;
				params.add(Long.toString(Long.valueOf(value.toString()).longValue()));
				hasProperty	= true ;
			} else if (type == ColumnFloat.class) {
				selection.append(name) ;
				selection.append(" = ? ") ;
				params.add(Float.toString(Float.valueOf(value.toString()).floatValue()));
				hasProperty	= true ;
			} else if (type == ColumnDouble.class) {
				selection.append(name) ;
				selection.append(" = ? ") ;
				params.add(Double.toString(Double.valueOf(value.toString()).floatValue()));
				hasProperty	= true ;
			} else if (type == ColumnBoolean.class) {
				selection.append(name) ;
				selection.append(" = ? ") ;
				params.add(Boolean.toString(Boolean.valueOf(value.toString()).booleanValue()));
				hasProperty	= true ;
			} else if (type == ColumnString.class
						|| type == ColumnText.class) {
				selection.append(name) ;
				selection.append(" = ? ") ;
				params.add(value.toString());
				hasProperty	= true ;
			} else if (type == ColumnDate.class 
						|| type == ColumnTimeStamp.class) {
				
				if(value instanceof Date){
					
					selection.append(name) ;
					selection.append(" = ? ") ;
					
					params.add(DateUtils.toDateString((Date)value,DateUtils.DATE_TIME_FORMAT));
					hasProperty	= true ;
				} else {
					hasProperty	= false ;
				}
			} else {
				hasProperty	= false ;
			}
		}
		
		// end
		psql.setTable(table);
		
		// select SQL
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM ");
		sql.append(table);
		sql.append(" WHERE ");
		sql.append(selection.toString());
		
		String[] param = new String[params.size()];
		for(int index = 0 , size = params.size() ; index < size ; index++){
			param[index]	= params.get(index);
		}
		
		// set property parameter
		psql.setSql(sql.toString());
		psql.setWhere(selection.toString());
		psql.setParams(param);
		
		return psql ;
	}
	
	public static PropertySchema create(Class<?> cls,boolean checkObject){
		
		if(cls == null){
			return null ;
		}
		
		final HashMap<Class<?>, PropertySchema> map = PropertySchemas ;
		
		// property schema is exist
		PropertySchema schema = map.get(cls) ;
		
		if(schema != null){
			return schema ;
		}
		
		// not database class
		String table = ObjectReflectUtil.getTableNameByClass(cls) ;
		
		if(checkObject && StringUtils.isEmpty(table)){
			return null ;
		}
		
		// the class declared field
		List<PropertyField> list = getClassAllPropertyFields(cls,checkObject);
		
		if(list == null || list.isEmpty()){
			return null ;
		}
		
		schema = new PropertySchema(table,list) ;
		
		map.put(cls, schema) ;
		
		return schema ;
	}
	
	public static void getClassAllDeclaredFields(Class<?> cls,List<Field> list){
		
		if(list == null || cls == null){
			return ;
		}
		
		// is object return
		if(cls.toString().equals(Object.class.toString())){
			return ;
		}
		
		Field[] fields 		= getClassSingleDeclaredFields(cls) ;
		
		// is no field return
		if(fields == null){
			return ;
		}
		
		// fill field to list
		for(int index = 0 , size = fields.length ; index < size ; index++){
			list.add(fields[index]);
		}
		
		// get super class Declared field
		getClassAllDeclaredFields(cls.getSuperclass(),list);
	}
	
	public static Field[] getClassSingleDeclaredFields(Class<?> cls){
		
		if(cls == null){
			return null ;
		}
		
		List<Field> outs 	= new ArrayList<Field>();
		Field[] fields 		= cls.getDeclaredFields() ;
		
		for(int index = 0 , size = fields.length ; index < size ; index++){
			
			Annotation[] anns	= fields[index].getAnnotations() ;
			
			if(anns == null){
				continue ;
			}
			
			// check annotation type
			for(int j = 0 ,ann_length = anns.length; j < ann_length; j++){
				
				Class<?> ann = anns[j].annotationType();
				
				// property type
				if (ann == ColumnByte.class
						|| ann == ColumnShort.class
						|| ann == ColumnInt.class
						|| ann == ColumnLong.class
						|| ann == ColumnFloat.class
						|| ann == ColumnDouble.class
						|| ann == ColumnBoolean.class
						|| ann == ColumnString.class
						|| ann == ColumnText.class
						|| ann == ColumnBinary.class
						|| ann == ColumnDate.class
						|| ann == ColumnTimeStamp.class) {
					outs.add(fields[index]);
					break ;
				}
			}
		}
		
		if(outs.isEmpty()){
			return null ;
		}
		
		Field[] array = new Field[outs.size()];
		
		for(int index = 0 , size = outs.size() ; index < size ; index++){
			array[index] = outs.get(index);
		}
		
		return array;
	}
	
	private static List<PropertyField> getClassAllPropertyFields(Class<?> cls,boolean checkField) {
		
		// class all fields
		List<Field> fields = new ArrayList<Field>();
		
		// the object fields
		getClassAllDeclaredFields(cls,fields);
		
		if(fields.isEmpty()){
			return null ;
		}
		
		// object property list
		List<PropertyField> pros = new ArrayList<PropertyField>() ;
		
		// build all property of the class
		buildPropertyField(pros,fields,checkField);
		
		// return
		return pros ;
	}
	
	/** build property field list */
	private static void buildPropertyField(List<PropertyField> pros,List<Field> fields,boolean checkField){
		
		if(pros == null || fields == null){
			return ;
		}
		
		int size 		= fields.size() ;
		int ann_length 	= 0 ;
		PropertyField  property = null ;
		
		for(int index = size - 1 ; index >= 0 ; index--){
			
			String attName 		= fields.get(index).getName() ;
			Annotation[] anns	= fields.get(index).getAnnotations() ;
			
			if(checkField && anns == null){
				continue ;
			}
			
			property 			= new PropertyField() ;
			ann_length			= anns != null ? anns.length : 0 ;
			
			// name and primary key
			property.setPropertyName(attName) ;
			property.setPrimaryKey(false);
			
			// check field is table column , the object field type
			for (int j = 0; j < ann_length; j++) {
				
				// custom annotation
				Class<?> ann = anns[j].annotationType();
				
				// PRIMARY KEY
				if (ann == ColumnPrimaryKey.class) {
					property.setPrimaryKey(true);
					continue ;
				}
				
				// set property type
				property.setPropertyType(ann) ;
				
				// property type
				if (ann == ColumnByte.class) {
					property.setPropertyColumn(COLUMN_DEFINE_SHORT);
				} else if (ann == ColumnShort.class) {
					property.setPropertyColumn(COLUMN_DEFINE_SHORT);
				} else if (ann == ColumnInt.class) {
					property.setPropertyColumn(COLUMN_DEFINE_INTEGER);
				} else if (ann == ColumnLong.class) {
					property.setPropertyColumn(COLUMN_DEFINE_LONG);
				} else if (ann == ColumnFloat.class) {
					property.setPropertyColumn(COLUMN_DEFINE_FLOAT);
				} else if (ann == ColumnDouble.class) {
					property.setPropertyColumn(COLUMN_DEFINE_DOUBLE);
				} else if (ann == ColumnBoolean.class) {
					property.setPropertyColumn(COLUMN_DEFINE_BOOLEAN);
				} else if (ann == ColumnString.class) {
					int length = ((ColumnString)anns[j]).length() ;
					property.setPropertyColumn(COLUMN_DEFINE_STRING + "(" + length + ")");
				} else if(ann == ColumnText.class){
					property.setPropertyColumn(COLUMN_DEFINE_TEXT);
				} else if (ann == ColumnBinary.class) {
					property.setPropertyColumn(COLUMN_DEFINE_BIN);
				} else if (ann == ColumnDate.class) {
					property.setPropertyColumn(COLUMN_DEFINE_DATE);
				} else if(ann == ColumnTimeStamp.class){
					property.setPropertyColumn(COLUMN_DEFINE_TIMESTAMP);
				} 
			}
			
			// add property to list
			pros.add(property) ;
		}
	}
	
	private static List<PropertyField> objectPropertyValues(Object obj,boolean ignorePrimaryKey){
		
		if (obj == null) {
			return null;
		}
		
		List<Field> fields = new ArrayList<Field>();
		
		// the object all fields
		getClassAllDeclaredFields(obj.getClass(), fields);
		
		if(fields.isEmpty()){
			return null ;
		}

		// create object property value list
		List<PropertyField> list = new ArrayList<PropertyField>() ;
		
		// build property values
		buildPropertyValues(obj,ignorePrimaryKey,list,fields);
		
		return list;
	}
	
	/** build property value list */
	private static void buildPropertyValues(Object obj,boolean ignorePrimaryKey,List<PropertyField> list,List<Field> fields){
		
		int size = fields.size();
		int ann_length = 0;
		PropertyField property 	= null ;
		Object value			= null ;
		boolean insertCoulmn	= false ;
		
		for(int index = size - 1 ; index >= 0 ; index--){
			
			String attName 		= fields.get(index).getName() ;
			Annotation[] anns	= fields.get(index).getAnnotations() ;
			
			if(anns == null){
				continue ;
			}
			
			property 			= new PropertyField() ;
			ann_length			= anns.length ;
			insertCoulmn		= true ;
			property.setPrimaryKey(false);
			
			// the object field type
			for (int j = 0; j < ann_length; j++) {

				// custom annotation
				Class<?> ann = anns[j].annotationType();

				// PRIMARY KEY
				if (ann == ColumnPrimaryKey.class) {
					
					if(!ignorePrimaryKey){
						
						PrimaryKeyType type = ((ColumnPrimaryKey)anns[j]).Type() ;
						
						if(type == PrimaryKeyType.AUTO){
							insertCoulmn = false ;
						}
					}
						
					property.setPrimaryKey(true);
					
					continue;
				}
				
				// reset
				value	= null ;
				
				// property type
				if(ann == ColumnByte.class
						|| ann == ColumnShort.class
						|| ann == ColumnInt.class
						|| ann == ColumnLong.class
						|| ann == ColumnFloat.class
						|| ann == ColumnDouble.class
						|| ann == ColumnString.class
						|| ann == ColumnText.class
						|| ann == ColumnBinary.class
						|| ann == ColumnDate.class
						|| ann == ColumnTimeStamp.class){
					// set property type
					property.setPropertyType(ann);
					value = ObjectReflectUtil.getter(obj, attName) ;
				} else if(ann == ColumnBoolean.class){
					// set boolean type
					property.setPropertyType(ann);
					value = ObjectReflectUtil.getterBoolean(obj, attName) ;
				}
			}

			// add property to list
			if(insertCoulmn && obj != null && value != null){
				property.setPropertyName(attName);
				property.setPropertyValue(value) ;
				list.add(property);
			}
		}
	}
	
	public static boolean copyPrimaryKey(Object des, Object src){
		
		if(des == null || src == null){
			return false ;
		}
		
		boolean success = false ;
		
		List<PropertyField> propertys = getClassAllPropertyFields(des.getClass(),true);
		
		Object value		= null ;
		Class<?> javaType 	= null ;
		
		for(PropertyField field : propertys){
			
			String name 	= field.getPropertyName() ;
			Class<?> type 	= field.getPropertyType() ;
			
			if(field.isPrimaryKey()){
				
				value 		= ObjectReflectUtil.getter(src, name) ;
				javaType 	= ObjectReflectUtil.columnTypeToJavaType(type) ;
				success 	= ObjectReflectUtil.setter(des, name, value, javaType) ;
			}
		}
		
		return success ;
	}
}
