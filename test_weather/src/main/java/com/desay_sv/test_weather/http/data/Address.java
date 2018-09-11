package com.desay_sv.test_weather.http.data;

import java.util.List;

/**
 * Created by zxl on 2018/9/6.
 */

public class Address {
    /**
     "type":"street",
     "status":1,
     "name":"胜利西路",
     "admCode":"320115",
     "admName":"江苏省,南京市,江宁区",
     "addr":"",
     "nearestPoint":[
     118.8051,
     31.9465
     ],
     "distance":13.118
     */
    public String type = "";
    public int status;
    public String name = "";
    public String admCode = "";
    public String admName = "";
    public String addr = "";
    public List<String> nearestPoint;
    public double distance;

    @Override
    public String toString() {
        return "Address{" +
                "type='" + type + '\'' +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", admCode='" + admCode + '\'' +
                ", admName='" + admName + '\'' +
                ", addr='" + addr + '\'' +
                ", nearestPoint=" + nearestPoint +
                ", distance=" + distance +
                '}';
    }
}
