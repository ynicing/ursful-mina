package com.weitu.framework.component.orm.test.serviceful;

import com.weitu.framework.component.orm.test.error.TestErrorCode;
import com.weitu.framework.web.exception.ServiceException;
import com.weitu.songda.entity.Test;
import com.weitu.songda.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类名：TestServiceful
 * 创建者：huangyonghua
 * 日期：2017-10-22 17:14
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
@Service("testServiceful")
public class TestServiceful {

    @Autowired
    private ITestService testService;
    //@Transactional
    public Test get(Integer id){
        try{
            int key = 1/id;
        }catch (Exception e){

            throw new ServiceException(TestServiceful.class, TestErrorCode.TEST_ERROR, e.getMessage());
        }
        return testService.get(id);
    }

    public static void main(String[] args)  throws Exception{

        String name = TestServiceful.class.getName();
        String [] names = name.split("[.]");
        System.out.println(names.length);
        System.out.println(TestServiceful.class.getName());

        byte [] data = TestServiceful.class.getName().getBytes("UTF-8");

        System.out.println(data.length);
    }

}
