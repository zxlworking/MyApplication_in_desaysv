package com.desay_sv.test_weather.event;

import com.desay_sv.test_weather.http.data.CityInfo;

/**
 * Created by zxl on 2018/9/6.
 */

public class SelectCityEvent {
    public CityInfo mCityInfo;

    public SelectCityEvent(CityInfo cityInfo){
        mCityInfo = cityInfo;
    }
}
