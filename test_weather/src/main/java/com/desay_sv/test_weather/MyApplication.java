package com.desay_sv.test_weather;

import android.app.Application;

import com.desay_sv.test_weather.utils.CommonUtils;
import com.desay_sv.test_weather.utils.EventBusUtils;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/9/6.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DebugUtil.IS_DEBUG = DebugUtil.STATE_OPEN;
        EventBusUtils.init();

    }

}
