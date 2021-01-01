package com.zpf.ainlp.exception;

/**
 * 业务异常expection
 */
public class BusinessException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	/**错误码**/
	private ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;

	}

	public BusinessException(ErrorCode errorCode, Throwable t) {
		this.errorCode = errorCode;
	}

	public BusinessException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public BusinessException(ErrorCode errorCode, String message, Throwable t) {
		super(message, t);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return  errorCode;
	}
}
