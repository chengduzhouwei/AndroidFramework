package org.zw.android.framework.ioc;

import java.lang.reflect.Field;

import org.zw.android.framework.util.ReflectUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

/**
 * 注入核心工具类
 * 
 * @author zhouwei
 *
 */
public final class InjectCore {
	
	static final String TAG 						= "InjectCore" ;
	
	public static final int DEFAULT_INTEGER 		= 0 ;
	public static final String DEFAULT_STRING 		= "" ;
	
	private static Context 		mContext ;
	private static Resources	mResource ;
	
	private InjectCore(){
		
	}
	
	public static void initInjectCore(Context context){
		
		mContext	= context ;
		
		if(context != null){
			mResource 	= context.getResources() ;
		}
	}
	
	/**
	 * 针对普通的java 对象, 注入Android UI组件
	 * 
	 * @param obj
	 * @return
	 */
	public static View injectOriginalObject(Object obj){
		
		if(obj == null){
			return null ;
		}
		
		if(mContext == null){
			Log.e(TAG, "Error: The Context is null");
			return null;
		}
		
		Class<?> cls = obj.getClass() ;
		
		long enter = System.currentTimeMillis() ;
		
		InjectLayout layout = cls.getAnnotation(InjectLayout.class);
		int layoutRid 		= layout != null ? layout.layout() : DEFAULT_INTEGER ;
		
		if(layout == null || layoutRid == DEFAULT_INTEGER){
			Log.e(TAG, "Error: The Object not layout resource");
			return null ;
		}
		
		View view = LayoutInflater.from(mContext).inflate(layoutRid, null);
		
		////////////////////字段注解///////////////////////
		Field[] fields = ReflectUtils.getDeclaredFields(cls);
		Object value = null ;

		for (Field f : fields) {

			// 可以访问
			f.setAccessible(true);

			// View注解类型
			InjectView injectview 	= f.getAnnotation(InjectView.class);
			int rid 				= injectview != null ? injectview.id() : 0 ;
			
			value = view.findViewById(rid);
			
			// 设置字段值
			ReflectUtils.setFieldValue(obj, f, value);
			
			if(value == null){
				continue ;
			}
			
			// add 事件
			addViewEvent(obj,obj,fields,f,injectview);
		}
		
		// set tag
		view.setTag(obj);
		
		Log.i(TAG, "Debug 在(" + cls.toString() + ")注解消耗时间 = " + (System.currentTimeMillis() - enter));
		
		// return
		return view ;
	}
	
	/**
	 * 主动注入,参数: Activity,自定义View,Dialog,Fragment
	 * 
	 * @param obj
	 */
	public static void injectUIProperty(Object obj){
		
		if(obj == null){
			return ;
		}
		
		Class<?> cls = obj.getClass() ;
		
		long enter = System.currentTimeMillis() ;
		
		try{
			
			////////////////////类注解///////////////////////
			
			InjectLayout layout = cls.getAnnotation(InjectLayout.class);
			int layoutRid 		= layout != null ? layout.layout() : DEFAULT_INTEGER ;
			
			// Activity
			if(obj instanceof Activity){
				
				if(layout == null || layoutRid == DEFAULT_INTEGER){
					Log.e(TAG, "Error: Activity not setContentView");
					return ;
				}
				
				// set Activity Content View
				((Activity)obj).setContentView(layoutRid);
			} 
			// ViewGroup
			else if(obj instanceof ViewGroup){
				
				ViewGroup root = (ViewGroup) obj ;
				
				// inflate layout
				if(layoutRid != DEFAULT_INTEGER){
					LayoutInflater.from(root.getContext()).inflate(layoutRid, root);
				}
			}
			
			////////////////////字段注解///////////////////////
			final Field[] fields = ReflectUtils.getDeclaredFields(cls);

			for (Field f : fields) {

				// 可以访问
				f.setAccessible(true);

				// View注解类型
				InjectView injectview = f.getAnnotation(InjectView.class);
				if (injectview != null) {
					indectView(obj,obj,fields, f, injectview);
				}

				// 资源注解类型
				InjectResource injectint = f.getAnnotation(InjectResource.class);
				if(injectint != null){
					indectResource(obj, f, injectint);
				}
			}
		} catch(Exception e){
			e.printStackTrace() ;
		}
		
		Log.i(TAG, "Debug 在(" + cls.toString() + ")注解消耗时间 = " + (System.currentTimeMillis() - enter));
	}
	
	private static Field findField(Field[] fields,String field){
		
		for(Field f : fields){
			if(f.getName().equals(field)){
				return f ;
			}
		}
		
		return null ;
	}
	
	private static Object findFieldValue(Object token,Field[] fields,String field){
		
		for(Field f : fields){
			
			if(f.getName().equals(field)){
				return ReflectUtils.getFieldValueObject(token, f);
			}
		}
		
		return null ;
	}
	
	private static void indectResource(Object obj,Field field,InjectResource inject){
		
		if(mResource == null){
			return ;
		}
		
		Object value = null ;
		
		if(inject.dimen() != DEFAULT_INTEGER){
			value	= mResource.getDimensionPixelSize(inject.dimen());
		} else if(inject.color() != DEFAULT_INTEGER){
			value	= mResource.getColor(inject.color());
		} else if(inject.string() != DEFAULT_INTEGER){
			value	= mResource.getString(inject.string());
		} else if(inject.stringArray() != DEFAULT_INTEGER){
			value	= mResource.getStringArray(inject.stringArray());
		} else if(inject.drawable() != DEFAULT_INTEGER){
			value	= mResource.getDrawable(inject.drawable());
		}
		
		// 设置字段值
		ReflectUtils.setFieldValue(obj, field, value);
	}
	
	private static void indectView(Object root,Object obj,Field[] fields,Field field,InjectView injectview){
		
		if(mContext == null || injectview == null){
			return ;
		}
		
		// 字段值是否存在
		if(ReflectUtils.getFieldValueObject(obj, field) != null){
			return ;
		}
		
		View view = null ;
		
		int layout 		= injectview.layout() ;
		int rid			= injectview.id() ;
		String parent 	= injectview.parent() ;
		
		if(layout != DEFAULT_INTEGER){
			
			if(root != null){
				
				// activity windows
				if(root instanceof Activity){
					view	= ((Activity)root).getLayoutInflater().inflate(layout, null);
				}
				// 
				else if(root instanceof Dialog){
					view = LayoutInflater.from(((Dialog)root).getOwnerActivity()).inflate(layout, null);
				} 
				// view
				else if(root instanceof View){
					view = LayoutInflater.from(((View)root).getContext()).inflate(layout, null);
				} 
				// fragment
				else if(root instanceof Fragment){
					view = LayoutInflater.from(((Fragment)root).getActivity()).inflate(layout, null);
				} 
				// other
				else {
					view = LayoutInflater.from(mContext).inflate(layout, null);
				}
			}
			// other
			else {
				view = LayoutInflater.from(mContext).inflate(layout, null);
			}
		} else {
			
			// 父布局容器
			if(parent != null && !parent.equals(DEFAULT_STRING)){
				
				Field parentF 	= findField(fields,parent);
				
				// 是否存在父字段
				if(parentF == null){
					return; 
				}
				
				// 可以访问
				parentF.setAccessible(true);
				
				Object value 	= ReflectUtils.getFieldValueObject(obj, parentF);
				
				// 值是否存在
				if(value == null){
					
					// 初始化关联字段
					indectView(root,obj,fields,parentF, parentF.getAnnotation(InjectView.class));
					
					// 得到新值
					value 	= ReflectUtils.getFieldValueObject(obj, parentF);
				}
				
				// 查找view
				view = findViewById(value,rid);
				
			} else {
				
				// 查找view
				view = findViewById(obj,rid);
			}
		}
		
		// 设置字段的值
		ReflectUtils.setFieldValue(obj, field, view);
		
		// view listener
		if(view == null){
			return ;
		}
		
		// add 事件
		addViewEvent(root,obj,fields,field,injectview);
	}
	
	/** 查找view */
	private static View findViewById(Object obj,int rid){
		
		if(rid == DEFAULT_INTEGER){
			Log.w(TAG, "Warring: rid is 0");
			return null ;
		}
		
		if(obj == null){
			Log.e(TAG, "Error: Not support UI Component");
			return null ;
		}
		
		View view = null ;
		
		if(obj instanceof Activity){
			view = ((Activity)obj).findViewById(rid);
		} 
		// dialog
		else if(obj instanceof Dialog){
			view = ((Dialog)obj).findViewById(rid);
		} 
		// view
		else if(obj instanceof View){
			view = ((View)obj).findViewById(rid);
		} 
		// fragment
		else if(obj instanceof Fragment){
			view = ((Fragment)obj).getView().findViewById(rid);
		}
		
		return view ;
	}
	
	private static void addViewEvent(Object root,Object obj,Field[] fields,Field field,InjectView injectview){
		
		// /////////////////////////View////////////////////////////////
		// onclick listener
		String onclick = injectview.onClick();
		if (onclick != null && !onclick.equals(DEFAULT_STRING)) {
			
			// 查找view 组件
			View view = null ;
			
			try {
				Object ob = field.get(obj);
				
				if (ob instanceof View) {
					view = (View)ob ;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(view == null){
				return ;
			}
				
			// 设置onclick
			if(onclick.toLowerCase().equals("this")){
				
				if(root instanceof View.OnClickListener){
					view.setOnClickListener((View.OnClickListener)root);
				}
				
			} else {
				
				// 查找对于监听器实现
				Object value = findFieldValue(root,fields,onclick);
				
				if(value instanceof View.OnClickListener){
					view.setOnClickListener((View.OnClickListener)value);
				}
			}
		}

		// onlongclick listener
		String onlongclick = injectview.onLongClick();
		if (onlongclick != null && !onlongclick.equals(DEFAULT_STRING)) {
			
			// 查找view 组件
			View view = null;

			try {
				Object ob = field.get(obj);

				if (ob instanceof View) {
					view = (View) ob;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (view == null) {
				return;
			}

			// 设置onclick
			if (onclick.toLowerCase().equals("this")) {

				if (root instanceof View.OnLongClickListener) {
					view.setOnLongClickListener((View.OnLongClickListener) root);
				}

			} else {

				// 查找对于监听器实现
				Object value = findFieldValue(root, fields, onclick);

				if (value instanceof View.OnLongClickListener) {
					view.setOnLongClickListener((View.OnLongClickListener) value);
				}
			}
		}

		// ///////////////////////////AdapterView///////////////////////
		// on item click listener
		String onitemclick = injectview.onItemClickListener();
		if (onitemclick != null && !onitemclick.equals(DEFAULT_STRING)) {
			
			// 查找view 组件
			AdapterView<?> view = null;

			try {
				Object ob = field.get(obj);

				if (ob instanceof AdapterView) {
					view = (AdapterView<?>) ob;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (view == null) {
				return;
			}

			// 设置onclick
			if (onclick.toLowerCase().equals("this")) {

				if (root instanceof AdapterView.OnItemClickListener) {
					view.setOnItemClickListener((AdapterView.OnItemClickListener) root);
				}

			} else {

				// 查找对于监听器实现
				Object value = findFieldValue(root, fields, onclick);

				if (value instanceof AdapterView.OnItemClickListener) {
					view.setOnItemClickListener((AdapterView.OnItemClickListener) value);
				}
			}
		}

		// on item click listener
		String onitemlongclick = injectview.onItemLongClickListener();
		if (onitemlongclick != null && !onitemlongclick.equals(DEFAULT_STRING)) {
			
			// 查找view 组件
			AdapterView<?> view = null;

			try {
				Object ob = field.get(obj);

				if (ob instanceof AdapterView) {
					view = (AdapterView<?>) ob;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (view == null) {
				return;
			}

			// 设置onclick
			if (onclick.toLowerCase().equals("this")) {

				if (root instanceof AdapterView.OnItemLongClickListener) {
					view.setOnItemLongClickListener((AdapterView.OnItemLongClickListener) root);
				}

			} else {

				// 查找对于监听器实现
				Object value = findFieldValue(root, fields, onclick);

				if (value instanceof AdapterView.OnItemLongClickListener) {
					view.setOnItemLongClickListener((AdapterView.OnItemLongClickListener) value);
				}
			}
		}
	}
}
