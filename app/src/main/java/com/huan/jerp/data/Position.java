package com.huan.jerp.data;

import com.amap.api.services.core.LatLonPoint;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by Administrator on 2015/10/9.
 */
public class Position extends BmobObject {
    private BmobGeoPoint Position;

    public LatLonPoint getPosition() {
        return new LatLonPoint(Position.getLatitude(),Position.getLongitude());
    }

    public void setPosition(LatLonPoint position) {
        Position = new BmobGeoPoint(position.getLongitude(),position.getLatitude());
    }
}
