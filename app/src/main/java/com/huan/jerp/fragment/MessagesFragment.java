package com.huan.jerp.fragment;

/**
 * Created by Andy on 2015/5/28.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huan.jerp.R;
import com.huan.jerp.adapter.TabFragmentAdapter;

import java.util.ArrayList;
import java.util.List;


public class MessagesFragment extends Fragment {
    private View rootView;
    private TabLayout tab;
    private ViewPager viewPager;
    private Context mContext;
    private View fm_view;

    private List<Fragment> fragments;

    private MySignFragment new_fm,hot_fm,recommand_fm;

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
        rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        if (rootView!=null){
            initView();
        }
        return rootView;
    }

    private void initView(){

        tab= (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager= (ViewPager) rootView.findViewById(R.id.viewpager);
        new_fm=MySignFragment.newInstance(MySignFragment.ARG_PARAM1);
        hot_fm=MySignFragment.newInstance(MySignFragment.ARG_PARAM2);
        List<String> tabList = new ArrayList<>();
        tabList.add("我的签到");
        tabList.add("签到");
        List<Fragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < tabList.size(); i++) {
            Fragment f1 = new MySignFragment();
            fragmentList.add(f1);
        }
        TabFragmentAdapter fragmentAdapter = new TabFragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList, tabList);
        tab.setTabsFromPagerAdapter(fragmentAdapter);
        viewPager.setAdapter(fragmentAdapter);//给ViewPager设置适配器
        viewPager.setOffscreenPageLimit(2);
        tab.setTabMode(TabLayout.MODE_FIXED);
        tab.setTabsFromPagerAdapter(fragmentAdapter);//给Tabs设置适配器
        tab.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
