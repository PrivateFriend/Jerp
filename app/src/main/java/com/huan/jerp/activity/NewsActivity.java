package com.huan.jerp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.huan.jerp.R;
import com.huan.jerp.data.News;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class NewsActivity extends ActionBarActivity {

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.title)
    private TextView title;
    @ViewInject(R.id.author)
    private TextView author;
    @ViewInject(R.id.content)
    private TextView content;
    @ViewInject(R.id.time)
    private TextView time;

    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ViewUtils.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //返回按钮
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsActivity.this.finish();
            }
        });

        getSupportActionBar().setTitle("公告详情");
        Intent intent=getIntent();
        News news=intent.getParcelableExtra("selected_news");
        title.setText(news.getTitle());
        content.setText(news.getContent());
        author.setText(news.getAuthor());
        time.setText(intent.getStringExtra("time"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news, menu);
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
