package com.smackdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IncomingChatMessageListener, View.OnClickListener {

    private String TAG = "smackDemo";

    private EditText et_input;

    private Button btn_send;

    private List<MsgBean> msgList;
    private LvChatAdapter adapter;
    private ListView lv_con;
    private ChatUtil chatUtil;
    private String friendId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        friendId = getIntent().getStringExtra(Constant.FRIEND_ID);
        if (TextUtils.isEmpty(friendId)) {
            friendId = "120@ykco";
        }
        initView();
    }

    private void initView() {
        chatUtil = ChatUtil.getInstance();
        chatUtil.addChatListener(this);
        msgList = new ArrayList<>();
        adapter = new LvChatAdapter(msgList, this);
        et_input = findViewById(R.id.et_input);
        btn_send = findViewById(R.id.btn_send);
        lv_con = findViewById(R.id.lv_content);
        lv_con.setAdapter(adapter);
        btn_send.setOnClickListener(this);
    }


    @Override
    public void newIncomingMessage(EntityBareJid from, final Message message, Chat chat) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = message.getBody();
                MsgBean msgBean = new MsgBean();
                msgBean.setMsgFrom(1);
                msgBean.setMsg(msg);
                msgList.add(msgBean);
                adapter.setMsgList(msgList);
                lv_con.smoothScrollToPosition(msgList.size());
            }
        });

    }

    @Override
    public void onClick(View v) {
        String msg = et_input.getText().toString().trim();
        chatUtil.sendMsg(msg, friendId);
        MsgBean msgBean = new MsgBean();
        msgBean.setMsg(msg);
        msgBean.setMsgFrom(0);
        msgList.add(msgBean);
        adapter.setMsgList(msgList);
        lv_con.smoothScrollToPosition(msgList.size());
        et_input.setText("");
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
