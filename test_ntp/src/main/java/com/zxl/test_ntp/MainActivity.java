package com.zxl.test_ntp;

import android.icu.text.SimpleDateFormat;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SntpTime mSntpTime = new SntpTime("85.199.214.101",10000,new Handler(),1);
//        mSntpTime.getTime();

        final SntpClientInfo mSntpClientInfo = new SntpClientInfo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSntpClientInfo.requestTime("85.199.214.101",30000);
            }
        }).start();

        long t = SystemClock.elapsedRealtime();
        System.out.println("zxl--->t--->"+t);
    }
}
