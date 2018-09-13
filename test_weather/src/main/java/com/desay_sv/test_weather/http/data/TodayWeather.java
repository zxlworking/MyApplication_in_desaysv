package com.desay_sv.test_weather.http.data;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeather {
    /*
        "today_weather":{
        "now_time":"18:45 实况",
        "temperature":"25",
        "humidity_icon_css":Object{...},
        "wind_direction":"西南风",
        "air_quality":"87良",
        "air_quality_icon_css":Object{...},
        "humidity":"61%",
        "limit_icon_css":Object{...},
        "wind_icon_css":Object{...},
        "limit_content":"限行1和6",
        "is_limit":1,
        "wind_value":"1级",
        "temperature_icon_css":Object{...}
    },
    */

    public String now_time = "";
    public String temperature = "";
    public String wind_direction = "";
    public String air_quality = "";
    public String humidity = "";
    public String wind_value = "";
    public String limit_content = "";
    public int is_limit;

    public TodayWeatherTemperatureIconCss temperature_icon_css;

    public TodayWeatherHumidityIconCss humidity_icon_css;
    public TodayWeatherWindIconCss wind_icon_css;
    public TodayWeatherAirQualityIconCss air_quality_icon_css;
    public TodayWeatherLimitIconCss limit_icon_css;

    @Override
    public String toString() {
        return "TodayWeather{" +
                "now_time='" + now_time + '\'' +
                ", temperature='" + temperature + '\'' +
                ", wind_direction='" + wind_direction + '\'' +
                ", air_quality='" + air_quality + '\'' +
                ", humidity='" + humidity + '\'' +
                ", wind_value='" + wind_value + '\'' +
                ", limit_content='" + limit_content + '\'' +
                ", is_limit=" + is_limit +
                ", temperature_icon_css=" + temperature_icon_css +
                ", humidity_icon_css=" + humidity_icon_css +
                ", wind_icon_css=" + wind_icon_css +
                ", air_quality_icon_css=" + air_quality_icon_css +
                ", limit_icon_css=" + limit_icon_css +
                '}';
    }
}
