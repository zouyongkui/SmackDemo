package com.smackdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView lv_friend;
    private LvFriendAdapter adapter;
    private List<FriendInfoBean> friendList;
    private Button btnAddFriend;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        btnAddFriend = findViewById(R.id.btn_addFriend);
        btnAddFriend.setOnClickListener(this);
        lv_friend = findViewById(R.id.lv_friend);
        friendList = ChatUtil.getInstance().getFriendList();
        adapter = new LvFriendAdapter(friendList, this);
        lv_friend.setAdapter(adapter);

        lv_friend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FriendListActivity.this, MainActivity.class);
                intent.putExtra(Constant.FRIEND_ID, friendList.get(position).getId());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        ChatUtil.getInstance().disConnect();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("请输入好友id")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = editText.getText().toString();
                        ChatUtil.getInstance().addFriend(str + "@" + Constant.XMPP_HOST, str);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
