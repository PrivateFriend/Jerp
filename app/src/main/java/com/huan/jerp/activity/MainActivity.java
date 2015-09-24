package com.huan.jerp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huan.jerp.R;
import com.huan.jerp.View.CircleTransformation;
import com.huan.jerp.data.User;
import com.huan.jerp.fragment.BlankFragment;
import com.huan.jerp.fragment.FriendsFragment;
import com.huan.jerp.fragment.HomeFragment;
import com.huan.jerp.fragment.MessagesFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.squareup.picasso.Picasso;

import cn.bmob.v3.BmobUser;
import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;
    @ViewInject(R.id.nv_main_navigation)
    private NavigationView navigationView;
    @ViewInject(R.id.tv_user_name)
    private TextView mUserName;
    @ViewInject(R.id.iv_user_head)
    private ImageView mUserHead;
    @ViewInject(R.id.drawer_layout)
    private DrawerLayout mDrawerLayout;
    //frament
    private FragmentTransaction  transaction;
    private Context mContext;

    private HomeFragment homeFragment;
    private FriendsFragment friendsFragment;
    private MessagesFragment messagesFragment;
    private BlankFragment blankFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        mContext=MainActivity.this;


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
        setTabSelection(0);
        getSupportActionBar().setTitle("公告板");
    }

    private void initView() {

        //设置头像和用户名
        User mUser= BmobUser.getCurrentUser(getBaseContext(), User.class);
        if (mUser!=null){
            if (mUser.getPhoto()!=null) {
                Picasso.with(mContext).load(mUser.getPhoto().getFileUrl(mContext)).transform(new CircleTransformation()).into(mUserHead);
            }else {
                Picasso.with(mContext).load(R.mipmap.ic_user).transform(new CircleTransformation()).into(mUserHead);
            }

        }else{
            Picasso.with(mContext).load(R.mipmap.ic_user).transform(new CircleTransformation()).into(mUserHead);
        }
        mUserName.setText(mUser.getName());
        final ActionBar actionBar = getSupportActionBar();
        //设置toorbar的返回按钮是否显示
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        navigationView.getMenu().getItem(0).setChecked(true);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        mUserHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置个人资料
                Toast.makeText(getApplication(), "设置个人资料", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(MainActivity.this,UserProfileActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //搜索按钮的功能
        if (id == R.id.action_search){
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }
        //设置选项
        Intent intent;
        switch (id){
            case android.R.id.home:
                //打开抽屉
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.setting:
                return true;
            case R.id.sync:
                return true;
            case R.id.reLogin:
                //注销
                intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                MainActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setTabSelection(int index) {
        transaction = getSupportFragmentManager().beginTransaction();
        hideFragments(transaction);
        switch (index) {
            case 0:
                if (homeFragment==null){
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.container_body,homeFragment);
                }else {
                    transaction.show(homeFragment);
                }
                break;
            case 1:
                if (friendsFragment==null){
                    friendsFragment = new FriendsFragment();
                    transaction.add(R.id.container_body,friendsFragment);

                }else {
                    transaction.show(friendsFragment);
                }
                break;
            case 2:
                if (messagesFragment==null){
                    messagesFragment = new MessagesFragment();
                    transaction.add(R.id.container_body,messagesFragment);
                }else{
                    transaction.show(messagesFragment);
                }

                break;
            case 3:
                if (blankFragment==null){
                    blankFragment = new BlankFragment();
                    transaction.add(R.id.container_body,blankFragment);
                }else {
                    transaction.show(blankFragment);
                }
                break;
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (friendsFragment != null) {
            transaction.hide(friendsFragment);
        }
        if (messagesFragment != null) {
            transaction.hide(messagesFragment);
        }
        if (blankFragment != null) {
            transaction.hide(blankFragment);
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.news_yc:
                                setTabSelection(0);
                                mToolbar.setTitle(menuItem.getTitle());
                                menuItem.setChecked(true);
                                break;
                            case R.id.tour_yc:
                                setTabSelection(1);
                                mToolbar.setTitle(menuItem.getTitle());
                                menuItem.setChecked(true);
                                break;
                            case R.id.food_yc:
                                setTabSelection(2);
                                mToolbar.setTitle(menuItem.getTitle());
                                menuItem.setChecked(true);
                                break;
                            case R.id.info_yc:
                                setTabSelection(3);
                                mToolbar.setTitle(menuItem.getTitle());
                                menuItem.setChecked(true);
                                break;
                            case R.id.setting:

                                break;
                            case R.id.about:

                                break;
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        BmobUser.logOut(this);
        super.onDestroy();
        //反注册EventBus
        EventBus.getDefault().unregister(this);
    }
}
