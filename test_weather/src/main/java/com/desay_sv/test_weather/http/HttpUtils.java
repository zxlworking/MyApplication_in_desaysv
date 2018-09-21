package com.desay_sv.test_weather.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.desay_sv.test_weather.http.data.CityInfoListResponseBean;
import com.desay_sv.test_weather.http.data.QSBKElementList;
import com.desay_sv.test_weather.http.data.TaoBaoAnchorListResponseBean;
import com.desay_sv.test_weather.http.data.TodayWeatherResponseBean;
import com.desay_sv.test_weather.http.data.UserInfoResponseBean;
import com.desay_sv.test_weather.http.listener.NetRequestListener;
import com.desay_sv.test_weather.utils.Constants;
import com.zxl.common.DebugUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Query;
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

    private static Retrofit mRetrofit;
    private static HttpAPI mHttpAPI;

    private HttpUtils(){
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(1, TimeUnit.MINUTES);
        okBuilder.readTimeout(1,TimeUnit.MINUTES);
        OkHttpClient okHttpClient = okBuilder.build();
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        Retrofit mRetrofit = retrofitBuilder
//                .baseUrl("http://www.zxltest.cn/cgi_server/")
                .baseUrl(Constants.WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        mHttpAPI = mRetrofit.create(HttpAPI.class);
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


    public void getZHTianQiByLocation(Context context, String l, final NetRequestListener listener){
        DebugUtil.d(TAG,"getZHTianQiByLocation::l = " + l);

//        if(isNetworkAvailable(context)) {
//            Call<ResponseBody> call = mHttpAPI.getZHTianQiByLocation(l);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG, "getZHTianQiByLocation::s = " + s);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });
//        }
        if(isNetworkAvailable(context)){
//        if(true){
            Observable<TodayWeatherResponseBean> observable = mHttpAPI.getZHTianQiByLocation(l);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<TodayWeatherResponseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getZHTianQiByLocation::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getZHTianQiByLocation::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(TodayWeatherResponseBean todayWeatherResponseBean) {
                            DebugUtil.d(TAG,"getZHTianQiByLocation::onNext::todayWeatherResponseBean = " + todayWeatherResponseBean);
                            if(listener != null){
                                listener.onSuccess(todayWeatherResponseBean);
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getZHTianQiByLocation::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getZHTianQiByCity(Context context, String city, final NetRequestListener listener){
        DebugUtil.d(TAG,"getZHTianQiByCity::city = " + city);

        if(isNetworkAvailable(context)){
//            Call<ResponseBody> call = mHttpAPI.getZHTianQiByCity(city);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG,"getZHTianQiByCity::s = " + s);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });
            Observable<TodayWeatherResponseBean> observable = mHttpAPI.getZHTianQiByCity(city);
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


    public void getCityInfoList(Context context, final NetRequestListener listener){
        DebugUtil.d(TAG,"getCityInfoList");

        if(isNetworkAvailable(context)){
            Observable<CityInfoListResponseBean> observable = mHttpAPI.getCityInfoList();
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<CityInfoListResponseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getCityInfoList::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getCityInfoList::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(CityInfoListResponseBean cityInfoListResponseBean) {
                            DebugUtil.d(TAG,"getCityInfoList::onNext::cityInfoListResponseBean = " + cityInfoListResponseBean);
                            if(listener != null){
                                listener.onSuccess(cityInfoListResponseBean);
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getCityInfoList::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getTaoBaoAnchor(Context context,int page, final NetRequestListener listener){
        DebugUtil.d(TAG,"getTaoBaoAnchor::page = " + page);

//        if(isNetworkAvailable(context)){
//            Call<ResponseBody> call = mHttpAPI.getTaoBaoAnchor(page);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG,"getTaoBaoAnchor::s = " + s);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });

        if(true){
            Observable<TaoBaoAnchorListResponseBean> observable = mHttpAPI.getTaoBaoAnchor(page);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<TaoBaoAnchorListResponseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"getTaoBaoAnchor::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"getTaoBaoAnchor::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(TaoBaoAnchorListResponseBean taoBaoAnchorListResponseBean) {
                            DebugUtil.d(TAG,"getTaoBaoAnchor::onNext::taoBaoAnchorListResponseBean = " + taoBaoAnchorListResponseBean);
                            if(listener != null){
                                listener.onSuccess(taoBaoAnchorListResponseBean);
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"getTaoBaoAnchor::net work error");
            if(listener != null){
                listener.onNetError();
            }
        }
    }

    public void getQSBK(Context context, int page, final NetRequestListener listener){
        DebugUtil.d(TAG,"getQSBK::page = " + page);

        if(isNetworkAvailable(context)){
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

    public void register(Context context, String user_operator, String user_info, final NetRequestListener listener){
        DebugUtil.d(TAG,"register::user_operator = " + user_operator + "::user_info = " + user_info);

//        if(isNetworkAvailable(context)) {
//            Call<ResponseBody> call = mHttpAPI.register(user_operator,user_info);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String s = new String(response.body().bytes());
//                        DebugUtil.d(TAG, "register::s = " + s);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });
//        }

        if(isNetworkAvailable(context)){
            Observable<UserInfoResponseBean> observable = mHttpAPI.register(user_operator,user_info);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UserInfoResponseBean>() {
                        @Override
                        public void onCompleted() {
                            DebugUtil.d(TAG,"register::onCompleted");
                        }

                        @Override
                        public void onError(Throwable e) {
                            DebugUtil.d(TAG,"register::onError::e = " + e);
                            if(listener != null){
                                listener.onNetError(e);
                            }
                        }

                        @Override
                        public void onNext(UserInfoResponseBean userInfoResponseBean) {
                            DebugUtil.d(TAG,"register::onNext::userInfoResponseBean = " + userInfoResponseBean);
                            if(listener != null){
                                listener.onSuccess(userInfoResponseBean);
                            }
                        }
                    });
        }else{
            DebugUtil.d(TAG,"register::net work error");
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
