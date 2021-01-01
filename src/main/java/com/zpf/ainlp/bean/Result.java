package com.zpf.ainlp.bean;


import com.zpf.ainlp.exception.ErrorCode;

public class Result {

	public int code;
	public String message;

	public Result() {
		this.code = ErrorCode.SUCCESS.getCode();
		this.message = ErrorCode.SUCCESS.getMessage();
	}

	public Result(int code) {
		this.code = code;
	}

	public Result(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
