package com.touchbiz.chatgpt.common.exception;

import com.touchbiz.chatgpt.common.dto.Result;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.dao.DuplicateKeyException;
//import org.springframework.data.redis.connection.PoolException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 异常处理器
 * 
 * @Author scott
 * @Date 2019
 */
@RestControllerAdvice
@Slf4j
public class JeecgBootExceptionHandler {

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(JeecgBootException.class)
	public Result<?> handleJeecgBootException(JeecgBootException e){
		log.error(e.getMessage(), e);
		return Result.error(e.getMessage());
	}

	/**
	 * 处理自定义微服务异常
	 */
//	@ExceptionHandler(JeecgCloudException.class)
//	public Result<?> handleJeecgCloudException(JeecgCloudException e){
//		log.error(e.getMessage(), e);
//		return Result.error(e.getMessage());
//	}

	/**
	 * 处理自定义异常
	 */
//	@ExceptionHandler(JeecgBoot401Exception.class)
//	@ResponseStatus(HttpStatus.UNAUTHORIZED)
//	public Result<?> handleJeecgBoot401Exception(JeecgBoot401Exception e){
//		log.error(e.getMessage(), e);
//		return new Result(401,e.getMessage());
//	}


//
//	@ExceptionHandler(DuplicateKeyException.class)
//	public Result<?> handleDuplicateKeyException(DuplicateKeyException e){
//		log.error(e.getMessage(), e);
//		return Result.error("数据库中已存在该记录");
//	}



	@ExceptionHandler(Exception.class)
	public Result<?> handleException(Exception e){
		log.error(e.getMessage(), e);
		return Result.error("操作失败，"+e.getMessage());
	}

	@ExceptionHandler({WebExchangeBindException.class})
	public Result<?> checkRequest(WebExchangeBindException e) {
		log.error(e.getMessage());
		BindingResult bindingResult = e.getBindingResult();
		final StringBuilder sb = new StringBuilder();

		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			sb.append(fieldError.getDefaultMessage()).append(";");
		}

		log.warn("WebExchangeBindException", e);
		return Result.error("参数错误:" +  sb.substring(0, sb.length() - 1));
	}

	@ExceptionHandler({IllegalArgumentException.class})
	public Result<?> illegalArgumentExceptionHandler(IllegalArgumentException e) {
		log.warn("Illegal argument exception", e);
		return Result.error("非法参数错误:" +  e.getMessage());
	}
	/**
	 * @Author 政辉
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Result<?> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
		StringBuilder sb = new StringBuilder();
		sb.append("不支持");
		sb.append(e.getMethod());
		sb.append("请求方法，");
		sb.append("支持以下");
		String [] methods = e.getSupportedMethods();
		if(methods!=null){
			for(String str:methods){
				sb.append(str);
				sb.append("、");
			}
		}
		log.error(sb.toString(), e);
		//return Result.error("没有权限，请联系管理员授权");
		return Result.error(405,sb.toString());
	}
	
	 /** 
	  * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException 
	  */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
    	log.error(e.getMessage(), e);
        return Result.error("文件大小超出10MB限制, 请压缩或降低文件质量! ");
    }

//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public Result<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
//    	log.error(e.getMessage(), e);
//    	//【issues/3624】数据库执行异常handleDataIntegrityViolationException提示有误 #3624
//        return Result.error("执行数据库异常,违反了完整性例如：违反惟一约束、违反非空限制、字段内容超出长度等");
//    }
//
//    @ExceptionHandler(PoolException.class)
//    public Result<?> handlePoolException(PoolException e) {
//    	log.error(e.getMessage(), e);
//        return Result.error("Redis 连接异常!");
//    }

}
