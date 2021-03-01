package com.ursful.framework.mina.common.support;

/**
 * 类名：IOrder
 * 创建者：huangyonghua
 * <p>创建时间:2021/2/26 8:14 </p>
 * 版权：ursful.com Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public interface IOrder {
    default int order() {
        return 0;
    }
}
