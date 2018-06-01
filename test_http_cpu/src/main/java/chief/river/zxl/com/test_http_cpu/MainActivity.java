package chief.river.zxl.com.test_http_cpu;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.Hashtable;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity {

    private String url = "https://39.106.3.112/appstore/mobile/app_list.action";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Hashtable<String,String> table = new Hashtable<>();
                table.put("type", ""+3);
                table.put("page", ""+1);
                table.put("ptcode", "CG00251A");
                table.put("cipher", DESUtil.httpEncryption());
                HttpsURLConnection mHttpsURLConnection = getHttpsURLConnection(MainActivity.this, addUrlAndParam(url,table), "GET");
                mHttpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
//                boolean result = hv.verify("www.test.com",session);
                        boolean result = hv.verify("desaysv_appstore_server",session);
                        return result;
                    }
                });

                try {
                    int mResponseCode = mHttpsURLConnection.getResponseCode();
                    if (mResponseCode == 200) {

                        InputStream is;
                        is = mHttpsURLConnection.getInputStream();

                        StringBuilder sb = new StringBuilder();
                        byte[] buffer = new byte[1024];
                        int count;
                        while ((count = is.read(buffer)) != -1) {
                            String s = new String(buffer, 0, count);
                            sb.append(s);
                        }
                        is.close();
                        System.out.println("zxl--->sb--->"+sb.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型 固定值
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型 固定值

    private static final String KEY_STORE_CLIENT_PATH = "client.p12";//客户端要给服务器端认证的证书
    private static final String KEY_STORE_TRUST_PATH = "ca.truststore";//客户端验证服务器端的证书库
    private static final String KEY_STORE_PASSWORD = "123456";// 客户端证书密码
    private static final String KEY_STORE_TRUST_PASSWORD = "123456";//客户端证书库密码
    /**
     * 获取HttpsURLConnection
     *
     * @param context 上下文
     * @param url     连接url
     * @param method  请求方式
     * @return HttpsURLConnection
     */
    public static HttpsURLConnection getHttpsURLConnection(Context context, String url, String method) {
        URL u;
        HttpsURLConnection connection = null;
        try {
            SSLContext sslContext = getSSLContext(context);
            if (sslContext != null) {
                u = new URL(url);
                connection = (HttpsURLConnection) u.openConnection();
                connection.setRequestMethod(method);//"POST" "GET"
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setRequestProperty("Content-Type", "binary/octet-stream");
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
                connection.setConnectTimeout(30000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }


    private static SSLContext sslContext = null;
    /**
     * 获取SSLContext
     *
     * @param context 上下文
     * @return SSLContext
     */
    public static SSLContext getSSLContext(Context context) {
        if(null == sslContext){
            try {
                // 服务器端需要验证的客户端证书
                KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
                // 客户端信任的服务器端证书
                KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);

                InputStream ksIn = context.getResources().getAssets().open(KEY_STORE_CLIENT_PATH);
                InputStream tsIn = context.getResources().getAssets().open(KEY_STORE_TRUST_PATH);
                try {
                    keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());
                    trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        ksIn.close();
                    } catch (Exception ignore) {
                    }
                    try {
                        tsIn.close();
                    } catch (Exception ignore) {
                    }
                }

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(trustStore);

                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
                keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());

                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

                return sslContext;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sslContext;
    }

    public static String addUrlAndParam(String url, Hashtable<String ,String> params){
        String tempUrl = url;
        Set<String> keys = params.keySet();
        int index  = 0;
        for(String key : keys){
            if(index == 0){
                try {
                    tempUrl = tempUrl + "?" + key + "=" + URLEncoder.encode(params.get(key),"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    tempUrl = tempUrl + "&" + key + "=" + URLEncoder.encode(params.get(key),"utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            index++;
        }
        return tempUrl;
    }
}
