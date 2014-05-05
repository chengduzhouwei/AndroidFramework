package test.service;

public interface IMessageDefine {

	public static final int MSG_TASK_START				= 0x00ff0001 ;
	public static final int MSG_TASK_END				= MSG_TASK_START + 1 ;
	
	public static final int MSG_TASK_LOGIN_SUCCESS		= MSG_TASK_END + 1 ;
	public static final int MSG_TASK_LOGIN_FAILED		= MSG_TASK_LOGIN_SUCCESS + 1 ;
	
	
}
