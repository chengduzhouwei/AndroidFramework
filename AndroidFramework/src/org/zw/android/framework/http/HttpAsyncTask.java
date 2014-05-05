package org.zw.android.framework.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * HTTP Async Task
 * 
 * @author zhouwei
 *
 */
public class HttpAsyncTask extends AbstractTask {

	private String url ;
	private MethodType methodType ;
	private final HashMap<String, String> propertyMap ;
	private final HashMap<String, String> parameterMap ;
	
	public HttpAsyncTask(){
		propertyMap 	= new HashMap<String, String>() ;
		parameterMap 	= new HashMap<String, String>() ;
		setUrl("");
		setMethodType(MethodType.GET);
		setResultType(ResultType.TYPE_STREAM) ;
		setObjectWrapper(null);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	protected MethodType getMethodType() {
		return methodType;
	}

	public void setMethodType(MethodType methodType) {
		this.methodType = methodType;
	}

	public void addProperty(String property,String value){
		propertyMap.put(property, value);
	}
	
	public HashMap<String, String> getPropertes() {
		return propertyMap;
	}

	public HashMap<String, String> getParameters() {
		return parameterMap;
	}

	public void addParameter(String property,String value){
		parameterMap.put(property, value);
	}
	
	@Override
	public Object onProcessing() {
		
		final ResultType type = getResultType() ;
		final IObjectWrapper wrapper = getObjectWrapper() ;
		
		InputStream 	  input 	= null ;
		
		try {
			
			HttpURLConnection connect = getHttpConnection(this) ;
			
			input = connect.getInputStream() ;
			
			byte[] data 	= readInputStream(input) ;
			Object value 	= null ;
			
			// process
			if(type == ResultType.BYTE_ARRAY){
				value =  data ;
			} else if(type == ResultType.TYPE_STREAM){
				value = new ByteArrayInputStream(data) ;
			}
			
			// call wrapper object
			if(wrapper != null){
				value = wrapper.wrapper(value) ;
			}
			
			connect.disconnect() ;
			
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
	
	/** Connection */
	private final static HttpURLConnection getHttpConnection(HttpAsyncTask task) throws Exception {
		
		final StringBuffer param 	= new StringBuffer() ;
		final MethodType type 		= task.getMethodType() ;
		final int readTime			= task.getReadTimeout() ;
		final int connectTime		= task.getConnectTimeout() ;
		String str					= task.getUrl() ;
		
		HashMap<String, String> map = task.getParameters() ;
		Iterator<String> keys = map.keySet().iterator() ;
		int index = 0 ;
		
		while(keys.hasNext()){
			
			if(index > 0){
				param.append("&");
			}
			
			String key = keys.next() ;
			param.append(key) ;
			param.append("=") ;
			param.append(map.get(key)) ;
			
			index++ ;
		}
		
		if (type == MethodType.GET && index > 0) {
			str = str + "?" + param.toString() ;
        }
		
        HttpURLConnection connection = null;
        final URL url = new URL(str);
        // open connection
        connection = (HttpURLConnection) url.openConnection();
        // connection read time out
        connection.setReadTimeout(readTime);
        // connection time out
        connection.setConnectTimeout(connectTime);
        
        // request property
        map = task.getPropertes() ;
        keys = map.keySet().iterator() ;
        
        while(keys.hasNext()){
        	String key = keys.next() ;
        	connection.setRequestProperty(key, map.get(key));
        }
        
        // set connection
        if (type == MethodType.POST) {
            connection.setRequestMethod("POST");
            
            // application/x-www-form-urlencoded // application/json
            //connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            // write parameter
			OutputStream output = connection.getOutputStream();
			output.write(param.toString().getBytes());
			output.flush();
			output.close();
			output = null;
        } else if (type == MethodType.GET) {
            connection.setRequestMethod("GET");
        }
        
        // connnection
		connection.connect();
        // result
        return connection;
    }
}
