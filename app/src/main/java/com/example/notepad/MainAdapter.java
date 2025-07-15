package com.example.notepad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MainAdapter extends BaseAdapter {
    Context context;
    List<Data> mDatas;

    public MainAdapter(Context context, List<Data> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MainAdapter.ViewHolder holder = null;
        View convertView = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list,null);
            holder = new MainAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (MainAdapter.ViewHolder) convertView.getTag();
        }
        Data d=mDatas.get(i);
        holder.tv_title.setText(d.getTitle().toString());
        holder.tv_data.setText(d.getData().toString());
        return convertView;
    }
    class ViewHolder{
        TextView tv_title,tv_data;
        public ViewHolder(View itemView) {
            tv_title=itemView.findViewById(R.id.item_tv_title);
            tv_data=itemView.findViewById(R.id.item_tv_data);
        }
    }
}
