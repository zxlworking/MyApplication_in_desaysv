package test.zxl.com.test_http;

import android.app.Activity;
import android.os.Bundle;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("zxl--->start-->");
//                    URL url = new URL("http://1.202.136.14:20189/gwtsp/hu/v2/cccEasyRoutingTest");
                    URL url = new URL("http://1.202.136.14:20189/gwtsp/hu/v2/mqtt/sendTestMsg");
                    HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                    hc.setRequestProperty("User-Agent", "Mozilla/5.0");
                    hc.setDoInput(true);
                    hc.setDoOutput(true);
                    hc.setRequestMethod("POST");
//                    hc.setRequestProperty("Content-Type","application/json");

                    OutputStream os = hc.getOutputStream();
//                    String content = "{\"tspKey\":\"024dfb6c0bfe4ed9aec0bb7953b1fc66\",\"poiList\":[{\"address\":\"北京市东城区王府井大街57号1\",\"pinyin\":\"wanglijiudian\",\"poiName\":\"万丽酒店1\",\"cityCode\":\"110000\",\"nvPid\":\"MAPIHEXRHMTRJPYNJBSNC\",\"lon\":\"116.409789\",\"tel\":\"010-65208888,010-65208999\",\"lat\":\"39.921159\"}],\"vin\":\"25411150XUFEICESH\"}";
                    //String content = "{\"tspKey\":\"024dfb6c0bfe4ed9aec0bb7953b1fc66\",\"apiKey\":\"c54b29b1f94540f58e8f6404f8d8d5ae\",\"title\":\"测试title\",\"message\":\"测试message\",\"params\":{\"expireTime\":\"0\",\"biContent\":{}},\"deviceIds\":\"9088C865-7655-0E3D-040F-3630074F21E0\"}";
                    //String content = "tspKey=024dfb6c0bfe4ed9aec0bb7953b1fc66&poiList=[{\"address\":\"北京市东城区王府井大街57号\",\"pinyin\":\"wanglijiudian\",\"poiName\":\"万丽酒店\",\"cityCode\":\"110000\",\"nvPid\":\"MAPIHEXRHMTRJPYNJBSNC\",\"lon\":\"116.409789\",\"tel\":\"010-65208888,010-65208999\",\"lat\":\"39.921156\"}]&vin=25411150XUFEICESH";
                    String content = "tspKey=024dfb6c0bfe4ed9aec0bb7953b1fc66&apiKey=c54b29b1f94540f58e8f6404f8d8d5ae&title=t&message=m&params={\"expireTime\":\"0\",\"biContent\":{}}&deviceIds=9088C865-7655-0E3D-040F-3630074F21E0";
                    os.write(content.getBytes());
                    //os.flush();
                    os.close();

                    System.out.println("zxl--->write end--->");

                    InputStream is = hc.getInputStream();
                    StringBuffer sb = new StringBuffer();
                    byte[] buffer = new byte[1024];
                    int count = 0;
                    while ((count = is.read(buffer,0,buffer.length)) != -1){
                        String s = new String(buffer,0,count);
                        sb.append(s);
                    }
                    System.out.println("zxl--->sb--->"+sb);

                } catch (Exception e) {
                    System.out.println("zxl--->e--->"+e);
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
