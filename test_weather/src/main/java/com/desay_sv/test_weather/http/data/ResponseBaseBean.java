package com.desay_sv.test_weather.http.data;

/**
 * Created by zxl on 2018/9/5.
 */

public class ResponseBaseBean {
    public int code;
    public String desc = "";

    @Override
    public String toString() {
        return "ResponseBaseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
