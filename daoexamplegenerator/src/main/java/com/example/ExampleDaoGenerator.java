package com.example;

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
