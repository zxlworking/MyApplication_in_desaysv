package com.desay_sv.test_weather.http.data;

import java.util.List;

/**
 * Created by zxl on 2018/9/6.
 */

public class QueryCityResponseBean extends ResponseBaseBean {
    public List<String> queryLocation;

    public List<Address> addrList;

    @Override
    public String toString() {
        return "QueryCityResponseBean{" +
                "queryLocation=" + queryLocation +
                ", addrList=" + addrList +
                '}';
    }
}
