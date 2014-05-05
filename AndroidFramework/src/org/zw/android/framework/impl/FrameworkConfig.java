package org.zw.android.framework.impl;

import android.content.Context;

/**
 * Framework Config
 * 
 * 1. context
 * 2. Cache Size
 * 3. Cache Name
 * 4. Database Name
 * 5. Decode Bitmap max width and max height
 * 
 * @author zhouwei
 *
 */
public final class FrameworkConfig {

	private Context	context ;
	private float 	CachePercent ;
	private String 	CacheName ;
	private int 	maxWidth ;
	private int 	maxHeight ;
	
	private int		databaseVersion ;
	private String 	databaseName ;
	
	public FrameworkConfig(Context	context){
		this.context = context ;
		setCachePercent(0.2f);
		setCacheName("_cache");
		setMaxWidth(480);
		setMaxHeight(360);
		setDatabaseName(null);
		setDatabaseVersion(0);
	}
	
	/** Default Framework Config */
	public static FrameworkConfig defaultConfig(Context context){
		return new FrameworkConfig(context) ;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public float getCachePercent() {
		return CachePercent;
	}

	public void setCachePercent(float cachePercent) {
		CachePercent = cachePercent;
	}

	public String getCacheName() {
		return CacheName;
	}

	public void setCacheName(String cacheName) {
		CacheName = cacheName;
	}

	public int getDatabaseVersion() {
		return databaseVersion;
	}

	public void setDatabaseVersion(int databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
}
