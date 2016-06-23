###创建Android测试项目，导入相关资源
![](http://img.blog.csdn.net/20160623144651612)

```
	sourceSets {
        main {
            java.srcDirs = ['src/main/java', 'src/main/java-gen']
        }
    }

	compile 'de.greenrobot:greendao-generator:2.1.0'
    compile 'de.greenrobot:greendao:2.1.0'
```

###在Android项目下创建用于生成帮助类及Bean的java文件

- 创建java文件
	![](http://img.blog.csdn.net/20160623145103712)

	![](http://img.blog.csdn.net/20160623145314734)

	![](http://img.blog.csdn.net/20160623145329844)

	![](http://img.blog.csdn.net/20160623145855425)

- 编写具体的ExampleDaoGenerator代码如下：
	```
	import de.greenrobot.daogenerator.DaoGenerator;
	import de.greenrobot.daogenerator.Entity;
	import de.greenrobot.daogenerator.Schema;
	
	public class ExampleDaoGenerator {
	    private static void addTaskDetail(Schema schema) {
	        //指定实体类，参数是实体类的类名
	        Entity entity = schema.addEntity("User");
	        //添加id索引
	        entity.addIdProperty();
	        //添加属性userId,指定非空
	        entity.addStringProperty("userId").notNull();
	        //添加属性username
	        entity.addStringProperty("username");
	        //添加属性age
	        entity.addIntProperty("age");
	        //添加属性phone
	        entity.addStringProperty("phone");
	
	        Entity entity1 = schema.addEntity("Person");
	        entity1.addIdProperty();
	        entity1.addStringProperty("personId").notNull();
	        entity1.addStringProperty("personName");
	    }
	
	    //生成数据库文件的目标包名//target package for dao files
	    public static void main(String[] args) throws Exception {
	        // 第一个参数是数据库版本号，第二个参数是生成的文件所处的路径
	        Schema schema = new Schema(1, "com.lpf.greendb.db.bean");
	        // 此行代码不加，所有生成的文件将会在同一个目录下，加了此行代码，帮助类将会生成在次路径下
	        schema.setDefaultJavaPackageDao("com.lpf.greendb.db.helper");
	        addTaskDetail(schema);
	        try {
	            //'..'代表当前目录,接着就是个人的项目路径
	            new DaoGenerator().generateAll(schema, "../greenDB/app/src/main/java/");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	```

- 右键ExampleDaoGenerator文件，**run ExampleDaoGenerator.main()**,自动生成代码
	![](http://img.blog.csdn.net/20160623151816547)

	![](http://img.blog.csdn.net/20160623150221708)

###操作数据库

- 创建MyApplication对象

	>初始化DaoMaster和DaoSession

	```
		public class MyApplication extends Application {
	    private static DaoMaster daoMaster;
	    private static DaoSession daoSession;
	
	    @Override
	    public void onCreate() {
	        super.onCreate();
	    }
	
	    /**
	     * 得到DaoMaster对象
	     *
	     * @param context
	     * @return
	     */
	    public static DaoMaster getDaoMaster(Context context) {
	        if (null == daoMaster) {
	            //第一个参数，上下文对象，第二个参数库名
	            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, "GREENDB", null);
	            daoMaster = new DaoMaster(helper.getWritableDatabase());
	        }
	        return daoMaster;
	    }
	
	    /**
	     * 得到DaoSession对象
	     *
	     * @param context
	     * @return
	     */
	    public static DaoSession getDaoSession(Context context) {
	        if (daoSession == null) {
	            if (daoMaster == null) {
	                daoMaster = getDaoMaster(context);
	            }
	            daoSession = daoMaster.newSession();
	        }
	        return daoSession;
	    }
	}
	```
	
- 创建一个用于操作User表的helper
	```
	public class DBHelper {
	    private static DBHelper dbHelper;
	    private DaoSession daoSession;
	    private UserDao userDao;
	    private static Context mContext;
	
	    public DBHelper() {
	    }
	
	    public static DBHelper getinstance(Context context) {
	        if (null == dbHelper) {
	            dbHelper = new DBHelper();
	            if (null != context) {
	                mContext = context.getApplicationContext();
	                dbHelper.daoSession = MyApplication.getDaoSession(mContext);
	                dbHelper.userDao = dbHelper.daoSession.getUserDao();
	            }
	        }
	        return dbHelper;
	    }
	
	    /**
	     * 根据id查询
	     *
	     * @param id
	     * @return
	     */
	    public User loadUser(long id) {
	        return userDao.loadByRowId(id);
	    }
	
	    /**
	     * 获取所有
	     *
	     * @return
	     */
	    public List<User> loadAll() {
	        return userDao.loadAll();
	    }
	
	    /**
	     * 添加
	     *
	     * @param user
	     * @return
	     */
	    public long saveUser(User user) {
	        return userDao.insertOrReplace(user);
	    }
	
	    /**
	     * 根据条件查询
	     *
	     * @param where
	     * @param query
	     * @return
	     */
	    public List<User> queryUser(WhereCondition where, WhereCondition... query) {
	        if (null == query)
	            return userDao.queryBuilder().where(where).build().list();
	        else
	            return userDao.queryBuilder().where(where, query).build().list();
	    }
	
	    /**
	     * 添加多个
	     *
	     * @param users
	     */
	    public void saveUsers(final List<User> users) {
	        if (null == users && users.size() == 0)
	            return;
	        userDao.getSession().runInTx(new Runnable() {
	            @Override
	            public void run() {
	                for (int i = 0; i < users.size(); i++) {
	                    userDao.insertOrReplace(users.get(i));
	                }
	            }
	        });
	    }
	
	    /**
	     * 删除
	     *
	     * @param user
	     */
	    public void delUser(User user) {
	        userDao.delete(user);
	    }
	}
	```
	
- activity操作代码如下
	```
	public class MainActivity extends AppCompatActivity {
	
	    @InjectView(R.id.toolbar)
	    Toolbar toolbar;
	    @InjectView(R.id.id)
	    EditText id;
	    @InjectView(R.id.name)
	    EditText name;
	    @InjectView(R.id.age)
	    EditText age;
	    @InjectView(R.id.phone)
	    EditText phone;
	    @InjectView(R.id.insert)
	    Button insert;
	    @InjectView(R.id.getall)
	    Button getall;
	    @InjectView(R.id.update)
	    Button update;
	    @InjectView(R.id.query)
	    Button query;
	    @InjectView(R.id.getbyid)
	    Button getbyid;
	    @InjectView(R.id.inserts)
	    Button inserts;
	    @InjectView(R.id.del)
	    Button del;
	    @InjectView(R.id.msg)
	    TextView msg;
	    @InjectView(R.id.fab)
	    FloatingActionButton fab;
	
	    DBHelper dbHelper;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        ButterKnife.inject(this);
	        setSupportActionBar(toolbar);
	
	        dbHelper = DBHelper.getinstance(this);
	
	    }
	
	    @OnClick({R.id.insert, R.id.getall, R.id.update, R.id.query, R.id.getbyid, R.id.inserts, R.id.del})
	    public void onClick(View view) {
	        switch (view.getId()) {
	            case R.id.insert:
	                if (null != getUser()) {
	                    dbHelper.saveUser(getUser());
	                    showMsg(dbHelper.loadAll());
	                    Toast.makeText(this, "添加成功！", Toast.LENGTH_LONG).show();
	                }
	                break;
	            case R.id.getall:
	                showMsg(dbHelper.loadAll());
	                Toast.makeText(this, "查询成功！", Toast.LENGTH_LONG).show();
	                break;
	            case R.id.update:
	                if (null != getUser()) {
	                    User user = getUser();
	                    user.setId(1l);
	                    dbHelper.saveUser(user);
	                    Toast.makeText(this, "修改成功！", Toast.LENGTH_LONG).show();
	                    showMsg(dbHelper.loadAll());
	                }
	                break;
	            case R.id.query:
	                showMsg(dbHelper.queryUser(UserDao.Properties.Age.eq(12)));
	                Toast.makeText(this, "查询成功！", Toast.LENGTH_LONG).show();
	                break;
	            case R.id.getbyid:
	                msg.setText(dbHelper.loadUser(1).toString());
	                Toast.makeText(this, "查询成功！", Toast.LENGTH_LONG).show();
	                break;
	            case R.id.inserts:
	                break;
	            case R.id.del:
	                dbHelper.delUser(dbHelper.loadAll().get(1));
	                showMsg(dbHelper.loadAll());
	                Toast.makeText(this, "删除成功！", Toast.LENGTH_LONG).show();
	                break;
	        }
	    }
	
	    public User getUser() {
	        User user = new User();
	        try {
	            if (null != id.getText())
	                user.setUserId(id.getText().toString());
	            if (null != name.getText())
	                user.setUsername(name.getText().toString());
	            if (null != age.getText())
	                user.setAge(Integer.parseInt(age.getText().toString()));
	            if (null != phone.getText())
	                user.setPhone(phone.getText().toString());
	        } catch (Exception e) {
	            e.printStackTrace();
	            user = null;
	        }
	
	        return user;
	    }
	
	    public void showMsg(List<User> users) {
	        if (null != users && users.size() > 0) {
	            String msg = "";
	            for (User user : users) {
	                msg = msg + user.toString() + "\n";
	            }
	            this.msg.setText(msg);
	        } else {
	            this.msg.setText(null);
	        }
	    }
	}
	
	```

- 效果如下
![](http://img.blog.csdn.net/20160623153647042)

>这里只涉及到基础的单表的基础操作，还有更多涉及到更复杂的或者多表操作的，还需进一步研究，对于前端，大部分应用只需要简单的操作基础数据，因此基本已经足够使用了。

[github源码](https://github.com/183619962/GreenDB)


