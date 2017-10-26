package com.weitu.framework.web.exception;


import com.weitu.framework.core.error.ErrorCode;
import com.weitu.framework.core.util.ClassUtils;

public class ServiceException extends RuntimeException {
	
	private static final long serialVersionUID = -8424446537917423207L;
	
    private Class<?> clazz;

    private ErrorCode errorCode;

    private String error;

    public Class<?> getClazz() {
        return clazz;
    }
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }


    public ServiceException(Class<?> clazz, ErrorCode errorCode, String description){
        super(description);
        this.error = ClassUtils.classToCode(clazz);
        this.clazz = clazz;
        this.errorCode = errorCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}