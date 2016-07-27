package com.seckill.exception;

public class SeckillCloseException extends SeckillException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8947510950764586569L;

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeckillCloseException(String message) {
		super(message);
	}

}
