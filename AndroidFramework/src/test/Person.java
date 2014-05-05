package test;

import org.zw.android.framework.db.ColumnString;
import org.zw.android.framework.xml.XMLSerializable;

public class Person implements XMLSerializable {

	@ColumnString(length=32)
	private String name ;
	
	@ColumnString(length=32)
	private String address ;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toXml() {
		
		StringBuilder xml = new StringBuilder() ;
		
		xml.append("<Person>");
		
		xml.append("<name>");
		xml.append(getName());
		xml.append("</name>");
		
		xml.append("<address>");
		xml.append(getAddress());
		xml.append("</address>");
		
		return xml.toString();
	}
}
