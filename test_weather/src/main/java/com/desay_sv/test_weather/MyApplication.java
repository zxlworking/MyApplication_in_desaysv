package com.desay_sv.test_weather;

import android.app.Application;

import com.desay_sv.test_weather.utils.EventBusUtils;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/9/6.
 */

public class MyApplication extends Application {

    //wx3588f7555119b55e
    //c4df129b87be31e477e921f0471d1a35

    @Override
    public void onCreate() {
        super.onCreate();

        DebugUtil.IS_DEBUG = DebugUtil.STATE_OPEN;
        EventBusUtils.init();
    }

    private void registerToWX() {
//        //第二个参数是指你应用在微信开放平台上的AppID
//        mWxApi = WXAPIFactory.createWXAPI(this, MainConstant.WX.WEIXIN_APP_ID, false);
//        // 将该app注册到微信
//        mWxApi.registerApp(MainConstant.WX.WEIXIN_APP_ID);
    }

}
