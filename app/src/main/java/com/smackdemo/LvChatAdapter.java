package com.smackdemo;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LvChatAdapter extends BaseAdapter {

    private List<MsgBean> msgList;
    private Context context;

    public LvChatAdapter(List<MsgBean> msgList, Context context) {
        this.msgList = msgList;
        this.context = context;
    }

    public void setMsgList(List<MsgBean> msgList) {
        this.msgList = msgList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return msgList == null ? 0 : msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_list, null);
            holder = new ViewHolder();
            holder.tv_con = convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (msgList.get(position).getMsgFrom() == 0) {
            holder.tv_con.setGravity(Gravity.START);
            holder.tv_con.setTextColor(Color.GREEN);
        } else {
            holder.tv_con.setGravity(Gravity.END);
            holder.tv_con.setTextColor(Color.BLUE);
        }
        holder.tv_con.setText(msgList.get(position).getMsg());
        return convertView;
    }

    class ViewHolder {
        TextView tv_con;
    }
}
