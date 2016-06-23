package com.lpf.greendb;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lpf.greendb.db.bean.User;
import com.lpf.greendb.db.helper.DBHelper;
import com.lpf.greendb.db.helper.UserDao;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

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
