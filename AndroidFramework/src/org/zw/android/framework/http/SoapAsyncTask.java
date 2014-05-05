package org.zw.android.framework.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 
 * @author zhouwei
 *
 */
public abstract class SoapAsyncTask extends AbstractTask {
	
	public SoapAsyncTask(){
		setResultType(ResultType.TYPE_STREAM);
	}
	
	@Override
	public Object onProcessing() {
		
		final ResultType type 		 = getResultType() ;
		final IObjectWrapper wrapper = getObjectWrapper() ;
		InputStream 	  input 	 = null;
		try {
			
			input = getInputStream() ;
			
			byte[] data 	= readInputStream(input) ;
			Object value 	= null ;
			
			if(type == ResultType.BYTE_ARRAY){
				value =  data ;
			} else if(type == ResultType.TYPE_STREAM){
				value = new ByteArrayInputStream(data) ;
			}
			
			if(wrapper != null){
				value = wrapper.wrapper(value) ;
			}
			
			return value ;
		} catch (Exception e) {
			e.printStackTrace();
			
			if(wrapper != null){
				wrapper.wrapper(e) ;
			}
			
			return e ;
		} finally {
			try{
				if(input != null){
					input.close() ;
				}
			}catch(Exception e){}
		}
	}
	
	/**
	 * Get Soap InputStream. User create SOAP request
	 * 
	 * @return
	 */
	public abstract InputStream getInputStream() ;

}
