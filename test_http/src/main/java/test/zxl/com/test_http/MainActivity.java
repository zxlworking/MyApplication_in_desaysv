package test.zxl.com.test_http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String url = "https://openapi.baidu.com/oauth/2.0/authorize?client_id=SiuEY9PfqdLiesZZ11CXz4M34qgs5low&qrcode=1&scope=basic&redirect_uri=bdconnect%3A%2F%2Fsuccess&display=mobile&response_type=token";
        String url = "https://success#expires_in=2592000&access_token=23.9758aa960d15fd7bb546af09480ce5af.2592000.1528354062.3894975121-11187497&session_secret=b492ab0804e3a5fc95ac09d73330e43f&session_key=9mtqXInl81DsKPEub3wqoZyudibZT28xYuJDY6CwSLnsgpuiJFxk%2FXL91ASL%2BnSadEzdocbuSp0LBqKG05FDJCNUUPcMLJp8j%2Bo%3D&scope=basic";
        URL urlParam = null;
        try {
            urlParam = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String mQuery = urlParam.getQuery();
        String mRef = urlParam.getRef();
        System.out.println("zxl--->mQuery = " + mQuery);
        System.out.println("zxl--->mRef = " + mRef);
    }
}
