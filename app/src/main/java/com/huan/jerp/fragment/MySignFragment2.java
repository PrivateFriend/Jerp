package com.huan.jerp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.huan.jerp.R;
import com.huan.jerp.activity.SignOtherActivity;
import com.huan.jerp.adapter.SignAdapter;
import com.huan.jerp.data.User;
import com.huan.jerp.utils.ConstantUtil;
import com.huan.jerp.utils.HttpUtil;

import java.util.List;

import cn.bmob.v3.BmobUser;
import it.gmariotti.cardslib.library.view.CardExpandableListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link MySignFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MySignFragment2 extends Fragment {


    private String mParam1;
    private String mParam2;

    private View rootView;

    private User u;
    private Context mcontext;
    private CardExpandableListView listview;


    public static MySignFragment2 newInstance(String param1, String param2) {
        MySignFragment2 fragment = new MySignFragment2();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MySignFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_my_sign_fragment2, container, false);

        u= BmobUser.getCurrentUser(this.getActivity(), User.class);
        mcontext=this.getActivity();
        listview= (CardExpandableListView) rootView.findViewById(R.id.expandableListView);

        Handler mHandler=new Handler(){

            public void handleMessage(android.os.Message msg) {
                try {

                    if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_FAIL) {
                        Toast.makeText(getActivity(), "加载失败，请重试", Toast
                                .LENGTH_SHORT)
                                .show();
                    } else if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_NULL) {
                        Toast.makeText(getActivity(),"还没有设置公司位置", Toast
                                .LENGTH_SHORT)
                                .show();
                    } else if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_SUCCESS) {
                        List<User> ps= (List<User>) msg.obj;
                        final SignAdapter adapter=new SignAdapter(mcontext, ps);
                        listview.setAdapter(adapter);
                        listview.expandGroup(0);
                        listview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                                SignAdapter.selected_item = groupPosition;
                                return false;
                            }
                        });
                        listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                SignAdapter.selected_item=groupPosition;
                                User u= (User) adapter.getChild(groupPosition, childPosition);
//                                Toast.makeText(getActivity(),u.getObjectId(),Toast.LENGTH_SHORT).show();
                                Intent t=new Intent(mcontext, SignOtherActivity.class);
                                t.putExtra("id",u.getObjectId());
                                startActivity(t);
                                return true;
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        HttpUtil.getUserListDataInNet(mcontext, mHandler);

        return rootView;
    }


}
