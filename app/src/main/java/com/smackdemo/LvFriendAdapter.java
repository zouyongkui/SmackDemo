package com.smackdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LvFriendAdapter extends BaseAdapter {

    private List<FriendInfoBean> friendList;
    private Context context;

    public LvFriendAdapter(List<FriendInfoBean> friendList, Context context) {
        this.friendList = friendList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return friendList == null ? 0 : friendList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_friend, null);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(friendList.get(position).getName());
        return convertView;
    }

    class ViewHolder {

        TextView tv_name;

    }
}
