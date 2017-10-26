package com.weitu.framework.component.orm.test;

import com.weitu.framework.component.orm.query.BaseQueryImpl;
import com.weitu.framework.component.orm.test.service.TestTypeServiceImpl;
import com.weitu.framework.component.orm.IBaseService;
import com.weitu.framework.component.orm.IBaseQuery;
import com.weitu.songda.entity.TestType;
import com.weitu.songda.service.ITestTypeService;

import java.util.List;

/**
 * 类名：TestTypeServiceTest
 * 创建者：huangyonghua
 * 日期：2017-10-21 12:45
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class TestTypeTest {

    public static void main(String[] args) throws Exception{

        Main.init();

        ITestTypeService baseDao = new TestTypeServiceImpl();

        IBaseQuery baseQuery = new BaseQueryImpl();
        baseQuery.table(TestType.class).createQuery();

        List<TestType> list = baseDao.query(baseQuery);

        System.out.println(list);

//        TestType testType = new TestType();
//        testType.setName("xxxx");
//        baseDao.save(testType);


    }
}
