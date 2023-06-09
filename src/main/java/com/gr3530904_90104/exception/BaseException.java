package com.gr3530904_90104.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseException extends RuntimeException{

    public BaseException(String msg, Throwable t) {
        super(msg, t);
        log.error(msg, t);
    }

    public BaseException(String msg) {
        super(msg);
        log.error(msg);
    }

}
