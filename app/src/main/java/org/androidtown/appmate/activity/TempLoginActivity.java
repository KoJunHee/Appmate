package org.androidtown.appmate.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;


import org.androidtown.appmate.R;

public class TempLoginActivity extends AppCompatActivity {


    //로딩 화면이 떠있는 시간(밀리초단위)
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_login);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                //메뉴액티비티를 실행하고 로딩화면을 죽인다
                Intent mainIntent = new Intent(TempLoginActivity.this, ProjectHomeActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
