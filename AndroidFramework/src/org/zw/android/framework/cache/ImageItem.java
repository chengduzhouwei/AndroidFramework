package org.zw.android.framework.cache;


/**
 * 
 * @author zhouwei
 *
 */
public class ImageItem implements Cloneable {

	/** 高清URL */
	private String highUrl ;
	
	/** 低分辨率 URL */
	private String lowUrl ;
	
	/** 当前正在使用的URL.默认是使用High */
	private String validUrl ;
	
	private int viewWidth ;
	
	private int viewHeight ;
	
	private int decodeWidth ;
	
	private int decodeHeight ;
	
	private Object tag ;
	
	public ImageItem(){
		setHighUrl("");
		setLowUrl("");
		setDecodeWidth(0);
		setDecodeHeight(0);
		setTag(null);
		setViewWidth(0);
		setViewHeight(0);
	}

	public int getViewWidth() {
		return viewWidth;
	}

	public void setViewWidth(int viewWidth) {
		this.viewWidth = viewWidth;
	}

	public int getViewHeight() {
		return viewHeight;
	}

	public void setViewHeight(int viewHeight) {
		this.viewHeight = viewHeight;
	}

	public Object getTag() {
		return tag;
	}

	public String getValidUrl() {
		return validUrl;
	}

	protected void setValidUrl(String validUrl) {
		this.validUrl = validUrl;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public String getHighUrl() {
		return highUrl;
	}

	public void setHighUrl(String highUrl) {
		this.highUrl = highUrl;
	}

	public String getLowUrl() {
		return lowUrl;
	}

	public void setLowUrl(String lowUrl) {
		this.lowUrl = lowUrl;
	}

	public int getDecodeWidth() {
		return decodeWidth;
	}

	public void setDecodeWidth(int decodeWidth) {
		this.decodeWidth = decodeWidth;
	}

	public int getDecodeHeight() {
		return decodeHeight;
	}

	public void setDecodeHeight(int decodeHeight) {
		this.decodeHeight = decodeHeight;
	}
	
	@Override
	public ImageItem clone(){
		try{
			return (ImageItem)super.clone();
		}catch(CloneNotSupportedException e){
			return new ImageItem() ;
		}
	}
}
