package com.zxl.test_ntp;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by uidq0955 on 2017/12/15.
 */

public class SntpTime {
    private String TAG = "SntpTime";
    private String mIp;
    private int mTimeOut;
    private Handler mOtherHandler;
    private int mHandlerWhatNumber;

    // 时间获取ip地址 // socket超时时间   //  消息句柄// 消息标号自定义What数字
    public SntpTime (String ip, int timeOut, Handler handler, int handlerWhatNumber) {
        this.mIp = ip;
        this.mTimeOut = timeOut;
        this.mOtherHandler = handler;
        this.mHandlerWhatNumber = handlerWhatNumber;
        Log.i(TAG, "SntpTime: " + mIp + " " + mTimeOut + " " + mHandlerWhatNumber);
    }

    public void getTime() {
        new Thread() {
            public void run() {
                //使用隐形类
                try {
                    Class C;
                    //通过名字查找类 class
                    C = Class.forName("android.net.SntpClient");
                    Log.i(TAG, "class is " + C);
                    Object obj = C.newInstance();
                    try {
                        //函数参数 2个  requestTime
                        //根据名字和参数个数查找函数
                        Method m = C.getMethod("requestTime", new Class[]{String.class, int.class});
                        //参数值以数组的形式传入,格式与Class一样
                        //调用函数
                        Object o = m.invoke(obj, new Object[]{mIp, mTimeOut});
                        //返回值转换
                        boolean b = (Boolean)o;
                        Log.i(TAG, "return is " + b);
                        if (b) {
                            //SntpClient.getNtpTime() //return time value computed from NTP server response
                            Method mGetNtpTime = C.getMethod("getNtpTime", new Class[]{});
                            Object oGetNtpTime = mGetNtpTime.invoke(obj, new Object[]{});
                            Long ntpTime = (Long)oGetNtpTime;
                            Log.i(TAG, "ntpTime is " + ntpTime);
                            //SntpClient.getNtpTimeReference()  == SystemClock.elapsedRealtime()
                            //reference clock corresponding to the NTP time
                            Method mGetNtpTimeReference = C.getMethod("getNtpTimeReference", new Class[]{});
                            Object oGetNtpTimeReference = mGetNtpTimeReference.invoke(obj, new Object[]{});
                            Long ntpTimeReference = (Long)oGetNtpTimeReference;
                            Log.i(TAG, "ntpTimeReference is " + ntpTimeReference);
                            ////////////////////////////////
                            Log.i(TAG, "SystemClock.elapsedRealtime() is "+ SystemClock.elapsedRealtime());
                            Log.i(TAG, "System.currentTimeMillis() is "+ System.currentTimeMillis());
                            Date test0 = new Date(ntpTime);
                            Log.i(TAG, "ntpTime is Date.toString()" + test0.toString());
                            Date test1 = new Date(System.currentTimeMillis());
                            Log.i(TAG, "System.currentTimeMillis() is Date.toString()" + test1.toString());
                            //很多网上的公式我这里测试返回的结果是错误的如: long now = client.getNtpTime() + System.nanoTime() / 1000  - client.getNtpTimeReference();
                            //本人这个计算公式是源码SntpClient.java源码注释中提供的,可自己查看
                            long nowTime = ntpTime + SystemClock.elapsedRealtime() - ntpTimeReference;
                            updateTimeAndDate(nowTime);
                            Date currentTime = new Date(nowTime);

                            Message toOtherHandler = new Message();
                            toOtherHandler.what = mHandlerWhatNumber;
                            toOtherHandler.obj = nowTime;
                            Log.i(TAG, "mHandlerWhatNumber is "+ mHandlerWhatNumber + "time : " + currentTime.toString());
                            mOtherHandler.sendMessage(toOtherHandler);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "GetClass SntpClient is error! two ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "GetClass SntpClient is error! one ");
                }
            }
        }.start();
    }

    private void updateTimeAndDate(Long sntpTime) {
        long systemTime = System.currentTimeMillis();
        Log.i(TAG, "Time////System.currentTimeMillis() is " + System.currentTimeMillis());
        Log.i(TAG, "Time////sntpTime is " + sntpTime);
        Log.i(TAG, "Time////(systemTime - sntpTime) = " + (systemTime - sntpTime));

        //if((systemTime - sntpTime >= 1000*60 ) || (systemTime - sntpTime <= -1000*60) ) {
        //时间格式修改
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.HHmmss");//yyyy-MM-dd HH:mm:ss
        String format = df.format(new Date(sntpTime));
        Log.i(TAG, "Time////format is " + format);

        boolean b = SystemClock.setCurrentTimeMillis(sntpTime);
        Log.i(TAG, "Time////System.currentTimeMillis() is " + System.currentTimeMillis() + "return currentTimeMillis bool = " + b);
//      }
    }

}
