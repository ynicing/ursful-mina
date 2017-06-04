# urs-database

轻量级ORM，详细查看src/test/Test.java

light orm database, CRED and statistic  @ursful.com<br/>

<pre>

IBaseDao<TestModel> baseDao = new BaseDaoImpl<TestModel>();
IBaseSQL baseSQL = new BaseSQLImpl();


TestModel model = new TestModel();
model.setId(UUID.randomUUID().toString());
model.setCreateDate(new Date());
model.setName("Hello");
model.setNumber(10);

//保存
baseDao.save(model);
 
model.setName("me");

//更新<br/>
baseDao.update(model);

model.setCreateDate(null);

//更新Null
baseDao.update(model, true);

//删除<br/>
baseDao.delete(model);
        
IQuery<TestModel> query = new QueryDaoImpl<TestModel>()
.table(TestModel.class)
.createQuery("*");

//查询<br/>
List<TestModel> testModels = baseDao.query(query);

//函数使用,如 sum

IQuery<TestModel> query = new QueryDaoImpl<TestModel>()
.table(TestModel.class)
.createQuery(TestModel.class, new Column("sum", null, TestModel.T_NUMBER, "number"));
TestModel result = baseDao.get(query);
System.out.println(result.getNumber());


Long res = (Long)baseSQL.queryObject("select count(*) from t_test_model");

System.out.println("Result : " + res);

</pre>

使用方法
----------------------------------
1.注入数据源

ConnectionManager.getManager().init(new BaseDataSource(dbinfo), dbinfo);

2.编写model，Java Field字段与数据库字段对应。

<pre>
@RdTable(name = "T_TEST_MODEL")
public class TestModel implements Serializable{

    @RdColumn(name = "ID", unique = true)
    private String id;

    @RdColumn(name = "NAME")
    private String name;

}
</pre>
