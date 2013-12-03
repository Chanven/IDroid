package com.andy.demo.netapi.exception;

/**
 * 自定义错误异常
 * @author ivankuo
 *
 */
@SuppressWarnings("serial")
public class XResponseException extends Exception{
	public static final int ERRCODE_LOCAL_ARGUMENT_JSON_TRANS = -1;
	public static final int ERRCODE_LOCAL_ARGUMENT_UNABLE = -2;
	public static final int ERRCODE_SERVER_RETURN_ERROR_MESSAGE = -3;
	public static final int ERRCODE_SERVER_RETURN_JSON_TRANS = -4;
	public static final int ERRCODE_SERVER_RETURN_EMPTY = -5;
	
	/*
	1	数据校验错误，即参数校验不通过，并在message中给出具体参数
	2	缺少必要的接口参数
	3	认证不通过，即传递的accessToken不存在，或已经过期，具体在message中给出，以使客户端能够区分是token不存在还是过期
	4	服务器拒绝服务，可能是接口访问次数太过频繁（服务端实现暂不考虑）
	5	其它参数可以后续增加，完善
	*/
	public static final int ERRORCODE_DATA_VERIFY_FAILED = 1;
	public static final int ERRORCODE_ARGUMENT_NOT_ENOUGH = 2;
	public static final int ERRORCODE_INVALID_ACCESSTOKEN = 3;
	public static final int ERRORCODE_SERVER_REFUSE = 4;
	public static final int ERRORCODE_OTHER = 5;
	public static final int ERRORCODE_SESSION_INAVAILABLE = 6;
	
	public static final String ERROR_MESSAGE_SERVER_RETURN_ERROR_HEAD = "Error_message: server say - ";
	
	private static final String ERROR_MESSAGE_LOCAL_ARGUMENT_JSON_TRANS = "Error_message: local argument json trans error";
	private static final String ERROR_MESSAGE_LOCAL_ARGUMENT_UNABLE = "Error_message: local argument unable";
	private static final String ERROR_MESSAGE_SERVER_RETURN_JSON_TRANS = "Error_message: server return json trans error";
	private static final String ERROR_MESSAGE_SERVER_RETURN_EMPTY = "Error_message: server return nothing";
	
	private int mErrCode;
	
	public XResponseException(int errCode) {
		super("X API err:" + errCode);
		mErrCode = errCode;
	}
	
	public XResponseException(String errMsg){
		super(errMsg);
		mErrCode = ERRORCODE_OTHER;
	}
	
	public XResponseException(int errCode, String errMsg) {
		super(errMsg);
		mErrCode = errCode;
	}
	
	public int getErrorCode() {
		return mErrCode;
	}
	
	@Override
	public String getMessage() {
		String returnString = super.getMessage();
		switch (mErrCode) {
		case ERRCODE_LOCAL_ARGUMENT_JSON_TRANS:
			returnString = ERROR_MESSAGE_LOCAL_ARGUMENT_JSON_TRANS;
			break;
		case ERRCODE_LOCAL_ARGUMENT_UNABLE:
			returnString = ERROR_MESSAGE_LOCAL_ARGUMENT_UNABLE;
			break;
		case ERRCODE_SERVER_RETURN_EMPTY:
			returnString = ERROR_MESSAGE_SERVER_RETURN_EMPTY;
			break;
		case ERRCODE_SERVER_RETURN_JSON_TRANS:
			returnString = ERROR_MESSAGE_SERVER_RETURN_JSON_TRANS;
			break;
		default:
			break;
		}
		return returnString;
	}
}
