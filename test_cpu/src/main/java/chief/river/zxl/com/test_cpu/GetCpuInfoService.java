package chief.river.zxl.com.test_cpu;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by uidq0955 on 2018/4/25.
 */

public class GetCpuInfoService extends Service {

    private static final String TAG = "GetCpuInfoService";

    private static final int MSG_GET_CPU_INFO = 1;

    private CpuTracker mStats = null;

    private Toast mToast = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_GET_CPU_INFO:
                    mStats.update();
                    sendEmptyMessageDelayed(MSG_GET_CPU_INFO, 10 * 1000);
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mStats = new CpuTracker(false);
        mStats.init();

        mHandler.sendEmptyMessageDelayed(MSG_GET_CPU_INFO, 30 * 1000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class CpuTracker extends ProcessCpuTracker {

        public CpuTracker(boolean includeThreads) {
            super(includeThreads);
        }

        @Override
        public void onLoadChanged(float load1, float load5, float load15) {
            float mCpuPercent = getTotalCpuPercent();
            if(mCpuPercent >= 95){
                Log.i(TAG,"onLoadChanged::mCpuPercent = " + mCpuPercent);
                if(mToast != null){
                    mToast.cancel();
                    mToast = null;
                }
                if(null == mToast){
                    mToast = Toast.makeText(GetCpuInfoService.this,getString(com.android.systemui.R.string.cpu_hight_info),Toast.LENGTH_SHORT);
                }
                mToast.show();
            }
        }
    }
}
