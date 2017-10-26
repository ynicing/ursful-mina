package com.weitu.framework.web.http;

import com.weitu.framework.core.error.ErrorCode;
import com.weitu.framework.core.error.ServiceErrorCode;
import com.weitu.framework.core.util.ClassUtils;
import com.weitu.framework.web.exception.ServiceException;

import java.util.Map;

public class CommonResponse {

	public Integer code;//code
    public String error;//detail class
    public String message;// test.message
    public String description = "";// e.getMessage
    public Object data;
    public Map<String, Object> attributes;

    public String toString(){
        return "code:" + code + "; message:" + message;
    }


    public CommonResponse(){}

	public CommonResponse(ErrorCode errorCode, String description){//code, message, info
		if(errorCode != null){
			this.message = errorCode.message();
            this.code = errorCode.code();
		}
		this.description = description;
	}

	public CommonResponse(Object data){
        if(data instanceof ServiceException){
            ServiceException se = (ServiceException)data;
            this.message = se.getErrorCode().message();
            this.code = se.getErrorCode().code();
            this.error = ClassUtils.classToCode(se.getClazz());
            this.data = null;
        }else {
            this.message = ServiceErrorCode.ERROR_SUCCESSFUL.message();
            this.code = ServiceErrorCode.ERROR_SUCCESSFUL.code();
            this.error = "success";
            this.data = data;
        }
	}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}