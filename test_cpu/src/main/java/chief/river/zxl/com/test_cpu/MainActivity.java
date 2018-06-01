package chief.river.zxl.com.test_cpu;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    try {
                        double mCpuInfo = getCpuUsage();
                        DecimalFormat mDecimalFormat = new DecimalFormat("#.##");
                        System.out.println("zxl--->getCpuUsage--->--->"+ mDecimalFormat.format(mCpuInfo)+"%");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.sendEmptyMessageDelayed(1,1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler.sendEmptyMessage(1);


    }

    double o_idle;
    double o_cpu;
    boolean initCpu = true;
    public double getCpuUsage() throws IOException {
        double usage = 0.0;

        if (initCpu) {
            initCpu = false;
            RandomAccessFile reader = null;
            reader = new RandomAccessFile("/proc/stat","r");
            String load = reader.readLine();
            String[] toks = load.split(" ");
            o_idle = Double.parseDouble(toks[5]);
            o_cpu = Double.parseDouble(toks[2])
                    + Double.parseDouble(toks[3])
                    + Double.parseDouble(toks[4])
                    + Double.parseDouble(toks[6])
                    + Double.parseDouble(toks[7])
                    + Double.parseDouble(toks[8])
                    + Double.parseDouble(toks[9]);
            reader.close();
        } else {
            RandomAccessFile reader = null;
            reader = new RandomAccessFile("/proc/stat", "r");
            String load;
            load = reader.readLine();
            String[] toks = load.split(" ");
            double c_idle = Double.parseDouble(toks[5]);
            double c_cpu = Double.parseDouble(toks[2])
                    + Double.parseDouble(toks[3])
                    + Double.parseDouble(toks[4])
                    + Double.parseDouble(toks[6])
                    + Double.parseDouble(toks[7])
                    + Double.parseDouble(toks[8])
                    + Double.parseDouble(toks[9]);
            if (0 != ((c_cpu + c_idle) - (o_cpu + o_idle))) {
                usage = (100.00 * ((c_cpu - o_cpu)))/((c_cpu + c_idle) - (o_cpu + o_idle));
            }
            o_cpu = c_cpu;
            o_idle = c_idle;
            reader.close();
        }
        return usage;
    }
}
