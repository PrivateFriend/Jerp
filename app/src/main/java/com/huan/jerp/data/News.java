package com.huan.jerp.data;

import java.lang.reflect.Array;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2015/8/29.
 */


public class News extends BmobObject {

    private String title;
    private String content;

    public void setAuthor(String author) {
        this.author = author;
    }

    private String author;

    public Array getEnable() {
        return enable;
    }

    public void setEnable(Array enable) {
        this.enable = enable;
    }

    private Array  enable;   //可见性



    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }



}
