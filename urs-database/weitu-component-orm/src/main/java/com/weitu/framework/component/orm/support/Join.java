package com.weitu.framework.component.orm.support;

import com.weitu.framework.core.util.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 类名：Join
 * 创建者：huangyonghua
 * 日期：2017-10-19 9:20
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Join {

    private JoinType type;
    private Class<?> clazz;
    private List<Condition> conditions;
    private String alias;

    public Join(String alias, Class<?> clazz){//table 默认内连接
        this(alias, clazz, JoinType.LEFT_JOIN);
    }

    public Join(String alias, Class<?> clazz, JoinType type){
        this.type = type;
        this.alias = alias;
        this.clazz = clazz;
        this.conditions = new ArrayList<Condition>();
    }

    public Join on(Column column, Object value, ExpressionType expressionType){
        conditions.add(new Condition().and(new Expression(column, value, expressionType)));
        return this;
    }

    public Join on(String columnName, Object value, ExpressionType expressionType){
        conditions.add(new Condition().and(new Expression(new Column(this.alias, columnName), value, expressionType)));
        return this;
    }

    public Join on(String columnName, Column column){
        conditions.add(new Condition().and(new Expression(new Column(this.alias, columnName), column, ExpressionType.CDT_Equal)));
        return this;
    }

    public Join on(Column thisColumn, Column column){
        conditions.add(new Condition().and(new Expression(thisColumn, column, ExpressionType.CDT_Equal)));
        return this;
    }

    public Join on(Condition condition){
        conditions.add(condition);
        return this;
    }

    public JoinType getType() {
        return type;
    }

    public void setType(JoinType type) {
        this.type = type;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
