package test.zxl.com.test_messenger_client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import test.zxl.com.test_messenger.data.TestChildObject;
import test.zxl.com.test_messenger.data.TestObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int MSG_TEST_BASIC = 1;
    private static final int MSG_TEST_OBJECT = 2;

    private Button mBtnBaic;
    private Button mBtnObject;

    private Messenger mServerMessenger;
    private Messenger mClientMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_TEST_BASIC:
                    Bundle mBundleBasic = msg.getData();
                    mBundleBasic.setClassLoader(getClass().getClassLoader());
                    Log.d(TAG,"server::MSG_TEST_BASIC::msg.arg1 = " + msg.arg1 + "::msg.objct = " + mBundleBasic.get("MSG_TEST_BASIC"));
                    break;
                case MSG_TEST_OBJECT:
                    Bundle mBundleObject = msg.getData();
                    mBundleObject.setClassLoader(getClass().getClassLoader());
                    Object mObjectData = mBundleObject.get("MSG_TEST_OBJECT");
                    TestObject mTestObject = null;
                    if(mObjectData instanceof TestObject){
                        mTestObject = (TestObject)mObjectData;
                    }
                    Log.d(TAG,"server::MSG_TEST_OBJECT::msg.objct = " + mTestObject);
                    break;
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"client::onCreate");

        mBtnBaic = findViewById(R.id.btn_basic);
        mBtnObject = findViewById(R.id.btn_object);

        Intent mIntent = new Intent();
        mIntent.setComponent(new ComponentName("test.zxl.com.test_messenger_server","test.zxl.com.test_messenger_server.TestService"));
        bindService(mIntent,mServiceConnection, Context.BIND_AUTO_CREATE);

        mBtnBaic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = Message.obtain();
                message.what = MSG_TEST_BASIC;
                Bundle mBundle = new Bundle();
                mBundle.putInt("MSG_TEST_BASIC",123);
                message.setData(mBundle);
                message.replyTo = mClientMessenger;
                try {
                    mServerMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        mBtnObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = Message.obtain();
                message.what = MSG_TEST_OBJECT;
                Bundle mBundle = new Bundle();
                TestObject mTestObject = new TestObject();
                TestChildObject mTestChildObject = new TestChildObject();
                mTestObject.mTestChildObject = mTestChildObject;
                mTestObject.testInt = 125;
                mTestObject.testStr="client";
                mTestObject.mTestChildObject.testInt = 126;
                mTestObject.mTestChildObject.testStr = "client_child";
                mBundle.putParcelable("MSG_TEST_OBJECT",mTestObject);
                message.setData(mBundle);
                message.replyTo = mClientMessenger;
                try {
                    mServerMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"client::onServiceConnected");
            mServerMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"client::onServiceDisconnected");
        }
    };
}
