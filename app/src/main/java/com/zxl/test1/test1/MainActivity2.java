package com.zxl.test1.test1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);
        tv.setText("--->2");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("--->2--->2");
                Intent mIntent = new Intent(MainActivity2.this,MainActivity3.class);
                //mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mIntent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        System.out.println("zxl--->MainActivity2--->onNewIntent");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("zxl--->MainActivity2--->onPause");
        Intent mIntent = new Intent(this,MainActivity2.class);
        startActivity(mIntent);
    }
}
