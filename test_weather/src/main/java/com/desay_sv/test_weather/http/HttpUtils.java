package com.desay_sv.test_weather.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.desay_sv.test_weather.http.data.QSBKElementList;
import com.desay_sv.test_weather.http.data.TodayWeatherResponseBean;
import com.desay_sv.test_weather.http.listener.NetRequestListener;
import com.desay_sv.test_weather.utils.Constants;
import com.zxl.common.DebugUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zxl on 2018/9/5.
 */

public class HttpUtils {
    private static final String TAG = "HttpUtils";

    private static HttpUtils mHttpUtils;

    private static Object mLock = new Object();

//    private static Retrofit mRetrofit;
//    private static HttpAPI mHttpAPI;

    private HttpUtils(){

    }

    public static HttpUtils getInstance(){
        DebugUtil.d(TAG,"getInstance");

        if(null == mHttpUtils){
            synchronized (mLock){
                if(null == mHttpUtils){
                    mHttpUtils = new HttpUtils();
                }
            }
        }
        return mHttpUtils;
    }

    private HttpAPI initBaseUrl(String url){
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(1, TimeUnit.MINUTES);
        okBuilder.readTimeout(1,TimeUnit.MINUTES);
        OkHttpClient okHttpClient = okBuilder.build();
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        Retrofit mRetrofit = retrofitBuilder
//                .baseUrl("http://www.zxltest.cn/cgi_server/")
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        HttpAPI mHttpAPI = mRetrofit.create(HttpAPI.class);
        return mHttpAPI;
    }

    public void getZHTianQiByCity(Context context, String l, final NetRequestListener listener){
        DebugUtil.d(TAG,"getZHTianQiByCity::l = " + l);

        HttpAPI mHttpAPI = initBaseUrl(Constants.WEATHER_BASE_URL);

        if(true){
            Observable<TodayWeatherResponseBean> observable = mHttpAPI.getZHTianQiByCity(l);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<TodayWeatherResponseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getZHTianQiByCity::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getZHTianQiByCity::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(TodayWeatherResponseBean todayWeatherResponseBean) {
                            DebugUtil.d(TAG,"getZHTianQiByCity::onNext::todayWeatherResponseBean = " + todayWeatherResponseBean);
                            if(listener != null){
                                listener.onSuccess(todayWeatherResponseBean);
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getZHTianQiByCity::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getQSBK(Context context, int page, final NetRequestListener listener){
        DebugUtil.d(TAG,"getQSBK::page = " + page);

        HttpAPI mHttpAPI = initBaseUrl(Constants.WEATHER_BASE_URL);

        if(true){
            Observable<QSBKElementList> observable = mHttpAPI.getQSBK(page);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<QSBKElementList>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getQSBK::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getQSBK::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(QSBKElementList qsbkElementList) {
                            DebugUtil.d(TAG,"getQSBK::onNext::qsbkElementList = " + qsbkElementList);
                            if(listener != null){
                                listener.onSuccess(qsbkElementList);
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getQSBK::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    //==============NetworkAvailable===============
    /**
     * 没有连接网络
     */
    private static final int NETWORK_NONE = -1;
    /**
     * 移动网络
     */
    private static final int NETWORK_MOBILE = 0;
    /**
     * 无线网络
     */
    private static final int NETWORK_WIFI = 1;

    public static boolean isNetworkAvailable(Context context) {
        return isNetConnect(getNetWorkState(context));
    }

    private static boolean isNetConnect(int state) {
        DebugUtil.d(TAG, "isNetConnect::state = " + state);
        if (state == NETWORK_WIFI) {
            return true;
        } else if (state == NETWORK_MOBILE) {
            return true;
        } else if (state == NETWORK_NONE) {
            return false;
        }
        return false;
    }

    private static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    public boolean isNetworkAvailable2(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        // 获取NetworkInfo对象
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        if (networkInfo != null && networkInfo.length > 0) {
            for (int i = 0; i < networkInfo.length; i++) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }

        return false;
    }
}
