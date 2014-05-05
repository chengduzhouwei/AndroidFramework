package org.zw.android.framework.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.zw.android.framework.util.DateUtils;
import org.zw.android.framework.util.ReflectUtils;
import org.zw.android.framework.util.StringUtils;

import android.util.Xml;

/**
 * XML Object factory
 * 
 * @author zhouwei
 *
 */
public final class XMLFactory {
	
	static final String TAG = "XMLFactory" ;
	
	private static Map<String, Field[]> FieldCache = new HashMap<String, Field[]>();
	
	private XMLFactory(){
		
	}
	
	public static void reset(){
		FieldCache.clear() ;
	}
	
	private static Field[] getFields(Class<?> cls){
		
		if(cls == null){
			return null ;
		}
		
		String clsName 		= cls.getSimpleName() ;
		
		// find fields from cache
		Field[] fields = FieldCache.get(clsName);

		if (fields == null) {

			fields = ReflectUtils.getDeclaredFields(cls);

			if (fields != null) {
				FieldCache.put(clsName, fields);
			}
		}
		
		return fields ;
	}
	
	/**
	 * to XML String
	 * 
	 * The Object only support field :  
	 * byte Byte 
	 * short Short
	 * int Integer
	 * long Long
	 * float Float
	 * double Double
	 * String 
	 * java.util.Date
	 * java.util.List
	 * XMLSerializable
	 * 
	 * @param bean
	 * @return
	 */
	public static String toXML(Object bean){
		
		if(bean == null){
			return "" ;
		}
		
		final StringBuilder xml = new StringBuilder() ;
		Class<?> cls 		= bean.getClass() ;
		String clsName 		= cls.getSimpleName() ;
		
		// root
		addTag(xml,clsName,false) ;
		
		// find fields from cache
		Field[] fields = getFields(cls);
		
		// fields is null
		if(fields == null){
			addTag(xml,clsName,true) ;
			return xml.toString() ;
		}
		
		// find all field
		for(Field field : fields){
			
			// accessible
			field.setAccessible(true);
			
			String tagName 	= field.getName() ;
			Class<?> type 	= field.getType() ;
			String value 	= null ;
			
			// start
			addTag(xml,tagName,false) ;
			
			// java base number type
			if(type == byte.class
					|| type == Byte.class
					|| type == short.class
					|| type == Short.class
					|| type == int.class
					|| type == Integer.class
					|| type == long.class
					|| type == Long.class
					|| type == double.class
					|| type == Double.class
					|| type == float.class
					|| type == Float.class
					|| type == String.class){
				value = ReflectUtils.getFieldValueString(bean, field);
			} 
			// date
			else if(type == Date.class){
				
				Object date = ReflectUtils.getFieldValueObject(bean, field);
				
				if(date != null){
					value 	= DateUtils.toDateString((Date)date, DateUtils.DATE_TIME_FORMAT);
				} else{
					value	= "" ;
				}
			}
			// List
			else if(type == List.class){
				
				Object val = ReflectUtils.getFieldValueObject(bean, field);
				
				if(val != null){
					
					List<?> list = (List<?>) val ;
					
					// 递归查找
					for(Object obj : list){
						
						String node 	= toXML(obj);
						
						if(!StringUtils.isEmpty(node)){
							addTagValue(xml, node);
						}
					}
				} else {
					value	= "" ; // empty node
				}
			} 
			// XMLSerializable Object
			else if(type == XMLSerializable.class){
				
				Object val = ReflectUtils.getFieldValueObject(bean, field);
				
				if(val != null){
					value 	= ((XMLSerializable)val).toXml() ;
				} else {
					value	=  "" ;
				}
			}
			
			// add value
			if(value != null){
				addTagValue(xml,value);
			}
			
			// end
			addTag(xml,tagName,true) ;
		}
		
		// end root
		addTag(xml,clsName,true) ;
		
		return xml.toString() ;
	}
	
	private static void addTag(StringBuilder xml, String tag,boolean isEnd){
		
		if(isEnd)
			xml.append("</") ;
		else
			xml.append("<") ;
		
		xml.append(tag) ;
		xml.append(">") ;
	}
	
	private static void addTagValue(StringBuilder xml,String value) {
		xml.append(value) ;
	}
	
	public static <T> T toObjectFromXml(String xml,T obj,XMLParserCallback<T> callback) {
		
		if(obj == null || StringUtils.isEmpty(xml)){
			return null ;
		}
		
		ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
		
		return toObjectFromStream(input,null,obj,callback) ;
	}
	
	public static <T> T toObjectFromStream(InputStream input,String encoder,T obj,XMLParserCallback<T> callback) {
		
		if(obj == null || input == null || callback == null){
			return null ;
		}
		
		final XmlPullParser parser = Xml.newPullParser();
		
		try{
			
			parser.setInput(input, encoder);
			
			// find root node
			int type = parser.getEventType();
			
			while (type != XmlPullParser.END_DOCUMENT) {
				
				switch(type){
				case XmlPullParser.START_DOCUMENT :
					callback.startDocument() ;
					break ;
				case XmlPullParser.START_TAG :
					callback.startTag(obj, parser.getName(), parser) ;
					break ;
				case XmlPullParser.END_TAG :
					callback.startTag(obj, parser.getName(), parser) ;
					break ;
				}
                
				type = parser.next() ;
            }
			
			return null ;
		} catch(Exception e){
			e.printStackTrace() ;
		} finally{
			
			try {
				input.close() ;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null ;
	}
}
