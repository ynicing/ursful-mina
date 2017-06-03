# urs-database
light operation database, CRED and statistic  @ursful.com

IBaseDao<TestModel> baseDao = new BaseDaoImpl<TestModel>();

DatabaseInfo dbinfo = new DatabaseInfo();
dbinfo.setDriver("com.mysql.jdbc.Driver");
dbinfo.setPassword("root");
dbinfo.setUsername("root");
dbinfo.setUrl("jdbc:mysql://localhost:3306/urs");
dbinfo.setMaxActive(5);
dbinfo.setMinActive(2);

ConnectionManager.getManager().init(new BaseDataSource(dbinfo), dbinfo);

TestModel model = new TestModel();
model.setId(UUID.randomUUID().toString());
model.setCreateDate(new Date());
model.setName("Hello");

//insert
baseDao.save(model);
 
model.setName("me");

//update
baseDao.update(model);

model.setCreateDate(null);

//update with null
baseDao.update(model, true);

//delete
baseDao.delete(model);
        
IQuery<TestModel> query = new QueryDaoImpl<TestModel>()
                .table(TestModel.class)
                .createQuery("*");
//query
List<TestModel> testModels = baseDao.query(query);
