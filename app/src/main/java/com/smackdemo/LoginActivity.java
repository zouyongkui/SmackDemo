package com.smackdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ChatUtil.LoginListener {

    private EditText et_account, et_psw;
    private Button btn_login;
    private CheckBox checkBox;
    private String account;
    private String psw;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        account = SPUtils.getInstance().getString(Constant.USR_ID);
        psw = SPUtils.getInstance().getString(Constant.USR_PSW);

        et_account = findViewById(R.id.et_account);
        et_psw = findViewById(R.id.et_psw);
        btn_login = findViewById(R.id.btn_login);
        checkBox = findViewById(R.id.cb_isRem);
        btn_login.setOnClickListener(this);

        et_account.setText(account);
        et_psw.setText(psw);
    }

    @Override
    public void onClick(View v) {
        account = et_account.getText().toString().trim();
        psw = et_psw.getText().toString().trim();
        ChatUtil.getInstance().connectServer(account, psw).setLoginListener(this);
        if (checkBox.isChecked()) {
            SPUtils.getInstance().put(Constant.USR_ID, account);
            SPUtils.getInstance().put(Constant.USR_PSW, psw);
        }
    }

    @Override
    public void onSuc() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, FriendListActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onError(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, "登陆失败！ " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
