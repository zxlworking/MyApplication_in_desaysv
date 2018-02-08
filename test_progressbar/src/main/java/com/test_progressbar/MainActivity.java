package com.test_progressbar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;


public class MainActivity extends AppCompatActivity {

    private Context mContext;

    private Dialog mDialog;

    private Button mShowBtn;
    private Button mAddBtn;
    private Button mDescBtn;

    private SeekBar mSeekBar;

    private View mDialogContentView = null;

    private Handler mHandler = new Handler();

    private int mProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mContext = this;

        mShowBtn = findViewById(R.id.show_btn);
        mAddBtn = findViewById(R.id.add_btn);
        mDescBtn = findViewById(R.id.desc_btn);

        mShowBtn.setOnClickListener(mOnClickListener);
        mAddBtn.setOnClickListener(mOnClickListener);
        mDescBtn.setOnClickListener(mOnClickListener);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mDialogContentView = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog_view,null);

                mSeekBar = mDialogContentView.findViewById(R.id.seek_bar);
                mSeekBar.setMax(100);
            }
        }).start();
    }

    private void updateWidth() {
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = 400;
        lp.height = 200;
        lp.gravity = Gravity.CENTER;
        mDialog.getWindow().setAttributes(lp);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.show_btn:
                    if(null == mDialog){
                        mDialog = new Dialog(mContext);
                        Window mWindow = mDialog.getWindow();
                        mWindow.requestFeature(Window.FEATURE_NO_TITLE);
                        mDialog.setContentView(mDialogContentView);
                        mDialog.create();

                        mWindow.setBackgroundDrawable(null);
                        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        updateWidth();
                    }

                    mDialog.show();
                    break;
                case R.id.add_btn:
                    mDialog.show();

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgress = mProgress + 10;
                            if(mProgress > mSeekBar.getMax()){
                                mProgress = mSeekBar.getMax();
                            }
                            mSeekBar.setProgress(mProgress);
                            mDialog.dismiss();
                        }
                    },1000);
                    break;
                case R.id.desc_btn:
                    mDialog.show();

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgress = mProgress - 10;
                            if(mProgress < 0){
                                mProgress = 0;
                            }
                            mSeekBar.setProgress(mProgress);
                            mDialog.dismiss();
                        }
                    },1000);
                    break;
            }
        }
    };
}
