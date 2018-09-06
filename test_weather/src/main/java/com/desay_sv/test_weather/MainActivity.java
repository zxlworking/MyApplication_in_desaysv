package com.desay_sv.test_weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.desay_sv.test_weather.event.LocatePermissionSuccessEvent;
import com.desay_sv.test_weather.event.RequestLocatePermissionEvent;
import com.desay_sv.test_weather.utils.EventBusUtils;
import com.zxl.common.DebugUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private ImageView img1;
    private ImageView img2;

    private SimpleTarget<Bitmap> mBitmapSimpleTarget1 = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            img1.setImageBitmap(resource);
            GlideApp.with(MainActivity.this).asBitmap().load("http://i.tq121.com.cn/i/weather2015/png/blue80.png").into(mBitmapSimpleTarget2);
        }
    };

    private SimpleTarget<Bitmap> mBitmapSimpleTarget2 = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            Bitmap bitmap = Bitmap.createBitmap(80,80, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Rect rectRes = new Rect(160, 0,160 + 80, 80);
            Rect rectDst = new Rect(0,0,bitmap.getWidth(), bitmap.getHeight());

            canvas.drawBitmap(resource,rectRes,rectDst,new Paint());

            img2.setImageBitmap(bitmap);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBusUtils.register(this);

        DebugUtil.d(TAG,"getInstance");

        setContentView(R.layout.activity_main);

        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);

        //GlideApp.with(this).asBitmap().load("http://i.tq121.com.cn/i/weather2015/png/blue80.png").into(mBitmapSimpleTarget1);
    }

    private void requestLocatePermission() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionOk = true;
        if (requestCode == 1) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isPermissionOk = false;
                    break;
                }
            }
        }
        if(isPermissionOk){
            EventBusUtils.post(new LocatePermissionSuccessEvent());
        }
        DebugUtil.d(TAG,"onRequestPermissionsResult::isPermissionOk = " + isPermissionOk);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtils.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestDoLocateEvent(RequestLocatePermissionEvent event){
        requestLocatePermission();
    }
}
