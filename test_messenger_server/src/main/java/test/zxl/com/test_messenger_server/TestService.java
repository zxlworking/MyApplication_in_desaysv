package test.zxl.com.test_messenger_server;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import test.zxl.com.test_messenger.data.TestChildObject;
import test.zxl.com.test_messenger.data.TestObject;

/**
 * Created by uidq0955 on 2018/5/17.
 */

public class TestService extends Service {
    private static final String TAG = "TestService";

    private static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";

    private static final int MSG_TEST_BASIC = 1;
    private static final int MSG_TEST_OBJECT = 2;

    private Messenger mMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_TEST_BASIC:
                    Bundle mBundleBasic = msg.getData();
                    mBundleBasic.setClassLoader(getClass().getClassLoader());
                    Log.d(TAG,"server::MSG_TEST_BASIC::msg.arg1 = " + msg.arg1 + "::msg.objct = " + mBundleBasic.get("MSG_TEST_BASIC"));

                    Message messageBasic = Message.obtain();
                    messageBasic.what = MSG_TEST_BASIC;
                    mBundleBasic = new Bundle();
                    mBundleBasic.putInt("MSG_TEST_BASIC",112233);
                    messageBasic.setData(mBundleBasic);
                    try {
                        msg.replyTo.send(messageBasic);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

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

                    Message messageObject = Message.obtain();
                    messageObject.what = MSG_TEST_OBJECT;
                    TestChildObject mTestChildObject = new TestChildObject();
                    mTestObject.mTestChildObject = mTestChildObject;
                    mTestObject.testInt = 112255;
                    mTestObject.testStr="server";
                    mTestObject.mTestChildObject.testInt = 112266;
                    mTestObject.mTestChildObject.testStr = "server_child";
                    mBundleObject.putParcelable("MSG_TEST_OBJECT",mTestObject);
                    messageObject.setData(mBundleObject);
                    try {
                        msg.replyTo.send(messageObject);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    });

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }

    class StopReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            Log.d(TAG,"onReceive::mAction = " + mAction);
            if(TextUtils.equals(mAction,ACTION_STOP_SERVICE)){
                stopSelf();
            }
        }
    }
}
