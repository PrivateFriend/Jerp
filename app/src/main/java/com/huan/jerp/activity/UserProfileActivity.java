package com.huan.jerp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huan.jerp.R;
import com.huan.jerp.View.BitmapUtil;
import com.huan.jerp.View.CircleTransformation;
import com.huan.jerp.data.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class UserProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    private static final String TAG = "UserProfileActivity";

    private static final int PICK_FROM_CAMERA = 0x000000;
    private static final int PICK_FROM_FILE = 0x000001;
    private static final int CROP_FROM_CAMERA = 0x000002;
    private static final int CROP_FROM_FILE = 0x000003;

    public static final String ARG_TAKEN_PHOTO_URI = "image";

    private static final int STATE_REFRESH = 0;
    private static final int STATE_MORE = 1;
    private AppBarLayout appBarLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<User> list=new ArrayList<>();
    private BmobQuery<User> bmobQuery;
    private int limit = 10;
    private int curPage = 0;
    private Context mContext;
    private Toolbar mToolbar;
    private ImageView user_icon;
    private TextView nameView;
    private ImageView sexView;
    private RelativeLayout rl_toast;
    private User mUser;
    private String user_id = "bd2e7925fd";
    private String bitmapUrl;

    private ImageView headImg;
    private Uri imgUri;
    private Uri photoUri;

    private LinearLayout layout_phone;

    private TextView tv_head, tv_nick, tv_sex, tv_address, tv_introduce, tv_phone, tv_creatTime,tv_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        layout_phone= (LinearLayout) findViewById(R.id.ll_cui_phone);
        headImg = (ImageView) findViewById(R.id.iv_cui_img);
        tv_head = (TextView) findViewById(R.id.tv_cui_editImg);
        tv_nick = (TextView) findViewById(R.id.tv_cui_nick);
        tv_sex = (TextView) findViewById(R.id.tv_cui_sex);
        tv_address = (TextView) findViewById(R.id.tv_cui_address);
        tv_introduce = (TextView) findViewById(R.id.tv_cui_introduce);
        tv_phone = (TextView) findViewById(R.id.tv_cui_phone);
        tv_creatTime = (TextView) findViewById(R.id.tv_cui_createTime);

        tv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(true);
            }
        });

        mContext = UserProfileActivity.this;
//        mUser= (Person) getIntent().getSerializableExtra(CURRENT_USER);

        initViews();
        initDatas();
    }

    private void initDatas() {
        mUser = BmobUser.getCurrentUser(mContext, User.class);
        user_id = mUser.getObjectId();
        BmobFile file=mUser.getPhoto();
        if (file!=null) {
            String icon_url =file.getFileUrl(mContext);
            Picasso.with(mContext).load(icon_url).transform(new CircleTransformation()).into(user_icon);
            Picasso.with(mContext).load(icon_url).transform(new CircleTransformation()).into(headImg);

        } else {
            Picasso.with(mContext).load(R.mipmap.ic_user).transform(new CircleTransformation()).into(user_icon);
        }
        nameView.setText(mUser.getName());
        tv_sign.setText(mUser.getInfo());
        tv_nick.setText(mUser.getName());
        tv_sex.setText(mUser.getAddress());
        tv_address.setText(mUser.getDepartment());
        tv_introduce.setText(mUser.getInfo());
        tv_phone.setText(mUser.getMobilePhoneNumber());
        tv_creatTime.setText(mUser.getCreatedAt());

        Boolean sex=mUser.getSex();
        //此处性别获取参数错误
        try {
            if (null==sex||!sex.booleanValue()) {
                sexView.setImageResource(R.mipmap.userinfo_icon_male);
            }else{
                sexView.setImageResource(R.mipmap.userinfo_icon_female);
            }
        }catch(Exception e){

        }

    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.tl_aup_toolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileActivity.this.finish();
            }
        });
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.ctl_aup_toolbar);
//        collapsingToolbar.setExpandedTitleColor(R.color.white);
//        collapsingToolbar.setCollapsedTitleTextColor(R.color.accent);
        collapsingToolbar.setTitle("用户详情");

        collapsingToolbar.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_ENDED) {
                    Log.e(TAG, "DragEvent.ACTION_DRAG_ENDED");
                }
                return false;
            }
        });
        appBarLayout= (AppBarLayout) findViewById(R.id.apl_aup_appbar);
        user_icon = (ImageView) findViewById(R.id.iv_vup_userProfilePhoto);
        user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入修改信息页面
//                Intent intent=new Intent(mContext,UserInfoActivity.class);
//                intent.putExtra(CURRENT_USER,mUser);
//                startActivity(intent);
                Toast.makeText(getApplication(), "修改资料", Toast.LENGTH_SHORT).show();
            }
        });
        nameView= (TextView) findViewById(R.id.tv_vup_userName);
        tv_sign= (TextView) findViewById(R.id.tv_vup_userSign);
        sexView= (ImageView) findViewById(R.id.iv_vup_sex);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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


    private void showDialog(boolean user){
        if (user) {
            View dialog_publish = LayoutInflater.from(mContext).inflate(
                    R.layout.dialog_publish_photo, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(dialog_publish);
            builder.create();
            final AlertDialog dialog = builder.show();
            ImageView choiceCamera = (ImageView) dialog_publish
                    .findViewById(R.id.img_choice_from_camera);
            ImageView choicePhoto = (ImageView) dialog_publish
                    .findViewById(R.id.img_choice_from_photo);
            ImageView choiceCancle = (ImageView) dialog_publish.findViewById(R.id.img_choice_cancale);
            choiceCamera.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    getPicFromCapture();

                }
            });

            choicePhoto.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    getPicFromContent();
                }
            });

            choiceCancle.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                }
            });
        }else{
//            startActivity(new Intent(mContext, LoginActivity.class));
        }
    }

    private void getPicFromContent() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, PICK_FROM_FILE);
    }

    private void getPicFromCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imgUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "avatar_" + String.valueOf(System.currentTimeMillis()) + ".png"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, requestCode);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                cropImageUri(imgUri, 250, 250, CROP_FROM_CAMERA);
                break;
            case PICK_FROM_FILE:
                imgUri = data.getData();
                doCrop();
                break;
            case CROP_FROM_CAMERA:
                if (imgUri != null) {
                    setCropImg(data,CROP_FROM_CAMERA);
                }
                break;
            case CROP_FROM_FILE:
                if (null != data) {
                    setCropImg(data,CROP_FROM_FILE);
                }
                break;
        }

    }

    private void doCrop() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imgUri, "image/*");
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_FROM_FILE);
    }

    private void setCropImg(Intent picdata,int type) {
        Bundle bundle = picdata.getExtras();
        Bitmap mBitmap=null;
        if (null != bundle) {
            if (type==CROP_FROM_FILE)
            {
                mBitmap =bundle.getParcelable("data");
            }else if(type==CROP_FROM_CAMERA)
            {

                try {
                    mBitmap = BitmapUtil.decodeSampledBitmapFromResource(mContext, photoUri, 150, 150);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            String BitmapPath = BitmapUtil.saveBitmap(mBitmap, mContext);
            final BmobFile file = new BmobFile(new File(BitmapPath));
            file.upload(this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    Log.e(TAG, "file.upload is ok");
                    //更新数据库信息
                    updateHead_thumb(file.getFileUrl(mContext));
                    mUser.update(mContext, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.e(TAG, " currentUser.update is ok");
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Log.e(TAG, " currentUser.update is onFailure---" + i + s);
                        }
                    });
                }

                @Override
                public void onFailure(int i, String s) {
                    Log.e(TAG, "file.upload is onFailure---" + i + s);
                }
            });


        }

    }

    private void updateHead_thumb(String url) {
        Picasso.with(mContext).load(url).transform(new CircleTransformation()).into(headImg);
        Picasso.with(mContext).load(url).transform(new CircleTransformation()).into(user_icon);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

    }
}
