package com.ursful.framework.tool.load;

import com.ursful.framework.tool.db.Information;
import com.ursful.framework.tool.util.TextUtil;

import java.util.List;
import java.util.Properties;

/**
 * Created by ynice on 3/31/17.
 */
public class Loading implements Runnable{

    private InfiniteProgressPanel panel;
    private Information information;
    private String db;
    private List<String> tables;
    private String folderMenu;
    private Properties properties;

    public Loading(Information information, String db, List<String> tables,
                   String folderMenu, Properties properties, InfiniteProgressPanel panel){
        this.information = information;
        this.db = db;
        this.tables = tables;
        this.folderMenu = folderMenu;
        this.properties = properties;
        this.panel = panel;
    }

    @Override
    public void run() {
        TextUtil.create(information, db, tables, properties.getProperty("package.folder"), folderMenu,
                this.properties.getProperty("java.folder"),
                properties.getProperty("web.folder"), this.properties.getProperty("common.folder"));
        panel.stop();
    }
}
