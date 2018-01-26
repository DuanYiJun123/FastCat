package com.qqduan.FastCat.exception;

public class ContextXmlError extends MySystemException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8567626807365390674L;

	public ContextXmlError(String msg) {
		super(msg);
	}

	public ContextXmlError(Throwable throwable) {
		super(throwable);
	}

	public ContextXmlError(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
