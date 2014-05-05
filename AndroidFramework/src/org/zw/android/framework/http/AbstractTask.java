package org.zw.android.framework.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.zw.android.framework.IExecuteAsyncTask.IAsyncTask;

/**
 * 
 * @author zhouwei
 *
 */
public abstract class AbstractTask extends IAsyncTask {
	
	public static enum MethodType {
		/** HTTP Get Method*/ GET, /** HTTP Post*/ POST
	}
	
	public static enum ResultType {
		/** byte[] */ BYTE_ARRAY,/** Stream */ TYPE_STREAM
	}
	
	protected static final int READ_TIME_OUT 				= 20000;
	protected static final int CONNECT_TIME_OUT 			= 5000;
	protected static final int BUFFER_SIZE 					= (1 << 10) * 8;
	protected static final int DEFAULT_MEM_BUFFER 			= 16 * 1024 ;
    
    private IObjectWrapper  objectWrapper ;
    private ResultType 		resultType ;
    private int 			readTimeout ;
    private int 			connectTimeout ;
    
    public AbstractTask(){
    	setReadTimeout(READ_TIME_OUT) ;
    	setConnectTimeout(CONNECT_TIME_OUT);
    }
    
	public IObjectWrapper getObjectWrapper() {
		return objectWrapper;
	}

	public void setObjectWrapper(IObjectWrapper objectWrapper) {
		this.objectWrapper = objectWrapper;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout < 1000 ? READ_TIME_OUT : readTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout < 1000 ? CONNECT_TIME_OUT : connectTimeout;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	/** read input Stream to mem*/
	protected static final byte[] readInputStream(InputStream input) throws Exception {
		
        byte[] buffer ;
        ByteArrayOutputStream out = null;
        try {
            // temp buffer
            out = new ByteArrayOutputStream(DEFAULT_MEM_BUFFER);
            // read byte[]
            int read = -1;
            buffer = new byte[BUFFER_SIZE];
            
            // start read
            while ((read = input.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            
            // get byte[]
            return out.toByteArray();
        } catch (Exception e) {
            throw e;
        } finally {
        	
            // release resource
            if (out != null) {
                out.close();
                out = null;
            }
            
            //release input stream
            if(input != null){
                input.close();
                input = null ;
            }
            
            // release temp buffer
            buffer = null ;
        }
    }
}
