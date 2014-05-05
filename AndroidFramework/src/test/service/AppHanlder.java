package test.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class AppHanlder extends Handler implements IMessageDefine {

	static final String TAG = "BaseHandler";

	protected Context mContext;

	public AppHanlder(Context context) {
		mContext = context;
	}

	public void sendMessage(int what) {
		obtainMessage(what).sendToTarget();
	}

	public void sendMessage(int what, Object obj) {
		obtainMessage(what, obj).sendToTarget();
	}
	
	public void showText(String text){
		if(mContext != null){
			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show() ;
		}
	}

	@Override
	public final void handleMessage(Message msg) {

		// for debug
		
		switch (msg.what) {

		case MSG_TASK_START:
			disposeMessage(msg);
			break;

		case MSG_TASK_END:
			disposeMessage(msg);
			break;

		default:
			disposeMessage(msg);
			break;
		}
	}

	/** sub class override the method */
	public void disposeMessage(Message msg) {

	}

}
