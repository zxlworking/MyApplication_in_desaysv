package com.desay_sv.test_weather.http.data;

/**
 * Created by zxl on 2018/9/29.
 */

public class UpdateInfoResponseBean extends ResponseBaseBean {
    /**
     {
     "versionCode": "1",
     "code": 0,
     "name": "com.desay_sv.test_weather",
     "versionName": "1.0",
     "desc": "success"
     }
     */
    public String name = "";
    public String versionCode = "";
    public String versionName = "";

    @Override
    public String toString() {
        return "UpdateInfoResponseBean{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                ", name='" + name + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                '}';
    }
}
