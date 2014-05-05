package org.zw.android.framework.xml;

import org.xmlpull.v1.XmlPullParser;

public interface XMLParserCallback<T> {
	
	public void startDocument() ;
	
	public void startTag(T t,String tag,XmlPullParser parser) ;
	
	public void endTag(T t,String tag,XmlPullParser parser) ;
}
