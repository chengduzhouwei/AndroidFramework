package org.zw.android.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class ReflectUtils {

	private ReflectUtils(){
		
	}
	
	public static Field[] getDeclaredFields(Class<?> clazz) {
		
		if(clazz == null){
			return null ;
		}

		List<Field> result = new ArrayList<Field>();

		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			
			if (Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			
			if ("serialVersionUID".equals(field.getName())) {
				continue;
			}
			
			result.add(field);
		}

		return result.toArray(new Field[0]);
	}
	
	// 得到字段的值 : 对象类型
	public static Object getFieldValueObject(Object obj,Field field){
		
		try {
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null ;
	}
	
	// 得到字段的值: 字符串类型
	public static String getFieldValueString(Object obj,Field field){
		
		Object value = getFieldValueObject(obj,field);
		
		if(value != null){
			return value.toString();
		}
		
		return "" ;
	}
	
	// 设置字段值
	public static void setFieldValue(Object obj,Field field,Object value){
		
		if(obj == null || field == null){
			return ;
		}
		
		try {
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Field findField(Field[] fields, String name){
		
		if(fields == null || name == null) return null ;
		
		for(Field f : fields){
			if(f.getName().equals(name)){
				return f ;
			}
		}
		
		return null ;
	}
}
