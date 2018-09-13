package com.desay_sv.test_weather.http.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uidq0955 on 2018/6/14.
 */

public class QSBKElementList extends ResponseBaseBean {
    public int current_page = 0;
    public List<QSBKElement> result = new ArrayList<>();

    @Override
    public String toString() {
        return "QSBKElementList{" +
                "current_page=" + current_page +
                ", code=" + code +
                ", desc='" + desc + '\'' +
                ", result=" + result +
                '}';
    }
}
