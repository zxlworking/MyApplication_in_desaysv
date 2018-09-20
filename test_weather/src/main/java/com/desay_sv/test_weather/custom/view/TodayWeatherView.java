package com.desay_sv.test_weather.custom.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desay_sv.test_weather.CityInfoListActivity;
import com.desay_sv.test_weather.R;
import com.desay_sv.test_weather.event.LocatePermissionSuccessEvent;
import com.desay_sv.test_weather.event.RequestLocatePermissionEvent;
import com.desay_sv.test_weather.event.SelectCityEvent;
import com.desay_sv.test_weather.http.HttpUtils;
import com.desay_sv.test_weather.http.data.ResponseBaseBean;
import com.desay_sv.test_weather.http.data.TodayWeatherResponseBean;
import com.desay_sv.test_weather.http.listener.NetRequestListener;
import com.desay_sv.test_weather.utils.EventBusUtils;
import com.zxl.common.DebugUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by zxl on 2018/9/5.
 */

public class TodayWeatherView extends CardView {

    private static final String TAG = "TodayWeatherView";

    private Context mContext;



    private LocationManager mLocationManager;
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            DebugUtil.d(TAG, "onLocationChanged::isNeedLoadData = " + isNeedLoadData + "::location = " + location);
            if (isNeedLoadData) {
                mLocationInfo = location.getLatitude() + "," + location.getLongitude();
                getDataFromNetByLocation(mLocationInfo);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private String mLocationInfo = "";

    private View mContentView;

    private View mTodayWeatherContentView;
    private View mLoadingView;
    private View mLoadErrorView;
    private TextView mLoadErrorTv;
    private Button mLoadErrorBtn;

    private LinearLayout mAddressInfoLl;
    private TextView mAddressInfo;
    private TextView mNowTimeTv;
    private TodayWeatherTemperatureView mTodayWeatherTemperatureView;
    private TextView mTemperatureTv;
    private TodayWeatherHumidityIconView mTodayWeatherHumidityIconView;
    private TextView mHumidityTv;
    private TodayWeatherWindIconView mTodayWeatherWindIconView;
    private TextView mWindTv;
    private TodayWeatherAirQualityIconView mTodayWeatherAirQualityIconView;
    private TextView mAirQualityTv;
    private LinearLayout mLimitContentLl;
    private TodayWeatherLimitIconView mTodayWeatherLimitIconView;
    private TextView mLimitTv;

    private TextView mTodayWeatherDetail1TitleTv;
    private TodayWeatherDetailIconView mTodayWeatherDetail1IconView;
    private TextView mTodayWeatherDetail1TemperatureTv;
    private TextView mTodayWeatherDetail1WeatherTv;
    private TextView mTodayWeatherDetail1WeatherDescTv;
    private TodayWeatherDetailWindIconView mTodayWeatherDetail1WindIconView;
    private TextView mTodayWeatherDetail1WindTv;
    private TodayWeatherDetailSunIconView mTodayWeatherDetail1SunIconView;
    private TextView mTodayWeatherDetail1SunTimeTv;

    private TextView mTodayWeatherDetail2TitleTv;
    private TodayWeatherDetailIconView mTodayWeatherDetail2IconView;
    private TextView mTodayWeatherDetail2TemperatureTv;
    private TextView mTodayWeatherDetail2WeatherTv;
    private TextView mTodayWeatherDetail2WeatherDescTv;
    private TodayWeatherDetailWindIconView mTodayWeatherDetail2WindIconView;
    private TextView mTodayWeatherDetail2WindTv;
    private TodayWeatherDetailSunIconView mTodayWeatherDetail2SunIconView;
    private TextView mTodayWeatherDetail2SunTimeTv;

    private Toolbar mToolbar;

    private boolean isLoading = false;
    private boolean isNeedLoadData = true;

    public TodayWeatherView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TodayWeatherView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TodayWeatherView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        DebugUtil.d(TAG, "init");
        mContext = context;

        mContentView = LayoutInflater.from(context).inflate(R.layout.today_weather_view, this);

        mTodayWeatherContentView = mContentView.findViewById(R.id.today_weather_content_view);
        mLoadingView = mContentView.findViewById(R.id.today_weather_loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.today_weather_load_error_view);
        mLoadErrorTv = mLoadErrorView.findViewById(R.id.load_error_tv);
        mLoadErrorBtn = mLoadErrorView.findViewById(R.id.load_error_btn);

        mAddressInfoLl = mContentView.findViewById(R.id.address_info_ll);
        mAddressInfo = mContentView.findViewById(R.id.address_info);
        mNowTimeTv = mContentView.findViewById(R.id.now_time_tv);
        mTodayWeatherTemperatureView = mContentView.findViewById(R.id.today_weather_temperature_view);
        mTemperatureTv = mContentView.findViewById(R.id.temperature_tv);
        mTodayWeatherHumidityIconView = mContentView.findViewById(R.id.today_weather_humidity_icon_view);
        mHumidityTv = mContentView.findViewById(R.id.humidity_tv);
        mTodayWeatherWindIconView = mContentView.findViewById(R.id.today_weather_wind_icon_view);
        mWindTv = mContentView.findViewById(R.id.wind_tv);
        mTodayWeatherAirQualityIconView = mContentView.findViewById(R.id.today_weather_air_quality_icon_view);
        mAirQualityTv = mContentView.findViewById(R.id.air_quality_tv);
        mLimitContentLl = mContentView.findViewById(R.id.limit_content_ll);
        mTodayWeatherLimitIconView = mContentView.findViewById(R.id.today_weather_limit_icon_view);
        mLimitTv = mContentView.findViewById(R.id.limit_tv);

        mTodayWeatherDetail1TitleTv = mContentView.findViewById(R.id.today_weather_detail_1_title_tv);
        mTodayWeatherDetail1IconView = mContentView.findViewById(R.id.today_weather_detail_1_icon_view);
        mTodayWeatherDetail1TemperatureTv = mContentView.findViewById(R.id.today_weather_detail_1_temperature_tv);
        mTodayWeatherDetail1WeatherTv = mContentView.findViewById(R.id.today_weather_detail_1_weather_tv);
        mTodayWeatherDetail1WeatherDescTv = mContentView.findViewById(R.id.today_weather_detail_1_weather_desc_tv);
        mTodayWeatherDetail1WindIconView = mContentView.findViewById(R.id.today_weather_detail_1_wind_icon_view);
        mTodayWeatherDetail1WindTv = mContentView.findViewById(R.id.today_weather_detail_1_wind_tv);
        mTodayWeatherDetail1SunIconView = mContentView.findViewById(R.id.today_weather_detail_1_sun_icon_view);
        mTodayWeatherDetail1SunTimeTv = mContentView.findViewById(R.id.today_weather_detail_1_sun_time_tv);

        mTodayWeatherDetail2TitleTv = mContentView.findViewById(R.id.today_weather_detail_2_title_tv);
        mTodayWeatherDetail2IconView = mContentView.findViewById(R.id.today_weather_detail_2_icon_view);
        mTodayWeatherDetail2TemperatureTv = mContentView.findViewById(R.id.today_weather_detail_2_temperature_tv);
        mTodayWeatherDetail2WeatherTv = mContentView.findViewById(R.id.today_weather_detail_2_weather_tv);
        mTodayWeatherDetail2WeatherDescTv = mContentView.findViewById(R.id.today_weather_detail_2_weather_desc_tv);
        mTodayWeatherDetail2WindIconView = mContentView.findViewById(R.id.today_weather_detail_2_wind_icon_view);
        mTodayWeatherDetail2WindTv = mContentView.findViewById(R.id.today_weather_detail_2_wind_tv);
        mTodayWeatherDetail2SunIconView = mContentView.findViewById(R.id.today_weather_detail_2_sun_icon_view);
        mTodayWeatherDetail2SunTimeTv = mContentView.findViewById(R.id.today_weather_detail_2_sun_time_tv);

        mLoadErrorBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromNetByLocation(mLocationInfo);
            }
        });

        mAddressInfoLl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CityInfoListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        boolean isNeedStartRequestPermissionActivity = false;

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            isNeedStartRequestPermissionActivity = true;
        }

        DebugUtil.d(TAG,"init::isNeedStartRequestPermissionActivity = " + isNeedStartRequestPermissionActivity);

        mLoadingView.setVisibility(VISIBLE);
        mLoadErrorView.setVisibility(GONE);
        mTodayWeatherContentView.setVisibility(INVISIBLE);
        if (isNeedStartRequestPermissionActivity) {
            EventBusUtils.post(new RequestLocatePermissionEvent());
        } else {
            doLocate();
//            mLocationInfo = "31.950454,118.809312";
//            getDataFromNetByLocation(mLocationInfo);
        }
    }

    private void getDataFromNetByLocation(String l) {
        DebugUtil.d(TAG,"getDataFromNetByLocation::isLoading = " + isLoading);
        if (isLoading) {
            return;
        }
        isLoading = true;

        mLoadingView.setVisibility(VISIBLE);
        mLoadErrorView.setVisibility(GONE);
        mTodayWeatherContentView.setVisibility(INVISIBLE);

        HttpUtils.getInstance().getZHTianQiByLocation(mContext, l, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                DebugUtil.d(TAG,"onSuccess::responseBaseBean = " + responseBaseBean);

                TodayWeatherResponseBean todayWeatherResponseBean = (TodayWeatherResponseBean) responseBaseBean;

                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(GONE);
                mTodayWeatherContentView.setVisibility(VISIBLE);

                setDataToView(todayWeatherResponseBean);

                isLoading = false;
                isNeedLoadData = false;
            }

            @Override
            public void onNetError() {
                DebugUtil.d(TAG,"onNetError");
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mTodayWeatherContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(R.string.no_network_tip);

                isLoading = false;
            }

            @Override
            public void onNetError(Throwable e) {
                DebugUtil.d(TAG,"onNetError::e = " + e);
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mTodayWeatherContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(mContext.getResources().getString(R.string.network_error_tip, ""));

                isLoading = false;
            }

            @Override
            public void onServer(ResponseBaseBean responseBaseBean) {
                DebugUtil.d(TAG,"onServer::responseBaseBean = " + responseBaseBean);
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mTodayWeatherContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(mContext.getResources().getString(R.string.server_error_tip, responseBaseBean.desc));

                isLoading = false;
            }
        });
    }

    private void getDataFromNetByCity(String City) {
        DebugUtil.d(TAG,"getDataFromNetByCity::isLoading = " + isLoading);
        if (isLoading) {
            return;
        }
        isLoading = true;

        mLoadingView.setVisibility(VISIBLE);
        mLoadErrorView.setVisibility(GONE);
        mTodayWeatherContentView.setVisibility(INVISIBLE);

        HttpUtils.getInstance().getZHTianQiByCity(mContext, City, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                DebugUtil.d(TAG,"onSuccess::responseBaseBean = " + responseBaseBean);

                TodayWeatherResponseBean todayWeatherResponseBean = (TodayWeatherResponseBean) responseBaseBean;

                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(GONE);
                mTodayWeatherContentView.setVisibility(VISIBLE);

                setDataToView(todayWeatherResponseBean);

                isLoading = false;
                isNeedLoadData = false;
            }

            @Override
            public void onNetError() {
                DebugUtil.d(TAG,"onNetError");
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mTodayWeatherContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(R.string.no_network_tip);

                isLoading = false;
            }

            @Override
            public void onNetError(Throwable e) {
                DebugUtil.d(TAG,"onNetError::e = " + e);
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mTodayWeatherContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(mContext.getResources().getString(R.string.network_error_tip, ""));

                isLoading = false;
            }

            @Override
            public void onServer(ResponseBaseBean responseBaseBean) {
                DebugUtil.d(TAG,"onServer::responseBaseBean = " + responseBaseBean);
                mLoadingView.setVisibility(GONE);
                mLoadErrorView.setVisibility(VISIBLE);
                mTodayWeatherContentView.setVisibility(INVISIBLE);
                mLoadErrorTv.setText(mContext.getResources().getString(R.string.server_error_tip, responseBaseBean.desc));

                isLoading = false;
            }
        });
    }


    private void setDataToView(TodayWeatherResponseBean todayWeatherResponseBean) {
        setToolbarTitle(todayWeatherResponseBean.today_weather.simple_content);

        mAddressInfo.setText(todayWeatherResponseBean.address_info);
        mNowTimeTv.setText(todayWeatherResponseBean.today_weather.now_time);
        mTodayWeatherTemperatureView.setTodayWeatherTemperatureIconCss(todayWeatherResponseBean.today_weather.temperature_icon_css);
        mTemperatureTv.setText(todayWeatherResponseBean.today_weather.temperature + "°C");
        if(todayWeatherResponseBean.today_weather.is_h == 1){
            mHumidityTv.setVisibility(VISIBLE);
            mTodayWeatherHumidityIconView.setVisibility(VISIBLE);

            mHumidityTv.setText("相对湿度 " + todayWeatherResponseBean.today_weather.humidity);
            mTodayWeatherHumidityIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather.humidity_icon_css);
        }else {
            mHumidityTv.setVisibility(INVISIBLE);
            mTodayWeatherHumidityIconView.setVisibility(INVISIBLE);
        }
        if(todayWeatherResponseBean.today_weather.is_w == 1) {
            mTodayWeatherWindIconView.setVisibility(VISIBLE);
            mWindTv.setVisibility(VISIBLE);

            mTodayWeatherWindIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather.wind_icon_css);
            mWindTv.setText(todayWeatherResponseBean.today_weather.wind_direction + " " + todayWeatherResponseBean.today_weather.wind_value);
        }else{
            mTodayWeatherWindIconView.setVisibility(INVISIBLE);
            mWindTv.setVisibility(INVISIBLE);
        }
        if(todayWeatherResponseBean.today_weather.is_pol == 1) {
            mTodayWeatherAirQualityIconView.setVisibility(VISIBLE);
            mAirQualityTv.setVisibility(VISIBLE);

            mTodayWeatherAirQualityIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather.air_quality_icon_css);
            mAirQualityTv.setText(todayWeatherResponseBean.today_weather.air_quality);
        }else{
            mTodayWeatherAirQualityIconView.setVisibility(INVISIBLE);
            mAirQualityTv.setVisibility(INVISIBLE);
        }
        if(todayWeatherResponseBean.today_weather.is_limit == 1){
            mLimitContentLl.setVisibility(VISIBLE);

            mTodayWeatherLimitIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather.limit_icon_css);
            mLimitTv.setText(todayWeatherResponseBean.today_weather.limit_content);
        }else{
            mLimitContentLl.setVisibility(INVISIBLE);
        }

        mTodayWeatherDetail1TitleTv.setText(todayWeatherResponseBean.today_weather_detail.get(0).title);
        mTodayWeatherDetail1IconView.setTodayWeatherDetailIconCss(todayWeatherResponseBean.today_weather_detail.get(0).weather_icon_css);
        mTodayWeatherDetail1TemperatureTv.setText(todayWeatherResponseBean.today_weather_detail.get(0).temperature + "°C");
        mTodayWeatherDetail1WeatherTv.setText(todayWeatherResponseBean.today_weather_detail.get(0).weather);
        String weatherDesc1 = todayWeatherResponseBean.today_weather_detail.get(0).weather_desc;
        if (!TextUtils.isEmpty(weatherDesc1)) {
            mTodayWeatherDetail1WeatherDescTv.setVisibility(VISIBLE);
            mTodayWeatherDetail1WeatherDescTv.setText(weatherDesc1);
        } else {
            mTodayWeatherDetail1WeatherDescTv.setVisibility(GONE);
        }
        mTodayWeatherDetail1WindIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather_detail.get(0).wind_icon_css);
        mTodayWeatherDetail1WindTv.setText(todayWeatherResponseBean.today_weather_detail.get(0).wind_direction + " " + todayWeatherResponseBean.today_weather_detail.get(0).wind_value);
        mTodayWeatherDetail1SunIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather_detail.get(0).sun_icon_css);
        mTodayWeatherDetail1SunTimeTv.setText(todayWeatherResponseBean.today_weather_detail.get(0).sun_time);

        mTodayWeatherDetail2TitleTv.setText(todayWeatherResponseBean.today_weather_detail.get(1).title);
        mTodayWeatherDetail2IconView.setTodayWeatherDetailIconCss(todayWeatherResponseBean.today_weather_detail.get(1).weather_icon_css);
        mTodayWeatherDetail2TemperatureTv.setText(todayWeatherResponseBean.today_weather_detail.get(1).temperature + "°C");
        mTodayWeatherDetail2WeatherTv.setText(todayWeatherResponseBean.today_weather_detail.get(1).weather);
        String weatherDesc2 = todayWeatherResponseBean.today_weather_detail.get(1).weather_desc;
        if (!TextUtils.isEmpty(weatherDesc2)) {
            mTodayWeatherDetail2WeatherDescTv.setVisibility(VISIBLE);
            mTodayWeatherDetail2WeatherDescTv.setText(weatherDesc2);
        } else {
            mTodayWeatherDetail2WeatherDescTv.setVisibility(GONE);
        }
        mTodayWeatherDetail2WindIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather_detail.get(1).wind_icon_css);
        mTodayWeatherDetail2WindTv.setText(todayWeatherResponseBean.today_weather_detail.get(1).wind_direction + " " + todayWeatherResponseBean.today_weather_detail.get(1).wind_value);
        mTodayWeatherDetail2SunIconView.setTodayWeatherHumidityIconCss(todayWeatherResponseBean.today_weather_detail.get(1).sun_icon_css);
        mTodayWeatherDetail2SunTimeTv.setText(todayWeatherResponseBean.today_weather_detail.get(1).sun_time);
    }


    public void doLocate() {
        DebugUtil.d(TAG,"doLocate");
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        DebugUtil.d(TAG,"can doLocate");

        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            DebugUtil.d(TAG, "NETWORK_PROVIDER");
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6 * 60 * 1000, 10, mLocationListener);
        }else if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            DebugUtil.d(TAG,"GPS_PROVIDER");
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6 * 60 * 1000, 10, mLocationListener);
        }else{
            //将手机位置服务中--基于网络的位置服务关闭后，则获取不到数据
            DebugUtil.d(TAG,"NETWORK_PROVIDER不可用，无法获取GPS信息!");
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DebugUtil.d(TAG,"onAttachedToWindow");
        EventBusUtils.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DebugUtil.d(TAG,"onDetachedFromWindow");
        EventBusUtils.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocatePermissionSuccessEvent(LocatePermissionSuccessEvent event){
        doLocate();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectCityEvent(SelectCityEvent event){
        setToolbarTitle(event.mCityInfo.city_name+"的天气");
        getDataFromNetByCity(event.mCityInfo.city_name);
    }

    public void setToolbar(Toolbar toolbar){
        mToolbar = toolbar;
    }

    public void setToolbarTitle(String s){
        if(mToolbar != null){
            //mToolbar.setTitle(s);
            TextView tv = mToolbar.findViewById(R.id.toolbar_title);
            tv.setText(s);
        }
    }
}
