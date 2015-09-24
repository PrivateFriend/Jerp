package com.huan.jerp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huan.jerp.R;
import com.huan.jerp.data.News;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/8/24.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder> {

    List<News> news= Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NewsAdapter(List<News> news, Context context) {
        this.news = news;
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    //删除新闻
    public void delete(int position){
        news.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public NewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view=inflater.inflate(R.layout.news_structor,viewGroup,false);
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NewsAdapter.MyViewHolder viewHolder, int i) {

        News newsItem=news.get(i);
        viewHolder.title.setText(newsItem.getTitle());
        viewHolder.content.setText(newsItem.getContent());
        viewHolder.author.setText(newsItem.getAuthor());
        viewHolder.time.setText(newsItem.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;
        TextView author;
        TextView time;
        public MyViewHolder(View itemView) {
            super(itemView);

            title= (TextView) itemView.findViewById(R.id.title);
            content= (TextView) itemView.findViewById(R.id.content);
            author= (TextView) itemView.findViewById(R.id.author);
            time= (TextView) itemView.findViewById(R.id.time);
        }
    }
}
