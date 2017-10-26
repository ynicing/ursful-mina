package com.weitu.framework.component.orm.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名：QueryHelper
 * 创建者：huangyonghua
 * 日期：2017-10-20 23:35
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class QueryHelper {


    private boolean distinct = false;



    private Class<?> table;
    private List<String> aliasList;
    private Map<String, Class<?>> aliasTable;
    private List<Join> joins;


    private List<Condition> conditions;
    private List<Condition> havings;
    private List<Column> groups;
    private List<Order> orders;


    private Class<?> returnClass;//if single this returnClass is class.
    private List<Column> returnColumns;//return...


    public Class<?> getTable() {
        return table;
    }

    public void setTable(Class<?> table) {
        this.table = table;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public List<String> getAliasList() {
        if(aliasList == null){
            aliasList = new ArrayList<String>();
        }
        return aliasList;
    }

    public void setAliasList(List<String> aliasList) {
        this.aliasList = aliasList;
    }

    public Map<String, Class<?>> getAliasTable() {
        return aliasTable;
    }

    public void setAliasTable(Map<String, Class<?>> aliasTable) {
        if(aliasTable == null){
            aliasTable = new HashMap<String, Class<?>>();
        }
        this.aliasTable = aliasTable;
    }

    public List<Join> getJoins() {
        if(joins == null){
            joins = new ArrayList<Join>();
        }
        return joins;
    }

    public void setJoins(List<Join> joins) {
        if(joins == null){
            joins = new ArrayList<Join>();
        }
        this.joins = joins;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public List<Condition> getHavings() {
        if(havings == null){
            havings = new ArrayList<Condition>();
        }
        return havings;
    }

    public void setHavings(List<Condition> havings) {
        this.havings = havings;
    }

    public List<Column> getGroups() {
        if(groups == null){
            groups = new ArrayList<Column>();
        }
        return groups;
    }

    public void setGroups(List<Column> groups) {
        this.groups = groups;
    }

    public List<Order> getOrders() {
        if(orders == null){
            orders = new ArrayList<Order>();
        }
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(Class<?> returnClass) {
        this.returnClass = returnClass;
    }

    public List<Column> getReturnColumns() {
        if(returnColumns == null){
            returnColumns = new ArrayList<Column>();
        }
        return returnColumns;
    }

    public void setReturnColumns(List<Column> returnColumns) {
        this.returnColumns = returnColumns;
    }
//    private List<Condition> conditions = new ArrayList<Condition>();
//    private List<Condition> havings = new ArrayList<Condition>();
//    private List<Column> groups = new ArrayList<Column>();
//    private List<Order> orders = new ArrayList<Order>();

}
