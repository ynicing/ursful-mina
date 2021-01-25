package com.ursful.framework.mina.server.client;

/**
 * <p>项目名称: ursful-mina </p>
 * <p>描述:  </p>
 * <p>创建时间:2021/1/25 14:36 </p>
 * <p>公司信息:厦门海迈科技股份有限公司&gt;研发中心&gt;框架组</p>
 *
 * @author huangyonghua, jlis@qq.com
 */
public interface IClientManager {
    void register(Client client);
    void deregister(Client client);
}
