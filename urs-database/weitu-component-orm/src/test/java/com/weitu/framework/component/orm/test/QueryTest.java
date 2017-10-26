package com.weitu.framework.component.orm.test;

import com.weitu.framework.component.orm.query.BaseQueryImpl;
import com.weitu.framework.component.orm.support.Condition;
import com.weitu.framework.component.orm.test.service.TestServiceImpl;
import com.weitu.framework.component.orm.IBaseQuery;
import com.weitu.framework.component.orm.support.Express;
import com.weitu.framework.component.orm.support.ExpressionType;
import com.weitu.framework.component.orm.support.Terms;
import com.weitu.songda.entity.Test;
import com.weitu.songda.service.ITestService;

import java.util.List;

/**
 * 类名：QueryTest
 * 创建者：huangyonghua
 * 日期：2017-10-19 19:56
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class QueryTest {

    public static void main(String[] args) throws Exception{
        Main.init();
        ITestService baseDao = new TestServiceImpl();


        IBaseQuery query = new BaseQueryImpl()
                .table(Test.class)
                .where(Test.T_NAME, "a", ExpressionType.CDT_LikeRight)
                .where(new Terms().
                        or(new Express(Test.T_BALANCE, 3, ExpressionType.CDT_MoreEqual)).
                        and(new Express(Test.T_BALANCE, 3, ExpressionType.CDT_MoreEqual)))
                .orderAsc(Test.T_ID)
                .createQuery();
                //.createQuery(Test.T_NAME);
        List<Test> testList = baseDao.query(query);

        System.out.println(testList);

    }
}
