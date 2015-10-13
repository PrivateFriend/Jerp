package com.huan.jerp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huan.jerp.R;
import com.huan.jerp.data.News;
import com.huan.jerp.data.User;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class AddNewsActivity extends AppCompatActivity {

    @ViewInject(R.id.et_title)
    private EditText et_title;
    @ViewInject(R.id.et_content)
    private EditText et_content;
    @ViewInject(R.id.bt_submit)
    private FloatingActionButton bt_submit;
    @ViewInject(R.id.toolbar)
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        ViewUtils.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("添加公告");

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交公告
                News news = new News();
                news.setTitle(et_title.getText().toString());
                news.setContent(et_content.getText().toString());
                news.setAuthor(BmobUser.getCurrentUser(getBaseContext(), User.class).getName());
//                if(cb_shichang.isChecked()) news.add("enable", "市场部");
//                if(cb_caiwu.isChecked()) news.add("enable","财务部");
//                if(cb_jishu.isChecked()) news.add("enable","技术部");
//                if(cb_yingxiao.isChecked()) news.add("enable","营销部");

                news.save(getBaseContext(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplication(), "提交成功", Toast.LENGTH_SHORT).show();
                        backNews();
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        });
    }

    private void backNews() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_news, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
