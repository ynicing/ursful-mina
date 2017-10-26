package com.weitu.framework.component.orm.test.error;

import com.weitu.framework.core.error.ErrorCode;

/**
 * 类名：TestErrorCode
 * 创建者：huangyonghua
 * 日期：2017-10-22 17:23
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public enum  TestErrorCode implements ErrorCode{

    TEST_ERROR(1, "test.error", "错误了");

    TestErrorCode(Integer code, String message, String description){
        this.code = code;
        this.message = message;
    }

    private Integer code;
    private String message;

    @Override
    public Integer code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }
}
