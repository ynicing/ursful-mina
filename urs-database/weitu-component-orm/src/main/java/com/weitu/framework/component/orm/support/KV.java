package com.weitu.framework.component.orm.support;

/**
 * 类名：KeyValue
 * 创建者：huangyonghua
 * 日期：2017-10-21 21:58
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class KV {

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString(){
        return "{" + key + ":" + value + "}";
    }
}
