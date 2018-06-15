package zxl.com.test_qsbk;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by uidq0955 on 2018/6/15.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
