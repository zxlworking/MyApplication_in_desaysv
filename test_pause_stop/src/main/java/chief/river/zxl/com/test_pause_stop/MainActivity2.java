package chief.river.zxl.com.test_pause_stop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        System.out.println("zxl--->MainActivity2--->onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("zxl--->MainActivity2--->onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("zxl--->MainActivity2--->onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("zxl--->MainActivity2--->onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("zxl--->MainActivity2--->onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("zxl--->MainActivity2--->onDestroy");
    }
}
