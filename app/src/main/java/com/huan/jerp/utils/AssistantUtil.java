package com.huan.jerp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/9/30.
 */
public class AssistantUtil {

    //判断网络情况
    public static boolean IsContectInterNet(Context activity) {
        try {
            boolean result;
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = cm.getActiveNetworkInfo();
            if (null!=netinfo && netinfo.isConnected()) {
                result = true;
            } else {
                result = false;
                Toast.makeText(activity,"无网络连接",
                        Toast.LENGTH_SHORT).show();
            }
            return result;
        } catch (Exception e) {
            return false;
        }
    }
}
