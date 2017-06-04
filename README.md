# urs-database
light operation database, CRED and statistic  @ursful.com<br/>

IBaseDao<TestModel> baseDao = new BaseDaoImpl<TestModel>();<br/>

TestModel model = new TestModel();<br/>
model.setId(UUID.randomUUID().toString());<br/>
model.setCreateDate(new Date());<br/>
model.setName("Hello");<br/>
model.setNumber(10);<br/>

//保存<br/>
baseDao.save(model);<br/>
 
model.setName("me");<br/>

//更新<br/>
baseDao.update(model);<br/>

model.setCreateDate(null);<br/>

//更新Null<br/>
baseDao.update(model, true);<br/>

//删除<br/>
baseDao.delete(model);<br/>
        
IQuery<TestModel> query = new QueryDaoImpl<TestModel>()<br/>
.table(TestModel.class)<br/>
.createQuery("*");<br/>

//查询<br/>
List<TestModel> testModels = baseDao.query(query);<br/>

//函数使用<br/>
IMultiQueryDao<TestModel> multiQuery = new MultiQueryDaoImpl<TestModel>();<br/>
multiQuery.createAliasTable("t", TestModel.class)<br/>
multiQuery.createQuery(TestModel.class, new Column("sum", "t", TestModel.T_NUMBER, TestModel.T_NUMBER));<br/>

List<TestModel> m = baseDao.query(dao);<br/>
System.out.println(m.get(0).getNumber());<br/>