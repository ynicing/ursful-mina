package com.weitu.framework.component.orm.test;

import com.weitu.framework.component.orm.BaseServiceImpl;
import com.weitu.framework.component.orm.IBaseQuery;
import com.weitu.framework.component.orm.query.MultiQueryImpl;
import com.weitu.framework.component.orm.test.model.ExtTest;
import com.weitu.framework.component.orm.test.service.TestServiceImpl;
import com.weitu.framework.component.orm.IBaseService;
import com.weitu.framework.component.orm.IMultiQuery;
import com.weitu.framework.component.orm.support.*;
import com.weitu.songda.entity.Test;
import com.weitu.songda.entity.TestType;
import com.weitu.songda.service.ITestService;

import java.util.List;
import java.util.Map;

/**
 * 类名：QueryTest
 * 创建者：huangyonghua
 * 日期：2017-10-19 19:56
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class MultiQueryTest {

    public static void main(String[] args) throws Exception{

        Main.init();

        ITestService testService = new TestServiceImpl();

        System.out.println(testService.list());


       String a = "a";
        String b = "b";
//select * from T_test t;
        IMultiQuery query = new MultiQueryImpl()
                .table(a, Test.class)
                .table("b", TestType.class)
                .where(new Column("a", Test.T_TYPE), new Column("b", TestType.T_ID))
                .where(new Column("a", Test.T_ID), 5, ExpressionType.CDT_MoreEqual)
                .where(new Column("a", Test.T_ID), 6, ExpressionType.CDT_Less)
                //.orderDesc(new Column("a", Test.T_ID))
                .createQuery(Test.class, new Column("a", "*"), new Column("b", TestType.T_NAME, "typeName"));


        //System.out.println(query.queryHelper());

    }
}
