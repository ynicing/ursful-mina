# urs-database

轻量级ORM，详细查看src/test/Test.java

light orm database, CRED and statistic  @ursful.com<br/>

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

//函数使用,如 sum <br/>

IQuery<TestModel> query = new QueryDaoImpl<TestModel>()<br/>
.table(TestModel.class)<br/>
.createQuery(TestModel.class, new Column("sum", null, TestModel.T_NUMBER, "number"));<br/>
TestModel result = baseDao.get(query);<br/>
System.out.println(result.getNumber());<br/>
