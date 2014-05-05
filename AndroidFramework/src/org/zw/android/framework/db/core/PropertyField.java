package org.zw.android.framework.db.core;


public final class PropertyField {

	private boolean 			primaryKey;

	private String 				propertyName;

	private Class<?> 			propertyType;
	
	private Object 				propertyValue;

	private String propertyColumn;
	
	protected PropertyField() {

	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Class<?> getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(Class<?> propertyType) {
		this.propertyType = propertyType;
	}

	public Object getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(Object propertyValue) {
		this.propertyValue = propertyValue;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getPropertyColumn() {
		return propertyColumn;
	}

	public void setPropertyColumn(String propertyColumn) {
		this.propertyColumn = propertyColumn;
	}

}
