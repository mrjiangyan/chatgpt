package com.touchbiz.chatgpt.common.exception;

import java.io.Serial;

/**
 * @Description: jeecg-boot自定义401异常
 * @author: jeecg-boot
 */
public class JeecgBoot401Exception extends RuntimeException {
	@Serial
    private static final long serialVersionUID = 1L;

	public JeecgBoot401Exception(String message){
		super(message);
	}

	public JeecgBoot401Exception(Throwable cause)
	{
		super(cause);
	}

	public JeecgBoot401Exception(String message, Throwable cause)
	{
		super(message,cause);
	}
}
