package com.weitu.framework.component.orm;

import com.weitu.framework.component.orm.support.ScriptType;

/**
 * 类名：ISQLScript
 * 创建者：huangyonghua
 * 日期：2017-10-21 21:43
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface ISQLScript {
    ScriptType type();
    String table();
    String [] columns();
}
