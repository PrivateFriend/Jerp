package com.huan.jerp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.huan.jerp.R;
import com.huan.jerp.activity.AddNewsActivity;
import com.huan.jerp.activity.NewsActivity;
import com.huan.jerp.adapter.NewsAdapter;
import com.huan.jerp.data.News;
import com.huan.jerp.utils.AssistantUtil;
import com.huan.jerp.utils.ConstantUtil;
import com.huan.jerp.utils.HttpUtil;
import com.lidroid.xutils.DbUtils;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;


public class HomeFragment extends Fragment {
    //----
    private View rootView;
    private CardRecyclerView news;
    private FloatingActionButton bt_submit;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //----
    private NewsAdapter adapter;
    private List<News> list_news;
    //----
    private DbUtils db;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //----
        initView();
        return rootView;
    }

    private void initView() {
        //----
        bt_submit = (FloatingActionButton) rootView.findViewById(R.id.bt_add);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNews();
            }
        });
        //----
        news = (CardRecyclerView) rootView.findViewById(R.id.news);
        //获取数据库缓存
        getDataInCache();

        if (null != list_news && list_news.size() > 0) {
            setAdapter();
        }
        //获取后台数据
        loadingData();

//        mSwipeRefreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
//        Log.i("System.out","///////////"+mSwipeRefreshLayout);
//        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R
//                .color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.i("System.out", "///////////shuaxin");
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        });
    }



    //设置适配器
    private void setAdapter() {
        adapter = new NewsAdapter(list_news, getActivity());
        news.setAdapter(adapter);
        news.setLayoutManager(new LinearLayoutManager(getActivity()));
        //----
        news.addOnItemTouchListener(new FragmentDrawer.RecyclerTouchListener(getActivity(), news, new FragmentDrawer.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                getNewsDetail(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    //查看公告详情
    public void getNewsDetail(int position) {
        Intent intent = new Intent(getActivity(), NewsActivity.class);
//        String id_selected = list_news.get(position).getObjectId();
//        intent.putExtra("id_selected", id_selected);
        //this.getActivity().finish();

        News news=new News();
        news.setTitle(list_news.get(position).getTitle());
        news.setContent(list_news.get(position).getContent());
        news.setAuthor(list_news.get(position).getAuthor());
        Bundle bundle=new Bundle();
        bundle.putParcelable("selected_news",news);
        bundle.putString("time", list_news.get(position).getCreatedAt());
        intent.putExtras(bundle);

        startActivity(intent);
    }

    //添加公告
    public void addNews() {
        Intent intent = new Intent(getActivity(), AddNewsActivity.class);
        startActivity(intent);
    }


    //-------------------------------------------------------------------
    //本地数据库操作
    /*
    获取缓存中的联系人数据
     */
    private void getDataInCache() {
        try {
            db = DbUtils.create(getActivity());
            if (db.tableIsExist(News.class)) {
                list_news = db.findAll(News.class);
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
    private void saveDataInCache(List<News> listData) {
        try {
            db = DbUtils.create(getActivity());
            if (!db.tableIsExist(News.class)) {
                db.createTableIfNotExist(News.class);
            } else {
                db.deleteAll(News.class);
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
            HttpUtil.getNewsListDataInNet(getActivity(), mHandler);
        }
    }

    //获取后台数据
    private List<News> listData = new ArrayList<News>();
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
                    listData = (List<News>) msg.obj;
                    //刷新适配器
                    adapterRefresh(listData);
                    //保存到本地缓存
                    saveDataInCache(listData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    //刷新适配器
    private void adapterRefresh(List<News> listData) {
        if (null == list_news) {
            //空项实例化之后添加新数据
            list_news = new ArrayList<News>();
            if (null != listData) {
                list_news.addAll(listData);
            }
            //数据源改变，通过创建方式刷新适配器（需要new一个适配器）
            setAdapter();
        } else {
            //清空数据，添加新数据
            if (!list_news.isEmpty()) {
                list_news.clear();
            }
            if (null != listData) {
                list_news.addAll(listData);
            }
            if (null != adapter) {
                //通过刷新数据方式刷新适配器（不会重新new适配器）
                adapter.notifyDataSetChanged();
            } else {
                setAdapter();
            }
        }
    }

}