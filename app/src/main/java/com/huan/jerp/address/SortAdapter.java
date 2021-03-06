package com.huan.jerp.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.huan.jerp.R;
import com.huan.jerp.View.CircleTransformation;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2015/8/17.
 * //在此处修改列表显示效果
 */
public class SortAdapter extends BaseAdapter implements SectionIndexer {
    private List<SortModel> list = null;
    private Context mContext;

    public SortAdapter(Context mContext, List<SortModel> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     *
     * @param list
     */
    public void updateListView(List<SortModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final SortModel mContent = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            //该处为列表项的显示效果
            view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.ivHead= (ImageView) view.findViewById(R.id.head);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        int section = getSectionForPosition(position);


        if(position == getPositionForSection(section)){
//            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setVisibility(View.GONE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        }else{
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        viewHolder.tvTitle.setText(this.list.get(position).getName());
        if(null!=list.get(position).getHead()&&!list.get(position).getHead().isEmpty()){
            Picasso.with(mContext).load(list.get(position).getHead()).transform(new CircleTransformation()).into(viewHolder.ivHead);
        }else {
            Picasso.with(mContext).load(R.mipmap.ic_user).transform(new CircleTransformation()).into(viewHolder.ivHead);
        }

        return view;

    }



    final static class ViewHolder {
        TextView tvLetter;
        TextView tvTitle;
        ImageView ivHead;
    }



    public int getSectionForPosition(int position) {
        return list.get(position).getSortLetters().charAt(0);
    }


    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     *
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();

        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
