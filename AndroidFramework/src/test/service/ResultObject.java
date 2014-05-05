package test.service;

import java.io.Serializable;

/**
 * 
 * @author zhouwei
 *
 */
@SuppressWarnings("serial")
public final class ResultObject implements Serializable {
	
	private int 	code ;
	private String 	content ;
	private String 	error ;
	private String  session ;

	public ResultObject(){
		clear();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void clear(){
		setCode(-1) ;
		setContent("");
		setError("");
	}

	@Override
	public ResultObject clone(){
		
		try{
			
			ResultObject obj = (ResultObject)super.clone() ;
			
			obj.clear() ;
			
			return obj ;
		} catch(CloneNotSupportedException e){
			return new ResultObject();
		}
	}
	
	
}
