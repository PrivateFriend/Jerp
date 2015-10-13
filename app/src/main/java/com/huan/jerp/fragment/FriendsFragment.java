package com.huan.jerp.fragment;

/**
 * Created by Andy on 2015/5/28.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.huan.jerp.R;
import com.huan.jerp.address.CharacterParser;
import com.huan.jerp.address.ClearEditText;
import com.huan.jerp.address.PinyinComparator;
import com.huan.jerp.address.SideBar;
import com.huan.jerp.address.SortAdapter;
import com.huan.jerp.address.SortModel;
import com.huan.jerp.data.User;
import com.huan.jerp.utils.AssistantUtil;
import com.huan.jerp.utils.ConstantUtil;
import com.huan.jerp.utils.HttpUtil;
import com.lidroid.xutils.DbUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;


public class FriendsFragment extends Fragment {
    //----
    private View parentView = null;
    private SwipeMenuListView sortListView;
    private ClearEditText mClearEditText;
    private SideBar sideBar;
    private TextView dialog;
    //----
    private List<SortModel> SourceDateList;
    private List<User> list_users;
    private SortAdapter adapter;
    //----
    private PinyinComparator pinyinComparator;
    private CharacterParser characterParser;
    //----
    private DbUtils db = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            parentView = inflater.inflate(R.layout.fragment_friends, container, false);
            //----
            characterParser = CharacterParser.getInstance();
            pinyinComparator = new PinyinComparator();
            //----
            initView();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return parentView;
    }

    private void initView() {
        try {
            //----
            dialog = (TextView) parentView.findViewById(R.id.dialog);
            sideBar = (SideBar) parentView.findViewById(R.id.sidrbar);
            sideBar.setTextView(dialog);
            sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

                @Override
                public void onTouchingLetterChanged(String s) {
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        sortListView.setSelection(position);
                    }

                }
            });
            //----
            mSwipeRefreshLayout = (SwipeRefreshLayout)parentView.findViewById(R.id.swipe_container);
            //设置刷新时动画的颜色，可以设置4个
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }, 1000);
                }
            });
            //----
            sortListView = (SwipeMenuListView) parentView.findViewById(R.id.listView);
            //获取数据库缓存
            getDataInCache();
            if (null != list_users && list_users.size() > 0) {
                setAdapter(false);
            }
            //获取后台数据
            loadingData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置适配器
    private void setAdapter(Boolean fromNet) {

        String[] s = new String[list_users.size()];
        String[] h = new String[list_users.size()];
        String[] phone = new String[list_users.size()];
        for (int i = 0; i < list_users.size(); i++) {
            s[i] = list_users.get(i).getName();

            if (fromNet) {
                BmobFile file = list_users.get(i).getPhoto();
                if (file != null) {
                    String icon_url = file.getFileUrl(getActivity().getBaseContext());
                    h[i] = icon_url;
                    list_users.get(i).setHead(icon_url);
                } else {
                    h[i] = null;
                }
            } else {
                h[i] = list_users.get(i).getHead();
            }

            phone[i] = list_users.get(i).getMobilePhoneNumber();
        }
        SourceDateList = filledData(s, h, phone);

        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(getActivity(), SourceDateList);
        sortListView.setAdapter(adapter);

        //----
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu swipeMenu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setIcon(R.drawable.ic_dialer_sip_red_300_48dp);
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                swipeMenu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_textsms_white_48dp);
                // add to menu
                swipeMenu.addMenuItem(deleteItem);
            }


        };
        sortListView.setMenuCreator(creator);
        sortListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                                + SourceDateList.get(position).getPhone()));
                        startActivity(intent);
                        break;
                    case 1:
                        // delete

                        Intent t = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
                                + SourceDateList.get(position).getPhone()));
                        t.putExtra("sms_body", "");
                        startActivity(t);

                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        //设置滑动监听器
        sortListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
        sortListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), position + " long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //----
        mClearEditText = (ClearEditText) parentView.findViewById(R.id.filter_edit);
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //--------------------------------------------------------------
    //填充数据
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    private List<SortModel> filledData(String[] date, String[] head, String[] p) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();

            sortModel.setName(date[i]);
            if (null != head[i]) {
                sortModel.setHead(head[i]);
            }
            sortModel.setPhone(p[i]);

            String pinyin = characterParser.getSelling(date[i]);

            String sortString = pinyin.substring(0, 1).toUpperCase();


            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }


    //-------------------------------------------------------------------
    //本地数据库操作
    /*
    获取缓存中的联系人数据
     */
    private void getDataInCache() {
        try {
            db = DbUtils.create(getActivity());
            if (db.tableIsExist(User.class)) {
                list_users = db.findAll(User.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                db.close();
            } catch (Exception ex) {
            }
        }
    }

    /*
     保存联系人数据到缓存中
      */
    private void saveDataInCache(List<User> listData) {
        try {
            db = DbUtils.create(getActivity());
            if (!db.tableIsExist(User.class)) {
                db.createTableIfNotExist(User.class);
            } else {
                db.deleteAll(User.class);
            }
            if (null != listData) {
                db.saveAll(listData);
            }
        } catch (Exception e) {
            try {
                db.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    //-------------------------------------------------------------
    //后台数据库操作
    /*
    加载网络数据
    */
    private void loadingData() {
        //当前有网络
        if (AssistantUtil.IsContectInterNet(getActivity())) {
            HttpUtil.getUserListDataInNet(getActivity(), mHandler);
        }
    }

    //获取后台数据
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_FAIL) {
                    Toast.makeText(getActivity(),"加载失败，请重试", Toast
                            .LENGTH_SHORT)
                            .show();
                } else if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_NULL) {
                    Toast.makeText(getActivity(),"未创建数据", Toast
                            .LENGTH_SHORT)
                            .show();
                } else if (msg.what == ConstantUtil.HANDLER_LOADING_DATA_SUCCESS) {
                    list_users = (List<User>) msg.obj;
                    setAdapter(true);
                    //保存到本地缓存
                    saveDataInCache(list_users);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

}