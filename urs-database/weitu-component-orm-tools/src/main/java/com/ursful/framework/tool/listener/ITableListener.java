package com.ursful.framework.tool.listener;

import java.util.List;

/**
 * Created by ynice on 3/27/17.
 */
public interface ITableListener {
    void select(String db, List<String> tables);
}
