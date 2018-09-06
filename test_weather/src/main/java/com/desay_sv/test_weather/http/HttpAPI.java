package com.desay_sv.test_weather.http;


import com.desay_sv.test_weather.http.data.TodayWeatherResponseBean;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by zxl on 2018/9/5.
 */

public interface HttpAPI {

    @GET("cgi_weather/test.py")
    public Observable<TodayWeatherResponseBean> getZHTianQiByCity(@Query("city")String city);

    @GET("cgi_qsbk/cgi_qsbk.py")
    public Observable<ResponseBody> getQSBK(@Query("page")int page);

}
