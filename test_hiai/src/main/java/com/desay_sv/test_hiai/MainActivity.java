package com.desay_sv.test_hiai;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.face.FaceComparator;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.face.FaceCompareResult;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FaceCompare";
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int TYPE_CHOOSE_PHOTO_CODE4PERSON1 = 10;
    private static final int TYPE_CHOOSE_PHOTO_CODE4PERSON2 = 11;
    private static final int TYPE_SHOW_RESULE = 12;
    private Bitmap mBitmapPerson1;
    private Bitmap mBitmapPerson2;
    private ImageView mImageViewPerson1;
    private ImageView mImageViewPerson2;
    private TextView mTxtViewResult;
    private boolean isPerson1=false;
    private Object mWaitResult = new Object();
    FaceComparator mFaceComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("zxl--->1");
        mImageViewPerson1 = (ImageView) findViewById(R.id.imgViewPerson1);
        mImageViewPerson2 = (ImageView) findViewById(R.id.imgViewPerson2);
        mTxtViewResult = (TextView) findViewById(R.id.result);

        System.out.println("zxl--->2");
        findViewById(R.id.btnPerson1).setOnClickListener(this);
        findViewById(R.id.btnPerson2).setOnClickListener(this);
        findViewById(R.id.btnstarCompare).setOnClickListener(this);
        System.out.println("zxl--->3");

        //To connect vision service
        VisionBase.init(getApplicationContext(),new ConnectionCallback(){
            @Override
            public void onServiceConnect() {
                Log.i(TAG, "onServiceConnect ");
            }

            @Override
            public void onServiceDisconnect() {
                Log.i(TAG, "onServiceDisconnect");
            }
        });
        mThread.start();
        System.out.println("zxl--->4");

        requestPermissions();
        System.out.println("zxl--->5");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFaceComparator != null) {
            mFaceComparator.release();
        }
    }

    public void onClickButton(View view) {
        int requestCode;
        switch (view.getId()) {
            case R.id.btnPerson1: {
                Log.d(TAG, "btnPerson1");
                isPerson1 = true;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                requestCode = PHOTO_REQUEST_GALLERY;
                startActivityForResult(intent, requestCode);
                break;
            }
            case R.id.btnPerson2: {
                Log.d(TAG, "btnPerson2");
                isPerson1 = false;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                requestCode = PHOTO_REQUEST_GALLERY;
                startActivityForResult(intent, requestCode);
                break;
            }
            case R.id.btnstarCompare: {
                startCompare();
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (data == null){
                return;
            }

            Uri selectedImage = data.getData();
            int type = data.getIntExtra("type", TYPE_CHOOSE_PHOTO_CODE4PERSON1);
            Log.d(TAG, "select uri:" + selectedImage.toString() + "type: " + type );
            getBitmap(type, selectedImage);
        }
    }

    private void getBitmap(int type, Uri imageUri) {
        String[] pathColumn = {MediaStore.Images.Media.DATA};

        //从系统表中查询指定Uri对应的照片
        Cursor cursor = getContentResolver().query(imageUri,pathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(pathColumn[0]);
        String picturePath = cursor.getString(columnIndex);  //获取照片路径
        cursor.close();

        if (isPerson1) {
            mBitmapPerson1 = BitmapFactory.decodeFile(picturePath);
            mHander.sendEmptyMessage(TYPE_CHOOSE_PHOTO_CODE4PERSON1);
        }else {
            mBitmapPerson2 = BitmapFactory.decodeFile(picturePath);
            mHander.sendEmptyMessage(TYPE_CHOOSE_PHOTO_CODE4PERSON2);
        }
    }

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int status = msg.what;
            Log.d(TAG, "handleMessage status = " + status);
            switch (status) {
                case TYPE_CHOOSE_PHOTO_CODE4PERSON1: {
                    if (mBitmapPerson1 == null) {
                        Log.e(TAG, "bitmap person1 is null !!!! ");
                        return;
                    }
                    mImageViewPerson1.setImageBitmap(mBitmapPerson1);
                    break;
                }
                case TYPE_CHOOSE_PHOTO_CODE4PERSON2: {
                    if (mBitmapPerson2 == null) {
                        Log.e(TAG, "bitmap person2 is null !!!! ");
                        return;
                    }
                    mImageViewPerson2.setImageBitmap(mBitmapPerson2);
                    break;
                }
                case TYPE_SHOW_RESULE: {
                    FaceCompareResult result = (FaceCompareResult)msg.obj;
                    if (result == null ) {
                        mTxtViewResult.setText("!!!not the same person!!!! result is null");
                        break;
                    }

                    if (result.isSamePerson()) {
                        mTxtViewResult.setText("The same Person !!  and score: " + result.getSocre());
                    } else {
                        mTxtViewResult.setText("Not the same Person !! and score:" + result.getSocre());
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void startCompare() {
        mTxtViewResult.setText("Is the same Person ??? ");
        synchronized (mWaitResult) {
            mWaitResult.notifyAll();
        }
    }

    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            mFaceComparator = new FaceComparator(getApplicationContext());
            FaceComparator faceComparator = mFaceComparator;
            while (true) {
                try {
                    synchronized (mWaitResult) {
                        mWaitResult.wait();
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }

                Log.d(TAG, "start Compare !!!! ");
                Frame frame1 = new Frame();
                frame1.setBitmap(mBitmapPerson1);
                Frame frame2 = new Frame();
                frame2.setBitmap(mBitmapPerson2);

                JSONObject jsonObject = faceComparator.faceCompare(frame1, frame2, null);
                if (jsonObject != null)
                    Log.d(TAG, "Compare end !!!!  json: " + jsonObject.toString());
                FaceCompareResult result = faceComparator.convertResult(jsonObject);
                Log.d(TAG, "convertResult end !!!! ");

                Message msg = new Message();
                msg.what = TYPE_SHOW_RESULE;
                msg.obj = result;
                mHander.sendMessage(msg);
            }
        }
    });

    private void requestPermissions(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        System.out.println("zxl--->onClick--->1");
        onClickButton(v);
    }
}