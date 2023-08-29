package com.example.bottomnavigationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置初始页面
        setDefaultFragment();

        //此处承接登录界面传过来的用户id与用户名
        Intent intent = getIntent();
        long userId = intent.getLongExtra("userId",0);
        String username = intent.getStringExtra("username");


        Toast.makeText(MainActivity.this,username,Toast.LENGTH_SHORT).show();


        BottomNavigationView btmNavView = findViewById(R.id.bottom_navigation_menu);
        btmNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                fragmentManager = getSupportFragmentManager();  //使用fragmentmanager和transaction来实现切换效果
                transaction = fragmentManager.beginTransaction();

                if(item.getItemId() == R.id.navigation_home)
                {
                    transaction.replace(R.id.content,new HomeFragment());
                    transaction.commit();
                    Toast.makeText(MainActivity.this,"home",Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (item.getItemId() == R.id.navigation_focus){

                    Toast.makeText(MainActivity.this,"focus",Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if(item.getItemId() == R.id.navigation_share)
                {
                    transaction.replace(R.id.content,new NewShareFrgment());
                    transaction.commit();
                    Toast.makeText(MainActivity.this,"share",Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if(item.getItemId() == R.id.navigation_me)
                {

                    Toast.makeText(MainActivity.this,"me",Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    private void setDefaultFragment(){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content,new HomeFragment());
        transaction.commit();
    }

}