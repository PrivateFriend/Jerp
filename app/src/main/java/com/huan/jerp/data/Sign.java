package com.huan.jerp.data;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2015/9/24.
 */
public class Sign extends BmobObject {
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String s) {
        this.user_id = s;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    private String user_id;
    private Integer grade;   // 0:迟到；1：正常；2：请假
}
