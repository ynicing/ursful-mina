package com.weitu.framework.component.orm.test;

import com.weitu.framework.component.orm.IBaseQuery;
import com.weitu.framework.component.orm.IMultiQuery;
import com.weitu.framework.component.orm.query.BaseQueryImpl;
import com.weitu.framework.component.orm.query.MultiQueryImpl;
import com.weitu.framework.component.orm.query.QueryUtils;
import com.weitu.framework.component.orm.support.*;
import com.weitu.songda.entity.Test;
import com.weitu.songda.entity.TestType;

import java.util.*;

/**
 * 类名：BaseFullQuery
 * 创建者：huangyonghua
 * 日期：2017-10-24 11:45
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class TestBaseFullQuery {


    public static void main(String[] args) {

        IBaseQuery query = new BaseQueryImpl();
        query.table(Test.class);
        query.where(Test.T_ID, 1, ExpressionType.CDT_More);
        query.where(new Terms()
                .or(new Express(Test.T_ID, 5, ExpressionType.CDT_MoreEqual))
                .or(new Express(Test.T_ID, 10, ExpressionType.CDT_LessEqual))
                .and(new Express(Test.T_NAME, "x", ExpressionType.CDT_LikeRight)));
        query.group(Test.T_ID);
        query.having(Test.T_ID, 1, ExpressionType.CDT_More);
        query.orderAsc(Test.T_NAME).orderDesc(Test.T_BALANCE);
        query.createQuery();
        QueryInfo info = QueryUtils.doQuery(query.queryHelper(), null);
        System.out.println(info.getSql());
        System.out.println(info.getValues());

        System.out.println("----------------------------");

        String test = "a";
        String testType = "b";
        IMultiQuery multiQuery = new MultiQueryImpl();
        multiQuery.table(test, Test.class).table(testType, TestType.class);
        multiQuery.where(new Column(test, Test.T_TYPE), new Column(testType, TestType.T_ID));
        multiQuery.where(new Column(test, Test.T_CREATE_DATE), new Date(),
                ExpressionType.CDT_LessEqual);
        multiQuery.where(new Condition()
                .or(new Expression(new Column(test, Test.T_ID), 5, ExpressionType.CDT_More)));
        multiQuery.createQuery(Test.class,
                new Column(test, Test.T_ID),
                new Column(test, Test.T_NAME),
                new Column(test, Test.T_BALANCE),
                new Column(test, Expression.EXPRESSION_ALL));
        info = QueryUtils.doQuery(multiQuery.queryHelper(), null);
        System.out.println(info.getSql());
        System.out.println(info.getValues());

        System.out.println("----------------------------");

        test = "a";
        testType = "b";
        multiQuery = new MultiQueryImpl();
        multiQuery.table(test, Test.class);
        multiQuery.join(new Join(testType, TestType.class)
                .on(new Column(test, Test.T_TYPE), new Column(testType, TestType.T_ID))
                .on(new Column(testType, TestType.T_ID), 3, ExpressionType.CDT_MoreEqual));
        multiQuery.where(new Column(test, Test.T_TYPE), new Column(testType, TestType.T_ID));
        multiQuery.where(new Column(test, Test.T_CREATE_DATE), new Date(),
                ExpressionType.CDT_LessEqual);
        multiQuery.where(new Condition()
                .or(new Expression(new Column(test, Test.T_ID), 5, ExpressionType.CDT_More)));
        multiQuery.createQuery(Map.class,
                new Column(test, Test.T_ID),
                new Column(test, Expression.EXPRESSION_ALL));
        info = QueryUtils.doQuery(multiQuery.queryHelper(), null);
        System.out.println(info.getSql());
        System.out.println(info.getValues());

        System.out.println("----------------------------");

        test = "a";
        testType = "b";
        List list = new ArrayList();//实现Collection类都可以。如ArrayList,HashSet...
        list.add(1);
        list.add(2);
        multiQuery = new MultiQueryImpl();
        multiQuery.table(test, Test.class);
        multiQuery.join(new Join(testType, TestType.class)
                .on(new Column(test, Test.T_TYPE), new Column(testType, TestType.T_ID))
                .on(new Column(testType, TestType.T_ID), 3, ExpressionType.CDT_MoreEqual));
        multiQuery.where(new Column(test, Test.T_TYPE), new Column(testType, TestType.T_ID));
        multiQuery.where(new Column(test, Test.T_ID), list, ExpressionType.CDT_In);

        info = QueryUtils.doQuery(multiQuery.queryHelper(), null);
        System.out.println(info.getSql());
        System.out.println(info.getValues());


    }
}