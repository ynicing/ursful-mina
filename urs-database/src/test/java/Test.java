import com.ursful.framework.database.*;
import com.ursful.framework.database.base.model.TestModel;
import com.ursful.framework.database.base.model.ViewModel;
import com.ursful.framework.database.query.Column;
import com.ursful.framework.database.query.IQuery;
import com.ursful.framework.database.query.MultiQueryDaoImpl;
import com.ursful.framework.database.query.QueryDaoImpl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by ynice on 6/3/17.
 */
public class Test {

    public static void main(String[] args) throws Exception{

        DatabaseInfo dbinfo = new DatabaseInfo();
        dbinfo.setDriver("com.mysql.jdbc.Driver");
        dbinfo.setPassword("root");
        dbinfo.setUsername("root");
        dbinfo.setUrl("jdbc:mysql://localhost:3306/urs_bak");
        dbinfo.setMaxActive(5);
        dbinfo.setMinActive(2);

        ConnectionManager.getManager().init(new BaseDataSource(dbinfo), dbinfo);

        query();

        IBaseDao<TestModel> baseDao = new BaseDaoImpl<TestModel>();

        //插入
        TestModel insert = new TestModel();
        insert.setId(UUID.randomUUID().toString());
        insert.setCreateDate(new Date());
        insert.setName("Hello");
        baseDao.save(insert);

        query();

        insert.setName("me");

        baseDao.update(insert);

        query();

        insert.setCreateDate(null);

        baseDao.update(insert, true);

        query();

        baseDao.delete(insert);

        query();



    }

    private static void query() throws Exception{
        IBaseDao<TestModel> baseDao = new BaseDaoImpl<TestModel>();
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
