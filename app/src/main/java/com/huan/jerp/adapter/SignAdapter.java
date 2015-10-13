package com.huan.jerp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huan.jerp.R;
import com.huan.jerp.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/10/9.
 */
public class SignAdapter extends BaseExpandableListAdapter {
    public  List<String> department=new ArrayList<String>();
    public  List<List<User>> everyone=new ArrayList<List<User>>();

    public static int selected_item;

    LayoutInflater mInflater;
    Context context;

    public SignAdapter(final Context context,List<User> users) {
        // TODO Auto-generated constructor stub
        mInflater = LayoutInflater.from(context);
        this.context = context;
        department.clear();
        everyone.clear();
        for (User u:users) {
            if(null==department||department.size()==0||!department.contains(u.getDepartment())){
                department.add(u.getDepartment());
            }else {
                System.out.println("2222");
            }
        }
        System.out.println(department);

        for(int i=0;i<department.size();i++){
            List<User> userlist=new ArrayList<User>();
            for (User u:users) {
                if(u.getDepartment().equals(department.get(i))){
                    userlist.add(u);
                }
            }
            everyone.add(userlist);
        }

    }
    @Override
    public int getGroupCount() {
        return department.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return everyone.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return department.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return everyone.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            mViewChild = new ViewChild();
            convertView = mInflater.inflate(R.layout.department_item, null);
            mViewChild.textView = (TextView) convertView.findViewById(R.id.channel_group_name);
            mViewChild.imageView = (ImageView) convertView.findViewById(R.id.channel_imageview_orientation);
            convertView.setTag(mViewChild);
        } else {
            mViewChild = (ViewChild) convertView.getTag();
        }

        if (isExpanded) {
            mViewChild.imageView.setImageResource(R.drawable.ic_chevron_left_black_18dp);
        } else {
            mViewChild.imageView.setImageResource(R.drawable.ic_chevron_right_black_18dp);
        }
        mViewChild.textView.setText(getGroup(groupPosition).toString());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            mViewChild = new ViewChild();
            convertView = mInflater.inflate(R.layout.name_listbiew, null);
            mViewChild.textView = (TextView) convertView.findViewById(R.id.name_list);
            convertView.setTag(mViewChild);
        } else {
            mViewChild = (ViewChild) convertView.getTag();
        }
        mViewChild.textView.setText(getNameList(everyone.get(groupPosition))[childPosition]);
//        SimpleAdapter mSimpleAdapter = new SimpleAdapter(context, setListViewData(getNameList(everyone.get(groupPosition))), R.layout.name_list_item,
//                new String[] { "name_list" }, new int[] { R.id.listview_item });
//        mViewChild.listView.setAdapter(mSimpleAdapter);

//        setGridViewListener(mViewChild.gridView);
 //       setListViewLisenner(mViewChild.listView);
//        mViewChild.listView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        return convertView;
    }

//    private void setListViewLisenner(ListView listView) {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView textView= (TextView) view;
//                Toast.makeText(context,everyone.get(selected_item).get(position).getObjectId(),Toast.LENGTH_SHORT).show();
//                Intent t=new Intent(context, SignOtherActivity.class);
//                t.putExtra("id",everyone.get(selected_item).get(position).getObjectId());
//                context.startActivity(t);
//            }
//        });
//    }


    /**
     * 设置listview数据
     *
     * @param data
     * @return
     */
    private ArrayList<HashMap<String, Object>> setListViewData(String[] data) {
        ArrayList<HashMap<String, Object>> gridItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < data.length; i++) {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("name_list", data[i]);
            gridItem.add(hashMap);
        }
        return gridItem;
    }

    public String[] getNameList(List<User> e){
        List<String> s=new ArrayList<String>();
        for (User u:e) {
            s.add(u.getName());
        }
        return s.toArray(new String[s.size()]);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    ViewChild mViewChild;

    static class ViewChild {
        ImageView imageView;
        TextView textView;
        ListView listView;
    }
}
