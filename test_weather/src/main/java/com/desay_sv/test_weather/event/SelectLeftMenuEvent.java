package com.desay_sv.test_weather.event;

import com.desay_sv.test_weather.http.data.CityInfo;

/**
 * Created by zxl on 2018/9/6.
 */

public class SelectLeftMenuEvent {
    public int mPosition;

    public SelectLeftMenuEvent(int position){
        mPosition = position;
    }
}
