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
public class Condition {

    private List<Expression> orExpressions = new ArrayList<Expression>();

    private List<Expression> andExpressions = new ArrayList<Expression>();

    public List<Expression> getOrExpressions() {
        return orExpressions;
    }

    public void setOrExpressions(List<Expression> orExpressions) {
        this.orExpressions = orExpressions;
    }

    public List<Expression> getAndExpressions() {
        return andExpressions;
    }

    public void setAndExpressions(List<Expression> andExpressions) {
        this.andExpressions = andExpressions;
    }

    public Condition or(Expression expression){
        orExpressions.add(expression);
        return this;
    }

    public Condition and(Expression expression){
        andExpressions.add(expression);
        return this;
    }

}
