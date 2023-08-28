package com.example.bottomnavigationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView btmNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btmNavView = findViewById(R.id.bottom_navigation_menu);

        btmNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navigation_home)
                {
                    Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if (item.getItemId() == R.id.navigation_test){
                    Toast.makeText(MainActivity.this, "test", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else if(item.getItemId() == R.id.navigation_info)
                {
                    Toast.makeText(MainActivity.this, "info", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }
}