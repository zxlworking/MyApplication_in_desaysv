package com.desay_sv.test_weather.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxl.common.DebugUtil;


public class CommonUtils {
    public static final String TAG = "CommonUtils";

    /** 判断是否是快速点击 */
    private static long lastClickTime;

    public static Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    public static String getVersionName(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int px2dip(int pxValue){
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static float dip2px(float dipValue){
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return  (dipValue * scale + 0.5f);
    }

//    public static void loadImage(ImageView img, int defaultResId, String url){
//        img.setImageResource(R.drawable.icon_album);
//        Glide.with(SmartRadioApp.get())
//                .load(url)
//                .transition(new DrawableTransitionOptions().crossFade(500))
//                .apply(new RequestOptions().placeholder(R.drawable.icon_album)
////                        .dontAnimate()
////                        .signature(EmptySignature.obtain()))
////                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE
//                )
//                //.thumbnail(0.1f)
//                .into(img);
//    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        DebugUtil.d(TAG,"isFastDoubleClick::timeD = " + timeD);
        if (0 < timeD && timeD < 500) {

            return true;
        }
        lastClickTime = time;
        return false;

    }


}
