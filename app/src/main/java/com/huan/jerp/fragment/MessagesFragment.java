package com.huan.jerp.fragment;

/**
 * Created by Andy on 2015/5/28.
 */

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.huan.jerp.R;
import com.huan.jerp.View.CalendarView;
import com.huan.jerp.adapter.TabFragmentAdapter;
import com.huan.jerp.data.Position;
import com.huan.jerp.data.Sign;
import com.huan.jerp.data.User;
import com.huan.jerp.utils.ConstantUtil;
import com.huan.jerp.utils.HttpUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;


public class MessagesFragment extends Fragment{

    private static double EARTH_RADIUS = 6378.137;

    private View rootView;
    private TabLayout tab;
    private ViewPager viewPager;
    private Context mContext;
    private View fm_view;
    private CalendarView calendarView;


    private List<Fragment> fragments;

    private MySignFragment new_fm,hot_fm,recommand_fm;
    private FloatingActionButton bt_sign;
    private FloatingActionButton bt_set;


    private LocationManagerProxy mlocationManager;
    private TextView tv_distance;
    public Double to_lat;
    public Double to_lng;
    private double diatance=0.0;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String normal="普通级别";
        if(BmobUser.getCurrentUser(getActivity().getBaseContext(), User.class).getPermissionname().equals(normal)){
            rootView=inflater.inflate(R.layout.fragment_my_sign, container, false);

            calendarView= (CalendarView) rootView.findViewById(R.id.calendar_view);
            tv_distance= (TextView) rootView.findViewById(R.id.tv_distance);
            tv_distance.setVisibility(View.GONE);
            bt_set= (FloatingActionButton) rootView.findViewById(R.id.bt_set);
            bt_set.setVisibility(View.GONE);
            bt_sign= (FloatingActionButton) rootView.findViewById(R.id.bt_sign);
            bt_sign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Handler nhandler=new Handler(){
                        public void handleMessage(android.os.Message msg) {
                            try {

                                if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_FAIL) {
                                    Toast.makeText(getActivity(),"加载失败，请重试", Toast
                                            .LENGTH_SHORT)
                                            .show();
                                } else if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_NULL) {
                                    Toast.makeText(getActivity(),"还没有设置公司位置", Toast
                                            .LENGTH_SHORT)
                                            .show();
                                } else if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_SUCCESS) {
                                    List<Position> ps= (List<Position>) msg.obj;
                                    to_lat=ps.get(0).getPosition().getLatitude();
                                    to_lng=ps.get(0).getPosition().getLongitude();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    };

                    HttpUtil.getP(getActivity(), nhandler);

                    if(mlocationManager==null){
                        mlocationManager=LocationManagerProxy.getInstance(getActivity());
                        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
                        // 在定位结束后，在合适的生命周期调用destroy()方法
                        // 其中如果间隔时间为-1，则定位只定一次
                        // 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
                        mlocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 0, new AMapLocationListener() {
                            @Override
                            public void onLocationChanged(AMapLocation aMapLocation) {
                                if (aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
                                    diatance=getDistance(aMapLocation.getLatitude(),aMapLocation.getLongitude(),to_lat,to_lng);
                                    tv_distance.setText(getDistance(aMapLocation.getLatitude(),aMapLocation.getLongitude(),to_lat,to_lng).toString()+"m");
                                } else {
                                    Log.e("AmapErr", "Location ERR:" + aMapLocation.getAMapException().getErrorCode());
                                }
                            }

                            @Override
                            public void onLocationChanged(Location location) {

                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
                    }


                    //获取系统时间，24小时制
                    Time time=new Time();
                    time.setToNow();
                    int year = time.year;
                    //月份默认为数字0-11
                    int month = time.month;
                    int day = time.monthDay;
                    final int minute = time.minute;
                    final int hour = time.hour;
                    int sec = time.second;

                    final Handler mHandler=new Handler(){
                        public void handleMessage(Message msg){
                            if(msg.what==1){
                                List<Sign> list=(List<Sign>)msg.obj;
                                if(list.size()==0){
                                    if(diatance<200){
                                        //此处根据时间存入不同的签到状态0,1,2,3,4,5
                                        Sign s=new Sign();
                                        s.setUser_id(BmobUser.getCurrentUser(getActivity(), User.class).getObjectId());
                                        //此处根据时间存入不同的签到状态0,1,2,3,4,5
                                        if(hour<17||(hour==17&&minute<=30)){
                                            Log.i("提示：","按时到达");
                                            s.setGrade(1);
                                        }else {
                                            Log.i("提示：","迟到了");
                                            s.setGrade(0);
                                        }
                                        s.save(getActivity(), new SaveListener() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(getActivity(),"签到成功",Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {

                                            }
                                        });
                                    }else {
                                        Toast.makeText(getActivity(),"距离不够",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(getActivity(),"今天已经签到过了",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    };
                    getData(year,month+1,day,mHandler);
                }
            });
        }else {
            rootView = inflater.inflate(R.layout.fragment_messages, container, false);
            initView();
        }
        return rootView;
    }

    private void initView(){

        tab= (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager= (ViewPager) rootView.findViewById(R.id.viewpager);
        List<String> tabList = new ArrayList<>();
        tabList.add("我的签到");
        tabList.add("查看签到");
        List<Fragment> fragmentList = new ArrayList<>();

        MySignFragment mySignFragment=new MySignFragment();
        fragmentList.add(mySignFragment);
        MySignFragment2 mySignFragment2=new MySignFragment2();
        fragmentList.add(mySignFragment2);
        TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList, tabList);
        tab.setTabsFromPagerAdapter(fragmentAdapter);
        viewPager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        viewPager.setOffscreenPageLimit(2);
        tab.setTabMode(TabLayout.MODE_FIXED);
        tab.setTabsFromPagerAdapter(fragmentAdapter);//给Tabs设置适配器
        tab.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
    }

    //根据经纬度计算两点之间的距离(单位：km)
    public static Double getDistance(Double lat1, Double lng1, Double lat2, Double lng2) {

        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double difference = radLat1 - radLat2;
        double mdifference = rad(lng1) - rad(lng2);
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(mdifference / 2), 2)));
        distance = distance * EARTH_RADIUS;
        distance = Math.round(distance * 10000) / 10;
//        String distanceStr = distance+"";
//        distanceStr = distanceStr.
//                substring(0, distanceStr.indexOf("."));

        return distance;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mlocationManager != null) {
//            mlocationManager.removeUpdates(this);
            mlocationManager.destroy();
        }
        mlocationManager = null;
    }

    //查询该用户当天是否已有记录
    public void getData(int y,int m,int d, final Handler handler){

        String dateString=String.valueOf(y).concat("-").concat(String.valueOf(m)).concat("-").concat(String.valueOf(d+1));
        String dateString1=String.valueOf(y).concat("-").concat(String.valueOf(m)).concat("-").concat(String.valueOf(d));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1  = null;
        Date date2  = null;
        try {
            date1 = sdf.parse(dateString);
            date2=sdf.parse(dateString1);
        } catch (ParseException e) {
        }
        BmobQuery<Sign> my_sign=new BmobQuery<Sign>();
        my_sign.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date1));

        BmobQuery<Sign> my_sign2=new BmobQuery<Sign>();
        my_sign2.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date2));

        BmobQuery<Sign> my_sign3=new BmobQuery<Sign>();
        my_sign3.addWhereEqualTo("user_id", BmobUser.getCurrentUser(getActivity(), User.class).getObjectId());

        List<BmobQuery<Sign>> andQuerys = new ArrayList<BmobQuery<Sign>>();
        andQuerys.add(my_sign);
        andQuerys.add(my_sign2);
        andQuerys.add(my_sign3);

        BmobQuery<Sign> sign=new BmobQuery<Sign>();
        sign.and(andQuerys);
        sign.findObjects(getActivity().getBaseContext(), new FindListener<Sign>() {
            @Override
            public void onSuccess(List<Sign> list) {
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = list;
                handler.sendMessage(message);
            }

            @Override
            public void onError(int i, String s) {
                System.out.println("查询失败:");
            }
        });
    }

}
