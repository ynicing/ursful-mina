package com.weitu.framework.component.orm.test;

import com.weitu.framework.component.orm.test.serviceful.TestServiceful;
import com.weitu.framework.core.error.ServiceErrorCode;
import com.weitu.framework.web.exception.ServiceException;
import com.weitu.framework.web.http.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 类名：TestController
 * 创建者：huangyonghua
 * 日期：2017-10-22 17:08
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestServiceful testServiceful;

    @RequestMapping("/get")
    @ResponseBody
    public CommonResponse get(Integer id){
        CommonResponse commonResponse = null;
        try {
            Object data = testServiceful.get(id);
            commonResponse = new CommonResponse(data);
        }catch (ServiceException e){
            commonResponse = new CommonResponse(e);
        }catch (Exception e){
            commonResponse = new CommonResponse(ServiceErrorCode.ERROR_SYSTEM_ERROR);
        }
        return commonResponse;
    }

//    @RequestMapping("/page")
}
