package com.huan.jerp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.huan.jerp.data.News;
import com.huan.jerp.data.Position;
import com.huan.jerp.data.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2015/9/30.
 */
public class HttpUtil {

    //获取所有用户列表
    public static void getUserListDataInNet(final Context context, final Handler mHandler) {
        BmobQuery<User> users = new BmobQuery<User>();
        users.setLimit(50);
        users.findObjects(context, new FindListener<User>() {

            @Override
            public void onSuccess(List<User> list) {
                try {
                    if (null != mHandler) {
                        Message msg = new Message();
                        if (null == list) {
                            msg.what = ConstantUtil.HANDLER_LOADING_DATA_NULL;
                        } else {
                            msg.what = ConstantUtil.HANDLER_LOADING_DATA_SUCCESS;
                            msg.obj = list;
                        }
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(int i, String s) {
                try {
                    if (null != mHandler) {
                        Message msg = new Message();
                        msg.what = ConstantUtil.HANDLER_LOADING_DATA_FAIL;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    //获取所有新闻列表
    public static void getNewsListDataInNet(final Context context, final Handler mHandler) {
        BmobQuery<News> query = new BmobQuery<News>();
        //新闻条目数量，默认为10
        query.setLimit(30);
        query.findObjects(context, new FindListener<News>() {
            @Override
            public void onSuccess(List<News> list) {
                try {
                    if (null != mHandler) {
                        Message msg = new Message();
                        if (null == list) {
                            msg.what = ConstantUtil.HANDLER_LOADING_DATA_NULL;
                        } else {
                            msg.what = ConstantUtil.HANDLER_LOADING_DATA_SUCCESS;
                            msg.obj = list;
                        }
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(int i, String s) {
                try {
                    if (null != mHandler) {
                        Message msg = new Message();
                        msg.what = ConstantUtil.HANDLER_LOADING_DATA_FAIL;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //获取公司位置信息
    public static void getP(final Context context, final Handler mHandler){
        BmobQuery<Position> p=new BmobQuery<Position>();
        p.setLimit(10);
        p.findObjects(context, new FindListener<Position>() {
            @Override
            public void onSuccess(List<Position> list) {
                try {
                    if (null != mHandler) {
                        Message msg = new Message();
                        if (null == list||list.size()==0) {
                            msg.what = ConstantUtil.HANDLER_LOADING_DATA_NULL;
                        } else {
                            msg.what = ConstantUtil.HANDLER_LOADING_DATA_SUCCESS;
                            msg.obj = list;
                        }
                        System.out.println("list:"+list.size());
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int i, String s) {
                try {
                    if (null != mHandler) {
                        Message msg = new Message();
                        msg.what = ConstantUtil.HANDLER_LOADING_DATA_FAIL;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
