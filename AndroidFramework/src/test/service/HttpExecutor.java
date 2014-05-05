package test.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public final class HttpExecutor {
	
	private final static int CONNECTION_TIMEOUT 			= 3000;
	private final static int READ_TIMEOUT	 				= 5000;
	
	protected HttpExecutor(){
		
	}
	
	private HttpParams buildHttpParams(int readTimeout){
		HttpParams httpParams = new BasicHttpParams() ;
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, readTimeout <= 0 ? READ_TIMEOUT : readTimeout);
		return httpParams ;
	}
	
	/** execute http request*/
	private boolean doExecute(HttpUriRequest httpMethod,int readTimeout,ResultObject result) {

		try {
			
			HttpParams httpParams = buildHttpParams(readTimeout);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
			
			// execute request
			HttpContext context 	= new BasicHttpContext();
			HttpResponse response 	= httpClient.execute(httpMethod, context);
			
			int stausCode = response.getStatusLine().getStatusCode() ;
			
			if (stausCode != HttpStatus.SC_OK) {
				result.setCode(stausCode) ;
				result.setContent("error: " + stausCode) ;
				return false ;
			}
			
			// get response
			HttpEntity entity 			= response.getEntity();
			String responseBody 		= EntityUtils.toString(entity);
			
			// set body to content
			result.setContent(responseBody) ;
			
			return true ;
		} catch (Exception e) {
			e.printStackTrace() ;
			return false ;
		}
	}
	
	public boolean doPost(String url,String paramter, ResultObject result) {
		return doPost(url,paramter,READ_TIMEOUT,result) ;
	}
	
	public boolean doPost(String url,String paramter,int readTimeout,ResultObject result) {
		
		StringEntity multipartEntity = null ;
		
		try {
			multipartEntity = new StringEntity(paramter);
		} catch (Exception e) {
			e.printStackTrace() ;
			return false ;
		}
		
		// execute http
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Content-Type", "application/json");
		httpPost.addHeader("Connection", "Close");
		httpPost.setEntity(multipartEntity);
		
		// handle response
		boolean noerror 	= doExecute(httpPost,readTimeout,result);
		
		if(!noerror){
			return false ;
		}
		
		// parser response code
		return parserJsonObject(result) ;
	}
	
	public boolean doGet(String url,ResultObject result) {
		
		HttpGet httpGet = new HttpGet(url) ;
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("Connection", "Close");
		// execute http
		boolean noerror 	= doExecute(httpGet,READ_TIMEOUT,result);
		
		if(!noerror){
			return false ;
		}
		
		// parser response code
		return parserJsonObject(result) ;
	} 
	
	private boolean parserJsonObject(ResultObject result){
		
		try{
			
			JSONObject j 	= new JSONObject(result.getContent()) ;
			int code 		= j.getInt("result");
			
			if(code == 0){
				return true ;
			}
		} catch(JSONException e){
			e.printStackTrace() ;
		}
		
		return false ;
	} 
}
