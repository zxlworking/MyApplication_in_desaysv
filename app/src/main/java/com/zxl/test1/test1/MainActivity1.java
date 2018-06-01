package com.zxl.test1.test1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity1 extends AppCompatActivity {

    private TextView tv;
    private ProgressBar progress;

    private Activity mActivity;

    private ITest mITest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        mITest = new ITest() {
            @Override
            public void print() {
                System.out.println("zxl--->"+mActivity.getPackageName());
            }
        };

        tv = (TextView) findViewById(R.id.tv);
        tv.setText("--->1");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MainActivity1.this,MainActivity2.class);
                //mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mIntent);
            }
        });

        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setIndeterminate(true);

        getInstalledAppInfo();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mITest.print();
            }
        });


    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public void getInstalledAppInfo() {
        PackageManager pm = getApplicationContext().getPackageManager();
        List<ApplicationInfo> installedList = pm.getInstalledApplications(0);
        for (ApplicationInfo appInfo : installedList) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                continue;
            } else {
                if ((appInfo.publicSourceDir != null)
                        && (!appInfo.packageName.trim().equals(""))
                        && (appInfo.packageName != null)
                        && (!appInfo.packageName.trim().equals(""))) {
                    String name = appInfo.packageName;
                    // 获取应用图标资源
                    Drawable icon = appInfo.loadIcon(pm);
                    //mAppInstalledBean.setIcon(drawableToBitmap(icon));
                    // 获取应用名称
                    String appName = pm.getApplicationLabel(appInfo).toString();
                    // 获取应用版本号
                    PackageInfo packageInfo;
                    try {
                        packageInfo = pm.getPackageInfo(appInfo.packageName, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    interface ITest{
        void print();
    }
}
