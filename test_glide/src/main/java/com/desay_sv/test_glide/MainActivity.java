package com.desay_sv.test_glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class MainActivity extends AppCompatActivity {

    private Bitmap b_dst_1;
    private Bitmap b_dst_2;

    private ImageView img;
    private ImageView img1;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = "http://i.tq121.com.cn/i/weather2015/city/iconall.png";

        img = findViewById(R.id.img);
        img1 = findViewById(R.id.img1);

        /*
        "img":"http://i.tq121.com.cn/i/weather2015/city/iconall.png",
        "background_position_x1":"-35",
        "background_position_y1":"-137",
        "width1":"15",
        "height1":"8",
        "background_position_x2":"-35",
        "background_position_y2":"-142",
        "width2":"15",
        "height2":"64.7969"
         */


        GlideApp.with(this).asBitmap().load(url).into(simpleTarget1);
    }

    SimpleTarget<Bitmap> simpleTarget1 = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {


            Bitmap b1 = Bitmap.createBitmap(80,142 - 8, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b1);

            Rect rectRes = new Rect(0,137 - 8,80,137 + 142 - 8);
            Rect rectDst = new Rect(0,0,b1.getWidth(),b1.getHeight());
            canvas.drawBitmap(resource,rectRes,rectDst,new Paint());

//            b_dst_1 = Bitmap.createBitmap(img.getWidth(),img.getHeight(), Bitmap.Config.ARGB_8888);
//            canvas = new Canvas(b_dst_1);
//            canvas.drawBitmap(b1,img.getWidth()/2 - b1.getWidth()/2,img.getHeight()/2 - b1.getHeight()/2,new Paint());

            b_dst_1 = b1;

            img.setImageBitmap(b1);
            GlideApp.with(MainActivity.this).asBitmap().load(url).into(simpleTarget2);
        }
    };

    SimpleTarget<Bitmap> simpleTarget2 = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            Bitmap b2 = Bitmap.createBitmap(14, (int) (137 - 38 - 64.7969), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b2);

            Rect rectRes = new Rect(36, (int) 64.7969,50, 137 - 38);
            Rect rectDst = new Rect(0,0,b2.getWidth(), b2.getHeight());
            canvas.drawBitmap(resource,rectRes,rectDst,new Paint());

            //b_dst_2 = Bitmap.createBitmap(img.getWidth(),img.getHeight(), Bitmap.Config.ARGB_8888);
//            canvas = new Canvas(b_dst_2);
            //canvas.drawBitmap(b_dst_1,img.getWidth()/2 - 15/2,0,new Paint());
//            canvas.drawBitmap(b2,img.getWidth()/2 - 15/2,0,new Paint());
            canvas = new Canvas(b_dst_1);
            rectRes = new Rect(0,0,b2.getWidth(), b2.getHeight());
            rectDst = new Rect(b_dst_1.getWidth()/2 - 4,0,b_dst_1.getWidth()/2 + b2.getWidth() - 4, b2.getHeight());
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            canvas.drawBitmap(b2,rectRes,rectDst,paint);

            img.setImageBitmap(b_dst_1);
            img1.setImageBitmap(b2);
        }
    };
}
