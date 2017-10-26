package com.weitu.framework.component.orm.test;

import com.weitu.framework.component.orm.Manager;
import com.weitu.framework.component.orm.test.service.TestServiceImpl;
import com.weitu.framework.component.orm.support.*;
import com.weitu.songda.entity.Test;
import com.weitu.songda.service.ITestService;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * 类名：Test
 * 创建者：huangyonghua
 * 日期：2017-10-17 14:17
 * 版权：厦门维途信息技术有限公司 Copyright(c) 2017
 * 说明：[类说明必填内容，请修改]
 */
public class Main {

    public static void init(){
        Manager.getManager().init(createDataSource());
    }

    public static void main(String[] args) throws Exception{

        init();

        ITestService testService = new TestServiceImpl();

        System.out.println("数量：" + testService.size());

        System.out.println();
        System.out.println("开始查询：");
        List<Test> testList = testService.list();

        for(Test test : testList){
            System.out.println(test.getId() + " == " + test.getCreateDate());
        }

        System.out.println();
        System.out.println("开始查询:1-2,(注：0 为第一条)");
        testList = testService.list(1, 2);
        for(Test test : testList){
            System.out.println(test.getId() + " == " + test.getCreateDate());
        }


        System.out.println();
        System.out.println("开始查询:(5-8], ID降序排序，");
        testList = testService.list(
                new Terms()
                        .and(new Express(Test.T_ID, 5, ExpressionType.CDT_More))
                        .and(new Express(Test.T_ID, 8, ExpressionType.CDT_LessEqual)),
                new MultiOrder().desc(Test.T_ID));
        for(Test test : testList){
            System.out.println(test.getId() + " == " + test.getCreateDate());
        }


        System.out.println();
        System.out.println("Key-Vaue查询，");
        List<KV> stringList = testService.list(Test.T_ID, Test.T_NAME,
                new Terms()
                        .and(new Express(Test.T_ID, 5, ExpressionType.CDT_More))
                        .and(new Express(Test.T_ID, 8, ExpressionType.CDT_LessEqual)),
                new MultiOrder().desc(Test.T_ID));
        System.out.println(stringList);



        System.out.println();

        int id = 0;

        Test test = new Test();
        test.setName("x");
        test.setCreateDate(new Date());
        boolean save = testService.save(test);
        System.out.println("保存结果：" + save);

        id = test.getId();
        System.out.println("输入ID：" + id);


        test = testService.get(test);
        System.out.println("打印名称：" + test.getName());

        System.out.println();


        test = testService.get(id);
        System.out.println("打印名称：" + test.getName());

        test.setName(UUID.randomUUID().toString());

        System.out.println("给它一个随机名称：" + test.getName());
        testService.update(test);
        test = testService.get(id);
        System.out.println("打印名称：" + test.getName());

        test.setName(null);
        System.out.println("给它null值");
        testService.update(test);
        test = testService.get(id);
        System.out.println("打印名称：" + test.getName());

        test.setName(null);
        System.out.println("给它null值");
        testService.update(test, true);
        test = testService.get(id);
        System.out.println("打印名称：" + test.getName());


        testService.delete(id);
        System.out.println("删除了吧：" + id);

        test = testService.get(id);
        System.out.println("对象：" + test);


        /*
        Test test = new Test();
        test.setBalance(10.0);
        test.setType(3);
        test.setContent("xxx");
        test.setCreateDate(new Date());
        baseDao.save(test);

        Test temp = baseDao.get(test.getId(), Test.class);
        System.out.println("content after save : " + temp.getContent());

        test.setContent("x");
        baseDao.update(test);

        temp = baseDao.get(test.getId(), Test.class);
        System.out.println("content after update:" + temp.getContent());

        test.setContent(null);
        baseDao.update(test, true);

        temp = baseDao.get(test.getId(), Test.class);
        System.out.println("content after set null update :" + temp.getContent());
        */




        //baseDao.delete(test.getId(), Test.class);
        //System.out.println("delete : " + test.getId());


    }

    private static DataSource createDataSource(){

        DataSource dataSource = new DataSource() {
            public Connection getConnection() throws SQLException {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    //DriverManager.registerDriver(Driver.class);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                String url = "jdbc:mysql://127.0.0.1:3306/framework?characterEncoding=UTF-8";
                return DriverManager.getConnection(url, "root", "root");
            }

            public Connection getConnection(String username, String password) throws SQLException {
                return null;
            }

            public PrintWriter getLogWriter() throws SQLException {
                return null;
            }

            public void setLogWriter(PrintWriter out) throws SQLException {

            }

            public void setLoginTimeout(int seconds) throws SQLException {

            }

            public int getLoginTimeout() throws SQLException {
                return 0;
            }

            public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                return null;
            }

            public <T> T unwrap(Class<T> iface) throws SQLException {
                return null;
            }

            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                return false;
            }
        };
        return dataSource;
    }
}
