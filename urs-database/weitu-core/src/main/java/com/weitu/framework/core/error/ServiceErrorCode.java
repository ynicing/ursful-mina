package com.weitu.framework.core.error;

/**
 * 枚举：SystemErrorCode
 * 创建者：huangyonghua
 * 日期：2017-10-22 11:47
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */

public enum ServiceErrorCode implements ErrorCode{

    ERROR_CODE_NOT_DEFINED(-1,"error.code.not.defined","未定义异常"),
    ERROR_SUCCESSFUL(0,"service.successful","响应成功"),
    ERROR_SYSTEM_ERROR(1, "service.not.found", "服务不存在"),
    ERROR_SERVICE_NOT_FOUND(2, "service.not.found", "服务不存在"),
    ERROR_METHOD_NOT_FOUND(3, "method.not.found","方法不存在"),
    ERROR_NO_PERMISSION(4,"no.permission","无权访问"),
    ERROR_INSTANCE_ERROR(5,"instance.error","实例化错误"),
    ERROR_ILLEGAL_ACCESS(6,"illegal.access","非法访问"),
    ERROR_PARA_ERROR(7,"parameter.error","参数错误"),
    ERROR_ILLEGAL_ARGUMENT(8,"illegal.argument","非法参数"),
    ERROR_INVOCATION_TARGET(9,"invocation.target","反射目标错误"),
    ERROR_NO_SUCH_METHOD(10,"no.such.method","没有该方法"),
    ERROR_SECURITY(11,"security","安全异常"),
    ERROR_INSTANTIATION(12,"instance","实例错误"),
    ERROR_DATE_FORMAT_ERROR(13,"date.format.error","日期格式化错误"),
    ERROR_NUMBER_FORMAT_ERROR(14,"number.format.error","数字格式化错误"),
    ERROR_CLASS_NOT_FOUND(15,"class.not.found","类找不到了"),
    ERROR_LESS_THAN(16,"less.than","小于"),
    ERROR_MORE_THAN(17,"more.than","大于"),
    ERROR_NO_PATTERN(18,"no.pattern","大于"),
    ERROR_NO_PATTERN_LENGTH(19,"no.pattern.length","长度不符"),
    ERROR_CREATE_BEAN(20,"create.bean.error","创建Bean失败"),
    ERROR_BEAN_NOT_FOUND(21,"bean.not.found","找不到Bean"),
    ERROR_RPOXY(22,"proxy.error","代理错误"),
    ERROR_QUERY_TABLE(23,"query.table","查询表错误"),
    ERROR_TIME_OUT(24,"time.out","超时"),
    ERROR_OTHER(25,"other","其他错误");

    private Integer code;
    private String message;

    ServiceErrorCode(Integer code, String message, String description) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer code() {
        return this.code;
    }

    @Override
    public String message() {
        return this.message;
    }

}
