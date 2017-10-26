package com.weitu.framework.component.orm.test.model;

import com.weitu.songda.entity.Test;

/**
 * 类名：ExtTest
 * 创建者：huangyonghua
 * 日期：2017-10-21 16:16
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */

public class ExtTest extends Test{

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
