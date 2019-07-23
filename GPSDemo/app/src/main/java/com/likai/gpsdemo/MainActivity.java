package com.likai.gpsdemo;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideSystemBar();//隐藏状态栏+导航栏+标题栏
    }

    public void hideSystemBar() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            android.view.View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(android.view.View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            android.view.View decorView = getWindow().getDecorView();
            int uiOptions =
                    android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                            | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
