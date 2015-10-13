package com.huan.jerp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.huan.jerp.R;
import com.huan.jerp.data.Position;
import com.huan.jerp.utils.ConstantUtil;
import com.huan.jerp.utils.HttpUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SelectPositionActivity extends AppCompatActivity implements GeocodeSearch.OnGeocodeSearchListener,View.OnClickListener,AMap.OnMapClickListener {

    private ProgressDialog progDialog = null;
    private GeocodeSearch geocoderSearch;
    private String addressName;
    private AMap aMap;
    private MapView mapView;
    private Marker geoMarker;
    private Marker regeoMarker;
    @ViewInject(R.id.toolbar)
    private Toolbar mToolbar;

    private static LatLonPoint lp;   //存储公司位置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_position);
        ViewUtils.inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //返回按钮
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPositionActivity.this.finish();
            }
        });
        getSupportActionBar().setTitle("公司位置");

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMapClickListener(this);
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    convertToLatLng(new LatLonPoint(28.129375,113.002808)), 15));
            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
//        Button geoButton = (Button) findViewById(R.id.geoButton);
//        geoButton.setOnClickListener(this);
//        Button regeoButton = (Button) findViewById(R.id.regeoButton);
//        regeoButton.setOnClickListener(this);
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 响应地理编码
     */
    public void getLatlon(final String name) {
        showDialog();
        GeocodeQuery query = new GeocodeQuery(name, "changsha");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_position, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("输入地址进行搜索，如：黄河大厦");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getLatlon(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        if (id == R.id.action_submit) {
            //更新公司位置数据
            updateP(lp);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateP(LatLonPoint lp) {
        HttpUtil.getP(this,handler);
    }

    private Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            try {
                Position p=new Position();
                p.setPosition(lp);
                if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_FAIL) {
                    Toast.makeText(getBaseContext(),"加载失败，请重试", Toast
                            .LENGTH_SHORT)
                            .show();
                } else if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_NULL) {
                    p.save(getBaseContext(), new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getBaseContext(),"公司位置保存成功",Toast.LENGTH_SHORT).show();
                            SelectPositionActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            System.out.println("位置保存失败");
                        }
                    });
                } else if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_SUCCESS) {
                    List<Position> ps= (List<Position>) msg.obj;
                    p.update(getBaseContext(), ps.get(0).getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getBaseContext(),"公司位置更新成功",Toast.LENGTH_SHORT).show();
                            SelectPositionActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

        dismissDialog();
        if (i == 0) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = regeocodeResult.getRegeocodeAddress().getFormatAddress()
                        + "附近";
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        convertToLatLng(latLonPoint), 15));
//                regeoMarker.setPosition(convertToLatLng(latLonPoint));
                Toast.makeText(this, addressName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "no address", Toast.LENGTH_SHORT).show();
            }
        } else if (i == 27) {
            Toast.makeText(this, "no internet", Toast.LENGTH_SHORT);
        } else if (i == 32) {
            Toast.makeText(this, "no key", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(this, "no ecrrow", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

        dismissDialog();
        if (i == 0) {
            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                    && geocodeResult.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        convertToLatLng(address.getLatLonPoint()), 15));
                geoMarker.setPosition(convertToLatLng(address
                        .getLatLonPoint()));
                lp=address.getLatLonPoint();
                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
                        + address.getFormatAddress();
                System.out.println(addressName);
                Toast.makeText(this, addressName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "no address", Toast.LENGTH_SHORT);
            }

        } else if (i == 27) {
            Toast.makeText(this, "no internet", Toast.LENGTH_SHORT);
        } else if (i == 32) {
            Toast.makeText(this, "no key", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(this, "other ecrrow", Toast.LENGTH_SHORT);
        }
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    private LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /**
             * 响应地理编码按钮
             */
//            case R.id.geoButton:
//                getLatlon("湘林家园");
//                break;
            /**
             * 响应逆地理编码按钮
             */
//            case R.id.regeoButton:
//                getAddress(latLonPoint);
//                break;
            default:
                break;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        geoMarker.setPosition(latLng);
        getAddress(convertToLatLonPoint(latLng));
        lp=convertToLatLonPoint(latLng);
    }
}
