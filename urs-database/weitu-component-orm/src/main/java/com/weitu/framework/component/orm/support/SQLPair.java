package com.weitu.framework.component.orm.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：SQLPair
 * 创建者：huangyonghua
 * 日期：2017-10-21 13:45
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class SQLPair {

    private String sql;
    private List<Pair> pair;

    public SQLPair(){}

    public SQLPair(String sql){
        this.sql = sql;
    }

    public SQLPair(String sql, Pair p){
        this.sql = sql;
        if(this.pair == null){
            this.pair = new ArrayList<Pair>();
        }
        this.pair.add(p);
    }

    public SQLPair(String sql, List<Pair> pair){
        this.sql = sql;
        this.pair = pair;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Pair> getPair() {
        return pair;
    }

    public void setPair(List<Pair> pair) {
        this.pair = pair;
    }
}
