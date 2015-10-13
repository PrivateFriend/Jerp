package com.huan.jerp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.huan.jerp.R;
import com.huan.jerp.data.User;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;


public class Login extends AppCompatActivity {
    @ViewInject(R.id.textInput_layout_name)
    private TextInputLayout utext;
    @ViewInject(R.id.textInput_layout_password)
    private TextInputLayout ptext;
    @ViewInject(R.id.bt_add)
    private FloatingActionButton b1;
    @ViewInject(R.id.bt_submit)
    private FloatingActionButton b2;
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;
    @ViewInject(R.id.checkBox)
    private CheckBox checkBox;
    @ViewInject(R.id.cb_remember)
    private CheckBox cb_remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("登录");

        SharedPreferences pfe=getSharedPreferences("user_remember",Context.MODE_PRIVATE);
        if(!pfe.getString("username","").isEmpty()){
            utext.getEditText().setText(pfe.getString("username", ""));
            ptext.getEditText().setText(pfe.getString("password", ""));
        }

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(utext.getEditText().getText().toString(), ptext.getEditText().getText().toString());
            }
        });

        b1.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submit(String user,String pass){

        try {
            BmobUser.loginByAccount(this, user, pass, new LogInListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (user != null){

                        //自动登录
                        if (checkBox.isChecked()) {
                            SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username", utext.getEditText().getText().toString());
                            editor.putString("password", ptext.getEditText().getText().toString());
                            editor.commit();
                        }

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        Toast.makeText(getApplication(), "登录成功", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        Login.this.finish();
                    }else {
                        e.getErrorCode();
                        Toast.makeText(getApplication(),"用户名或密码错误",Toast.LENGTH_SHORT).show();
                    }
                    //记住密码
                    if (cb_remember.isChecked()) {
                        SharedPreferences preferences = getSharedPreferences("user_remember", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", utext.getEditText().getText().toString());
                        editor.putString("password", ptext.getEditText().getText().toString());
                        editor.commit();
                    }

                }
            });

        }catch (Exception e){
            Toast.makeText(getApplication(),"用户名或密码错误",Toast.LENGTH_SHORT).show();
        }

    }

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, Login.class));
    }
}
