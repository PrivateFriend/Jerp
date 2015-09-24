package com.huan.jerp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huan.jerp.R;
import com.huan.jerp.View.CalendarView;


public class MySignFragment extends android.support.v4.app.Fragment {
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private CalendarView calendarView;
    private View rootView;


    public static MySignFragment newInstance(String param1) {
        MySignFragment fragment = new MySignFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public MySignFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView=inflater.inflate(R.layout.fragment_my_sign, container, false);
        if (rootView!=null){
            initView();
        }
        return rootView;
    }

    private void initView(){
        calendarView= (CalendarView) rootView.findViewById(R.id.calendar_view);
//        calendarView.setClickDataListener(new ClickDataListener() {
//
//            @Override
//            public void clickData(String year, String month, String day) {
//                Toast.makeText(getActivity().getBaseContext(),
//                        year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }
}
