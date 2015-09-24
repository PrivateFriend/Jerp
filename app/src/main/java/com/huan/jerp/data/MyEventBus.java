package com.huan.jerp.data;

/**
 * Created by Administrator on 2015/9/23.
 */
public class MyEventBus {
    public User getMuser() {
        return muser;
    }

    public Boolean[] getMboolean() {
        return mboolean;
    }

    private User muser;
    private Boolean[] mboolean;

    public MyEventBus() {
    }
    public MyEventBus(Boolean[] b) {
        mboolean=b;
    }
    public MyEventBus(User u) {
        muser=u;
    }
}
