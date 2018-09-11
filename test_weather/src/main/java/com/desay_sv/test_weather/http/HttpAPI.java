package com.desay_sv.test_weather.http;


import com.desay_sv.test_weather.http.data.QueryCityResponseBean;
import com.desay_sv.test_weather.http.data.TodayWeatherResponseBean;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by zxl on 2018/9/5.
 */

public interface HttpAPI {

    @GET("cgi_weather/test2.py")
    public Observable<TodayWeatherResponseBean> getZHTianQiByCity(@Query("l")String l);

    @GET("cgi_qsbk/cgi_qsbk.py")
    public Observable<ResponseBody> getQSBK(@Query("page")int page);

    @GET("/")
    public Observable<QueryCityResponseBean> queryCity(@Query("l")String l, @Query("type")int type);
}
