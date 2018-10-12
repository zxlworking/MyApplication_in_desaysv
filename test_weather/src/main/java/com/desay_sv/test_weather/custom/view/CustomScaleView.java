package com.desay_sv.test_weather.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.desay_sv.test_weather.R;
import com.desay_sv.test_weather.utils.CommonUtils;
import com.desay_sv.test_weather.utils.Constants;
import com.desay_sv.test_weather.utils.WXUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.zxl.common.DebugUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by zxl on 2018/10/12.
 */

public class CustomScaleView extends LinearLayout {

    private static final String TAG = "CustomScaleImageView";

    private Context mContext;

    private ImageView mScaleImg;

    private int mDownX;
    private int mDownY;

    private int mLastDownX;
    private int mLastDownY;

    private int mCurrentDownX;
    private int mCurrentDownY;

    private double mLastDoublePointerDistance = 0;
    private double mCurrentDoublePointerDistance = 0;

    private boolean isCustomeMove = false;
    private boolean isDoublePointerToOne = false;

    private File mScaleFile = null;

    public CustomScaleView(Context context) {
        super(context);
        init(context);
    }

    public CustomScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }

    public void setScaleImg(ImageView img){
        mScaleImg = img;
    }

    public void setUrl(final String url){

        setVisibility(VISIBLE);

        new AsyncTask<Void, Integer, File>() {

            @Override
            protected File doInBackground(Void... params) {
                File file = null;
                try {
                    FutureTarget<File> future = Glide
                            .with(getContext())
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

                    file = future.get();

                } catch (Exception e) {
                    DebugUtil.d(TAG, e.getMessage());
                }
                return file;
            }

            @Override
            protected void onPostExecute(File file) {
                DebugUtil.d(TAG,"onPostExecute::file = " + file + "::mScaleImg = " + mScaleImg);
                if(file == null){
                    return;
                }
                mScaleFile = file;
                Glide.with(getContext()).load(file).into(mScaleImg);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }
        }.execute();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        DebugUtil.d(TAG,"onVisibilityChanged::visibility = " + visibility);
        DebugUtil.d(TAG,"onVisibilityChanged::width = " + getWidth() + "::height = " + getHeight());

//        if(visibility == View.GONE){
//            View child = getChildAt(0);
//            child.setLayoutParams(new LinearLayout.LayoutParams(getWidth(),getHeight()));
//        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        DebugUtil.d(TAG,"onTouchEvent::getPointerCount = " + event.getPointerCount());
        if(event.getPointerCount() > 1){
            isDoublePointerToOne = true;
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    mLastDoublePointerDistance = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int distanceX = (int) (event.getX(0) - event.getX(1));
                    int distanceY = (int) (event.getY(0) - event.getY(1));
                    double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
                    if(mLastDoublePointerDistance > 0){
                        mLastDoublePointerDistance = mCurrentDoublePointerDistance;
                        mCurrentDoublePointerDistance = distance;


                        double deltaDistance = mCurrentDoublePointerDistance - mLastDoublePointerDistance;
                        DebugUtil.d(TAG,"DoublePointer::deltaDistance = " + deltaDistance);

                        View child = getChildAt(0);
                        double width = child.getWidth() + deltaDistance;
                        double height = child.getHeight() + deltaDistance;

                        child.setLayoutParams(new LinearLayout.LayoutParams((int)width,(int)height));

                    }else{
                        mLastDoublePointerDistance = distance;
                        mCurrentDoublePointerDistance = distance;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    DebugUtil.d(TAG,"DOUBLE ACTION_UP::isDoublePointerToOne = " + isDoublePointerToOne);
                    break;
            }
        }else{
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    isCustomeMove = false;

                    mDownX = (int) event.getX();
                    mDownY = (int) event.getY();

                    mLastDownX = (int) event.getX();
                    mLastDownY = (int) event.getY();

                    mCurrentDownX = mLastDownX;
                    mCurrentDownY = mLastDownY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) Math.abs(event.getX() - mDownX);
                    int deltaY = (int) Math.abs(event.getY() - mDownY);
//                    DebugUtil.d(TAG,"ACTION_MOVE::deltaX = " + deltaX + "::deltaY = " + deltaY);
                    if(deltaX > 5 || deltaY > 5){
                        isCustomeMove = true;
                    }

                    mLastDownX = mCurrentDownX;
                    mLastDownY = mCurrentDownY;
                    mCurrentDownX = (int) event.getX();
                    mCurrentDownY = (int) event.getY();
                    if(!isDoublePointerToOne && isCustomeMove){
                        scrollBy(mLastDownX - mCurrentDownX,mLastDownY - mCurrentDownY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    DebugUtil.d(TAG,"ONE ACTION_UP::isCustomeMove = " + isCustomeMove);
                    if(!isDoublePointerToOne && !isCustomeMove){
                        performClick();
                    }
                    isDoublePointerToOne = false;

                    mLastDoublePointerDistance = 0;
                    mCurrentDoublePointerDistance = 0;

                    mDownX = 0;
                    mDownY = 0;

                    mLastDownX = 0;
                    mLastDownY = 0;

                    mCurrentDownX = 0;
                    mCurrentDownY = 0;

                    break;
            }
        }
        return true;
    }
}
