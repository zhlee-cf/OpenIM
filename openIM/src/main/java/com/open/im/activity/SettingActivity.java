package com.open.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.open.im.R;
import com.open.im.service.IMService;

/**
 * 个人设置界面
 * Created by Administrator on 2016/4/8.
 */
public class SettingActivity extends Activity implements View.OnClickListener {

    private RelativeLayout rl_change_pwd;
    private RelativeLayout rl_clean;
    private Button btn_logout;
    private SettingActivity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        act = this;
        initView();

        register();
    }

    private void register() {
        rl_change_pwd.setOnClickListener(this);
        rl_clean.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
    }

    private void initView() {
        rl_change_pwd = (RelativeLayout) findViewById(R.id.rl_change_pwd);
        rl_clean = (RelativeLayout) findViewById(R.id.rl_clean);
        btn_logout = (Button) findViewById(R.id.btn_logout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_change_pwd:
                act.startActivity(new Intent(act, UpdatePasswordActivity.class));
                break;
            case R.id.rl_clean:
                act.startActivity(new Intent(act,CleanCacheActivity.class));
                break;
            case R.id.btn_logout:
                // 注销登录时，退出应用，关闭服务
                IMService.getInstance().stopSelf();
                Intent loginIntent = new Intent(act, ReLoginActivity.class);
                act.startActivity(loginIntent);
                act.finish();
                break;
        }
    }
}