# urs-database
light operation database, CRED and statistic  @ursful.com<br/>

IBaseDao<TestModel> baseDao = new BaseDaoImpl<TestModel>();<br/>

DatabaseInfo dbinfo = new DatabaseInfo();<br/>
dbinfo.setDriver("com.mysql.jdbc.Driver");<br/>
dbinfo.setPassword("root");<br/>
dbinfo.setUsername("root");<br/>
dbinfo.setUrl("jdbc:mysql://localhost:3306/urs");<br/>
dbinfo.setMaxActive(5);<br/>
dbinfo.setMinActive(2);<br/>

ConnectionManager.getManager().init(new BaseDataSource(dbinfo), dbinfo);<br/>

TestModel model = new TestModel();<br/>
model.setId(UUID.randomUUID().toString());<br/>
model.setCreateDate(new Date());<br/>
model.setName("Hello");<br/>

//insert<br/>
baseDao.save(model);<br/>
 
model.setName("me");<br/>

//update<br/>
baseDao.update(model);<br/>

model.setCreateDate(null);<br/>

//update with null<br/>
baseDao.update(model, true);<br/>

//delete<br/>
baseDao.delete(model);<br/>
        
IQuery<TestModel> query = new QueryDaoImpl<TestModel>()<br/>
                .table(TestModel.class)<br/>
                .createQuery("*");<br/>
//query<br/>
List<TestModel> testModels = baseDao.query(query);<br/>
