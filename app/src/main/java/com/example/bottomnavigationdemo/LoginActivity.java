package com.example.bottomnavigationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottomnavigationdemo.MainActivity;
import com.example.bottomnavigationdemo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {

    private boolean btnPwdSwitch = false;
    private EditText etPwd;
    private EditText etAccount;
    private CheckBox cbRememberPwd;

    private Button btLogin;
    private TextView tvSignUp;

    private final Gson gson = new Gson();
    ResponseBody<Object> dataResponseBody = null;
    ResposeData dataResposeData = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        etPwd = findViewById(R.id.et_pwd);
        etAccount = findViewById(R.id.et_account);
        cbRememberPwd = findViewById(R.id.cb_remember_pwd);
        btLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_sign_up);
        btLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);

        //用于设置密码可见性
        ivPwdSwitch.setOnClickListener(view -> {
            btnPwdSwitch = !btnPwdSwitch;
            if (btnPwdSwitch) {
                ivPwdSwitch.setImageResource(R.drawable.baseline_visibility_24);
                etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                ivPwdSwitch.setImageResource(R.drawable.baseline_visibility_off_24);
                etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                        InputType.TYPE_CLASS_TEXT);
                etPwd.setTypeface(Typeface.DEFAULT);
            }
        });
        //从文件中读取保存的东西
        loadSavedData();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == btLogin.getId()) {
            editSavedData();
            try {
                tryLogin();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (id == tvSignUp.getId()) {
            Toast.makeText(LoginActivity.this, "Sign Up", Toast.LENGTH_SHORT).show();
        }
    }

    private void tryLogin() throws InterruptedException {
        //页面跳转
        String account = etAccount.getText().toString();
        String password = etPwd.getText().toString();
        if(!account.equals("") && !password.equals(""))
        {
            post(account, password);
        }
        else
        {
            Toast.makeText(LoginActivity.this,getResources().getString(R.string.emptyAccountOrPassword),Toast.LENGTH_SHORT).show();
            return;
        }
        Thread.sleep(500);
        if(dataResponseBody == null || dataResposeData == null)
        {
            Toast.makeText(LoginActivity.this,getResources().getString(R.string.apiErrorTimeout),Toast.LENGTH_SHORT).show();
            return;
        }
        switch(dataResponseBody.code){
            case 401: Toast.makeText(LoginActivity.this,getResources().getString(R.string.resourceUnauthorized),Toast.LENGTH_SHORT).show();break;
            case 404: Toast.makeText(LoginActivity.this,getResources().getString(R.string.resourceUnfound),Toast.LENGTH_SHORT).show();break;
            case 500: Toast.makeText(LoginActivity.this,dataResponseBody.msg,Toast.LENGTH_SHORT).show();break;
            case 5217: Toast.makeText(LoginActivity.this,getResources().getString(R.string.permissionError),Toast.LENGTH_SHORT).show();break;
            case 5311: Toast.makeText(LoginActivity.this,getResources().getString(R.string.apiTimesError),Toast.LENGTH_SHORT).show();break;
            case 5314: Toast.makeText(LoginActivity.this,getResources().getString(R.string.apiParamError),Toast.LENGTH_SHORT).show();break;
            case 200: Toast.makeText(LoginActivity.this,getResources().getString(R.string.loginSuccess),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(LoginActivity.this,MainActivity.class);
                intent.putExtra("userId",dataResposeData.id);
                intent.putExtra("username",dataResposeData.username);
                startActivity(intent);
                Log.d("Intent", "Starting MainActivity with userId: " + dataResposeData.id);
                break;
        }


    }



    private void post(String account, String password) {
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/user/login?password=" + password + "&username=" + account;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", getResources().getString(R.string.appId))
                    .add("appSecret", getResources().getString(R.string.appSecrect))
                    .add("Content-Type", "application/json")
                    .build();


            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<ResponseBody<Object>>() {
            }.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            dataResponseBody = gson.fromJson(body, jsonType);
            Log.d("info", dataResponseBody.toString());
            if(dataResponseBody.code != 200)
            {
                return;
            }
            jsonType = new TypeToken<ResposeData>(){}.getType();
            dataResposeData = gson.fromJson(dataResponseBody.getData().toString(), jsonType);
            Log.d("MyInfo",dataResposeData.toString());
        }
    };


    public static class ResponseBody<T> {

        /**
         * 业务响应码
         */
        private int code;
        /**
         * 响应提示信息
         */
        private String msg;
        /**
         * 响应数据
         */
        private T data;

        public ResponseBody() {
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public T getData() {
            return data;
        }

        @NonNull
        @Override
        public String toString() {
            return "ResponseBody{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

    private static class ResposeData {
        public String appKey;
        public String avatar;
        public long createTime;
        public long id;
        public String introduce;
        public long lastUpdateTime;
        public String password;
        public int sex;
        public String username;

        @NonNull
        @Override
        public String toString() {
            return "data{" +
                    "appKey=" + appKey +
                    ", avatar=" + avatar +
                    ", ceateTime=" + createTime +
                    ", id=" + id +
                    ", introduce=" + introduce +
                    ", lastUpdateTime=" + lastUpdateTime +
                    ", password=" + password +
                    ", sex=" + sex +
                    ", username=" + username +
                    '}';
        }
    }
    private void loadSavedData() {
        //从string.xml中获取键值对的key值
        String spFileName = getResources().getString(R.string.shared_perferince_file_name);
        String accountKey = getResources().getString(R.string.login_account_name);
        String passwordKey = getResources().getString(R.string.login_password);
        String rememberPasswordKey = getResources().getString(R.string.login_remenber_password);
        //使用SharedPreference类来管理数据
        SharedPreferences spFile = getSharedPreferences(spFileName, MODE_PRIVATE);
        //使用其get方法根据键值来读取数据。
        //因为只需读取，不需要编辑，因此不使用Editor
        //用法：（键值，若不存在该键值时的返回值）
        String account = spFile.getString(accountKey, null);
        String password = spFile.getString(passwordKey, null);
        Boolean rememberPassword = spFile.getBoolean(rememberPasswordKey, false);

        //若返回的对象非空且读出的东西不是空的，则向文本框内设置值
        if (account != null && !TextUtils.isEmpty(account)) {
            etAccount.setText(account);
        }
        if (password != null && !TextUtils.isEmpty(password)) {
            etPwd.setText(password);
        }
        //但不管怎样，都需要设置复选框的状态，因为默认值已经设置为false
        cbRememberPwd.setChecked(rememberPassword);

    }
    private void editSavedData() {
        //从string.xml中获取键值对的key值
        String spFilename = getResources().getString(R.string.shared_perferince_file_name);
        String accountKey = getResources().getString(R.string.login_account_name);
        String passwordKey = getResources().getString(R.string.login_password);
        String rememberPasswordKey = getResources().getString(R.string.login_remenber_password);

        //使用SharedPreference类来管理数据
        //用法：（文件名，打开模式）
        //关于打开模式，有以下几个常用选项：
        //MODE_PRIVATE：默认选项，代表文件是私有，只能由当前应用访问，且是覆写模式
        //MODE_APPEND:追加写入模式
        //MODE_WORLD_READALE：表示可以被其他应用读取
        //MODE_WORLD_WRITEABLE：表示可以被其他应用写入
        //要是想支持公用读写，可以在中间加个 + 号连接
        SharedPreferences spFile = getSharedPreferences(spFilename, Context.MODE_PRIVATE);
        //使用SharedPreference的Editor接口快速存储数据
        SharedPreferences.Editor editor = spFile.edit();

        if (cbRememberPwd.isChecked()) {
            String password = etPwd.getText().toString();
            String account = etAccount.getText().toString();

            //写入键值对
            editor.putString(accountKey, account);
            editor.putString(passwordKey, password);
            editor.putBoolean(rememberPasswordKey, true);
            //写入缓存
            editor.apply();
        } else {
            //移除键值对
            editor.remove(accountKey);
            editor.remove(passwordKey);
            editor.remove(rememberPasswordKey);
            //写入缓存
            editor.apply();
        }
    }
}
