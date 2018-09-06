package com.desay_sv.test_weather.http.data;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeather {
    /*
    {
        "now_time":"13:00 实况",
        "temperature":"28",
        "wind_direction":"西北风",
        "air_quality":"74良",
        "humidity":"65%",
        "wind_value":"2",
        "temperature_icon_css":Object{...}
        "humidity_icon_css":Object{...},
        "wind_icon_css":Object{...},
        "air_quality_icon_css":Object{...},
    }
    */

    public String now_time = "";
    public String temperature = "";
    public String wind_direction = "";
    public String air_quality = "";
    public String humidity = "";
    public String wind_value = "";

    public TodayWeatherTemperatureIconCss temperature_icon_css;

    public TodayWeatherHumidityIconCss humidity_icon_css;
    public TodayWeatherWindIconCss wind_icon_css;
    public TodayWeatherAirQualityIconCss air_quality_icon_css;

    @Override
    public String toString() {
        return "TodayWeather{" +
                "now_time='" + now_time + '\'' +
                ", temperature='" + temperature + '\'' +
                ", wind_direction='" + wind_direction + '\'' +
                ", air_quality='" + air_quality + '\'' +
                ", humidity='" + humidity + '\'' +
                ", wind_value='" + wind_value + '\'' +
                ", temperature_icon_css=" + temperature_icon_css +
                ", humidity_icon_css=" + humidity_icon_css +
                ", wind_icon_css=" + wind_icon_css +
                ", air_quality_icon_css=" + air_quality_icon_css +
                '}';
    }
}
