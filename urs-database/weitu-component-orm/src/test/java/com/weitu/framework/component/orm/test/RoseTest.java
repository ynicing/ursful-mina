package com.weitu.framework.component.orm.test;

import com.weitu.framework.component.orm.test.service.RoseServiceImpl;
import com.weitu.songda.entity.Rose;
import com.weitu.songda.service.IRoseService;

/**
 * 类名：RoseTest
 * 创建者：huangyonghua
 * 日期：2017-10-22 09:48
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class RoseTest {
    public static void main(String[] args) {
        Main.init();
        IRoseService roseService = new RoseServiceImpl();

        Rose rose = new Rose();
        rose.setName("xxx");
        roseService.save(rose);

//        rose.setId("a");
//        roseService.save(rose);
    }
}
