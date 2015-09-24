package com.huan.jerp.fragment;

/**
 * Created by Andy on 2015/5/28.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huan.jerp.R;
import com.huan.jerp.activity.AddNewsActivity;
import com.huan.jerp.activity.NewsActivity;
import com.huan.jerp.adapter.NewsAdapter;
import com.huan.jerp.data.News;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;


public class HomeFragment extends Fragment{

    private CardRecyclerView news;
    private NewsAdapter adapter;
    private static String[] titles = null;
    private FloatingActionButton bt_submit;
    private List<News> data ;


    public HomeFragment() {
        // Required empty public constructor
    }

    //获取所显示的条目
    public  List<News> getData() {

        final List<News> data = new ArrayList<>();

        BmobQuery<News> query=new BmobQuery<News>();
        //新闻条目数量，默认为10
        query.setLimit(100);
        query.findObjects(this.getActivity(), new FindListener<News>() {
            @Override
            public void onSuccess(List<News> list) {
                data.addAll(list);

                adapter=new NewsAdapter(data,getActivity());
                news.setAdapter(adapter);
                news.setLayoutManager(new LinearLayoutManager(getActivity()));

                news.addOnItemTouchListener(new FragmentDrawer.RecyclerTouchListener(getActivity(), news, new FragmentDrawer.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        getNews(position);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        bt_submit= (FloatingActionButton) rootView.findViewById(R.id.bt_add);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNews();
            }
        });

        news= (CardRecyclerView) rootView.findViewById(R.id.news);
        data=new ArrayList<News>();
        data=getData();

        return rootView;
    }




    public void getNews(int position){
                Intent intent=new Intent(getActivity(), NewsActivity.class);
                String id_selected=data.get(position).getObjectId();
                intent.putExtra("id_selected", id_selected);
                startActivity(intent);
                //this.getActivity().finish();
    }

    public void addNews(){
        Intent intent=new Intent(getActivity(), AddNewsActivity.class);
        startActivity(intent);
    }

}
