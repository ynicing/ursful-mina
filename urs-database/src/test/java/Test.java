import com.ursful.framework.database.*;
import com.ursful.framework.database.base.model.TestModel;
import com.ursful.framework.database.base.model.ViewModel;
import com.ursful.framework.database.query.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by ynice on 6/3/17.
 */
public class Test {

    /*
    create table t_test_model(
        ID varchar(36) primary key,
        NAME varchar(255),
        CREATE_DATE datetime,
        NUMBER int(8));

     */

    private static IBaseDao<TestModel> baseDao = new BaseDaoImpl<TestModel>();

    static {
        DatabaseInfo dbinfo = new DatabaseInfo();
        dbinfo.setDriver("com.mysql.jdbc.Driver");
        dbinfo.setPassword("root");
        dbinfo.setUsername("root");
        dbinfo.setUrl("jdbc:mysql://localhost:3306/urs_bak");
        dbinfo.setMaxActive(5);
        dbinfo.setMinActive(2);
        ConnectionManager.getManager().init(new BaseDataSource(dbinfo), dbinfo);
    }

    public static void main(String[] args) throws Exception{


        query();

        //插入
        TestModel testModel = new TestModel();
        testModel.setId(UUID.randomUUID().toString());
        testModel.setCreateDate(new Date());
        testModel.setName("Hello");
        testModel.setNumber(10);

        baseDao.save(testModel);

        query();

        testModel.setName("me");

        baseDao.update(testModel);

        query();

        testModel.setCreateDate(null);


        baseDao.update(testModel, true);

        query();


        IMultiQueryDao<TestModel> dao = new MultiQueryDaoImpl<TestModel>();
        dao.createAliasTable("t", TestModel.class)
        .createQuery(TestModel.class, new Column("sum", "t", TestModel.T_NUMBER, TestModel.T_NUMBER));
        List<TestModel> m = baseDao.query(dao);
        System.out.println(m.get(0).getNumber());

        baseDao.delete(testModel);

        query();

    }

    private static void query() throws Exception{

        IQuery<TestModel> query = new QueryDaoImpl<TestModel>()
                .table(TestModel.class)
                .createQuery("*");
        List<TestModel> testModels = baseDao.query(query);
        System.out.println();
        System.out.println("-------query----------" + baseDao.queryCount(query.createCount()));
        for(TestModel testModel : testModels){
            System.out.println(testModel.getId() + ">" + testModel.getName() + ">" + testModel.getCreateDate());
        }
        System.out.println("---------end----------");
        System.out.println();
    }
}
