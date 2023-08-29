package com.example.bottomnavigationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {


    private boolean btnPwdSwitch = false;

    private final Gson gson = new Gson();
    public ResponseBody<Object> dataResponseBody = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final ImageView ivPwdSwitch = findViewById(R.id.iv_signUp_pwd_switch_again);
        EditText etPwd = findViewById(R.id.et_signUp_pwd);
        EditText etPwdAgain = findViewById(R.id.et_signUp_pwd_again);
        EditText etAccount = findViewById(R.id.et_signUp_account);
        Button btSignUp = findViewById(R.id.btn_signUp_signUp);
        //
        btSignUp.setOnClickListener(view -> {
            if(etPwd.getText().toString().length() == 0 || etPwdAgain.getText().toString().length() == 0 || etAccount.getText().toString().length() == 0)
            {
                Toast.makeText(SignUpActivity.this,getResources().getString(R.string.emptyAccountOrPassword),Toast.LENGTH_SHORT).show();
            }
            else if(!etPwd.getText().toString().equals(etPwdAgain.getText().toString()))
            {
                Toast.makeText(SignUpActivity.this,getResources().getString(R.string.unmatchPassword),Toast.LENGTH_SHORT).show();
            }
            else
            {
                post(etAccount.getText().toString(),etPwd.getText().toString());
                //sleep500毫秒，抵消延迟
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //对响应结果进行处理
                if(dataResponseBody == null)
                {
                    Toast.makeText(SignUpActivity.this,getResources().getString(R.string.apiErrorTimeout),Toast.LENGTH_SHORT).show();
                }
                switch (dataResponseBody.code)
                {
                    case 401: Toast.makeText(SignUpActivity.this,getResources().getString(R.string.resourceUnauthorized),Toast.LENGTH_SHORT).show();break;
                    case 404: Toast.makeText(SignUpActivity.this,getResources().getString(R.string.resourceUnfound),Toast.LENGTH_SHORT).show();break;
                    case 500: Toast.makeText(SignUpActivity.this,dataResponseBody.msg,Toast.LENGTH_SHORT).show();break;
                    case 5217: Toast.makeText(SignUpActivity.this,getResources().getString(R.string.permissionError),Toast.LENGTH_SHORT).show();break;
                    case 5311: Toast.makeText(SignUpActivity.this,getResources().getString(R.string.apiTimesError),Toast.LENGTH_SHORT).show();break;
                    case 5314: Toast.makeText(SignUpActivity.this,getResources().getString(R.string.apiParamError),Toast.LENGTH_SHORT).show();break;
                    case 200: Toast.makeText(SignUpActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Intent intent=new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(SignUpActivity.this,LoginActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });


        //用于设置密码可见性
        ivPwdSwitch.setOnClickListener(view -> {
            btnPwdSwitch = !btnPwdSwitch;
            if (btnPwdSwitch) {
                ivPwdSwitch.setImageResource(R.drawable.baseline_visibility_24);
                etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etPwdAgain.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                ivPwdSwitch.setImageResource(R.drawable.baseline_visibility_off_24);
                etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                        InputType.TYPE_CLASS_TEXT);
                etPwd.setTypeface(Typeface.DEFAULT);
                etPwdAgain.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                        InputType.TYPE_CLASS_TEXT);
                etPwdAgain.setTypeface(Typeface.DEFAULT);
            }
        });



    }

    private void post(String username, String password){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/user/register";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", getResources().getString(R.string.appId))
                    .add("appSecret", getResources().getString(R.string.appSecrect))
                    .add("Content-Type", "application/json")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("username", username);
            bodyMap.put("password", password);
            // 将Map转换为字符串类型加入请求体中
            String body = gson.toJson(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);
            }catch (NetworkOnMainThreadException ex){
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
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());
        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class ResponseBody <T> {

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

        public ResponseBody(){}

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



}