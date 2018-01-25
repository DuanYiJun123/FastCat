package com.qqduan.FastCat.exception;

public class MySystemException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3359560961706244639L;

	public MySystemException(String msg) {
		super(msg);
	}

	public MySystemException(Throwable throwable) {
		super(throwable);
	}

	public MySystemException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
