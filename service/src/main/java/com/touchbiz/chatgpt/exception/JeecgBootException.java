package com.touchbiz.chatgpt.exception;

import java.io.Serial;

/**
 * @Description: jeecg-boot自定义异常
 * @author: jeecg-boot
 */
public class JeecgBootException extends RuntimeException {
	@Serial
    private static final long serialVersionUID = 1L;

	public JeecgBootException(String message){
		super(message);
	}

	public JeecgBootException(String message, Object ... params){
		super(String.format(message, params));
	}
	
	public JeecgBootException(Throwable cause)
	{
		super(cause);
	}
	
	public JeecgBootException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
