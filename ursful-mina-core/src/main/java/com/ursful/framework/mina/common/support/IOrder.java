package com.ursful.framework.mina.common.support;

/**
 * <p>项目名称: ursful-mina </p>
 * <p>描述:  </p>
 * <p>创建时间:2021/2/26 8:14 </p>
 * <p>公司信息:厦门海迈科技股份有限公司&gt;研发中心&gt;框架组</p>
 *
 * @author huangyonghua, jlis@qq.com
 */
public interface IOrder {
    default int order() {
        return 0;
    }
}
