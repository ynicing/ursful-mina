package com.weitu.framework.component.orm.error;

/**
 * 类名：MessageUtils
 * 创建者：huangyonghua
 * 日期：2017-10-20 14:48
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class MessageUtils {

    public static final String ILLEGAL_ARGUMENT = "illegal.argument";
    public static final String ILLEGAL_ACCESS = "illegal.access";
    public static final String TABLE_COLUMN_NOT_ALLOW_NULL = "table.column.not.allow.null";

    //否则的话 整张表都清理了;至支持id
    public static final String TABLE_WHEN_DELETE_SHOULD_DO_WITH_PARAMETER = "when.delete.should.do.with.parameter";


    public static final String TABLE_NOT_FOUND = "table.not.found.in.class";
    public static final String TABLE_DELETE_WITHOUT_ID = "delete.without.id";
    public static final String TABLE_UPDATE_WITHOUT_ID = "update.without.id";
    public static final String TABLE_GET_WITHOUT_ID = "get.without.id";
    public static final String TABLE_SAVE_WITH_ANY_VALUE = "save.without.any.value";
}
