package com.qqduan.FastCat.exception;

public class ErrorCodeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 756078788178143604L;

	public ErrorCodeException(String msg) {
		super(msg);
	}

	public ErrorCodeException(Throwable throwable) {
		super(throwable);
	}

	public ErrorCodeException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
