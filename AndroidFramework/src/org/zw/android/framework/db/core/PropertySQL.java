package org.zw.android.framework.db.core;

import android.content.ContentValues;

public final class PropertySQL {

	private String 				table ;
	private String 				nullColumnHack ;
	private String 				where ;
	private ContentValues 		contentValues ;
	
	private String 				sql ;
	private String[] 			params ;
	
	protected PropertySQL(){
		
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getTable() {
		return table;
	}
	
	public void setTable(String table) {
		this.table = table;
	}
	
	public ContentValues getContentValues() {
		return contentValues;
	}

	public void setContentValues(ContentValues param) {
		this.contentValues = param;
	}

	public String getNullColumnHack() {
		return nullColumnHack;
	}

	public void setNullColumnHack(String nullColumnHack) {
		this.nullColumnHack = nullColumnHack;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}
}
