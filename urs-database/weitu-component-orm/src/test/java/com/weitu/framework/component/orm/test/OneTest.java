package com.weitu.framework.component.orm.test;

import com.weitu.framework.component.orm.query.BaseQueryImpl;
import com.weitu.framework.component.orm.query.MultiQueryImpl;
import com.weitu.framework.component.orm.test.service.TestServiceImpl;
import com.weitu.framework.component.orm.IBaseQuery;
import com.weitu.framework.component.orm.IMultiQuery;
import com.weitu.framework.component.orm.IQuery;
import com.weitu.framework.component.orm.support.Column;
import com.weitu.framework.component.orm.support.ExpressionType;
import com.weitu.songda.entity.Test;
import com.weitu.songda.service.ITestService;

/**
 * 类名：OneTest
 * 创建者：huangyonghua
 * 日期：2017-10-21 19:38
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class OneTest {

    public static void main(String[] args) {
        Main.init();


        ITestService testService = new TestServiceImpl();
        Test test = testService.get(new Integer(9));
        System.out.println(test.getId() + "==" + test.getName());

        System.out.println(Test.class);

         test = testService.get(9);
        System.out.println(test.getId() + "==" + test.getName());

        Test t = new Test();
        t.setType(5);
        t.setBalance(0.0);
        test = testService.get(t);
        System.out.println(test.getId() + "==" + test.getName());

        IBaseQuery query = new BaseQueryImpl();
        query.table(Test.class).createQuery();
        test = testService.get(query);

        System.out.println(test.getId());

        IMultiQuery multiQuery = new MultiQueryImpl();
        multiQuery.table("a", Test.class);
        multiQuery.where(new Column("a", Test.T_ID), 3, ExpressionType.CDT_More);
        multiQuery.createQuery(Test.class);
        test = testService.get(multiQuery);
        System.out.println(test.getId());

    }
}
