package com.touchbiz.chatgpt.common.exception;

public class RequestLimitException extends RuntimeException {
    public RequestLimitException() {
        super("HTTP请求超过了限定的访问次数");
    }

    public RequestLimitException(String message) {
        super(message);
    }
}
