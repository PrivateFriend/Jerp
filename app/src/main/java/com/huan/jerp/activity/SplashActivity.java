package com.huan.jerp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.huan.jerp.R;
import com.huan.jerp.data.User;

import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class SplashActivity extends ActionBarActivity {

    private static final int ANIMATION_DURATION = 2000;
    private static final float SCALE_END = 1.13F;


    private static final int[] SPLASH_ARRAY = {
            R.drawable.splash0,
            R.drawable.splash1,
            R.drawable.splash2,
            R.drawable.splash3,
            R.drawable.splash4,
            R.drawable.splash5,
            R.drawable.splash6,
            R.drawable.splash7,
            R.drawable.splash8,
            R.drawable.splash9,
            R.drawable.splash10,
            R.drawable.splash11,
            R.drawable.splash12,
            R.drawable.splash13,
            R.drawable.splash14,
            R.drawable.splash15,
            R.drawable.splash16,
    };

    ImageView mSplashImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, "eccc7ac9c330502182bd42bb39c86a31");

        mSplashImage= (ImageView) findViewById(R.id.iv_entry);
        Random r = new Random(SystemClock.elapsedRealtime());
        mSplashImage.setImageResource(SPLASH_ARRAY[r.nextInt(SPLASH_ARRAY.length)]);

        //判断是否首次进入
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstTime = prefs.getBoolean("first_time", true);
        if (firstTime){

            //初回起動フラグをfalseに変更
            SharedPreferences.Editor pEdit = prefs.edit();
            pEdit.putBoolean("first_time", false);
            pEdit.commit();
        }


        animateImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
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

    private void animateImage() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(mSplashImage, "scaleX", 1f, SCALE_END);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(mSplashImage, "scaleY", 1f, SCALE_END);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(ANIMATION_DURATION).play(animatorX).with(animatorY);
        set.start();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                SharedPreferences preferences=getSharedPreferences("user", Context.MODE_PRIVATE);
                if(preferences.getString("username","").isEmpty()){
                    Login.start(SplashActivity.this);
                    SplashActivity.this.finish();
                }else {
                    BmobUser.loginByAccount(getBaseContext(), preferences.getString("username", ""), preferences.getString("password", ""), new LogInListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }
                    });
                }

//                SplashActivity.this.finish();
            }
        });
    }
}
