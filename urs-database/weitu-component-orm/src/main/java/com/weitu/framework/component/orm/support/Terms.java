package com.weitu.framework.component.orm.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：Condition
 * 创建者：huangyonghua
 * 日期：2017-10-19 18:11
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Terms {

    private List<Expression> orExpressions = new ArrayList<Expression>();
    private List<Expression> andExpressions = new ArrayList<Expression>();

    public Condition getCondition(){
        Condition condition = new Condition();
        condition.setAndExpressions(andExpressions);
        condition.setOrExpressions(orExpressions);
        return condition;
    }

    public Terms or(Express express){
        orExpressions.add(express.getExpression());
        return this;
    }

    public Terms and(Express express){
        andExpressions.add(express.getExpression());
        return this;
    }
}
