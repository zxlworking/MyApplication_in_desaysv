package com.desay_sv.test_weather.http.listener;

import com.desay_sv.test_weather.http.data.ResponseBaseBean;

/**
 * Created by zxl on 2018/9/5.
 */

public interface NetRequestListener {
    public void onSuccess(ResponseBaseBean responseBaseBean);

    public void onNetError();
    public void onNetError(Throwable e);

    public void onServer(ResponseBaseBean responseBaseBean);
}
