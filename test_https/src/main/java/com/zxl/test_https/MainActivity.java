package com.zxl.test_https;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;


public class MainActivity extends AppCompatActivity {
    private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型 固定值
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型 固定值

    private static final String KEY_STORE_CLIENT_PATH = "client.p12";//客户端要给服务器端认证的证书
    private static final String KEY_STORE_TRUST_PATH = "ca.truststore";//客户端验证服务器端的证书库
    private static final String CA_CERT_PATH = "ca-cert.pem";//CA证书
    private static final String CA_P12_PATH = "ca.p12";//CA证书
    private static final String KEY_STORE_PASSWORD = "123456";// 客户端证书密码
    private static final String KEY_STORE_TRUST_PASSWORD = "123456";//客户端证书库密码

//    public final static String SERVER_ADDRESS = "https://10.218.5.7:8443/appstore/";
//    public final static String SERVER_ADDRESS = "https://www.test.com:8443/appstore/";
    public final static String SERVER_ADDRESS = "https://www.desaysvnevs.club:8443/appstore/";
//    public final static String SERVER_ADDRESS = "https://www.12306.cn";
    //public final static String SERVER_ADDRESS = "http://testkey_server_cn/test_web/Test";
//    public final static String SERVER_ADDRESS = "https://39.106.3.112/appstore/";

    private String mUrlStr;

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;

//        final JsonObject mLoginParams = new JsonObject();
//        mLoginParams.addProperty("userName", "admin");
//        mLoginParams.addProperty("password", "admin");
//        mUrlStr = SERVER_ADDRESS + "api.jsp?method=" + "userLogin" + "&params=" + mLoginParams.toString();
        mUrlStr = SERVER_ADDRESS;

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL mUrl = new URL(mUrlStr);
                    HttpURLConnection mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
                    mHttpURLConnection.connect();

                    InputStream is;
                    is = mHttpURLConnection.getInputStream();

                    StringBuilder sb = new StringBuilder();
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = is.read(buffer)) != -1) {
                        String s = new String(buffer, 0, count);
                        sb.append(s);
                    }
                    is.close();
                    System.out.println("zxl--->Thread--->result--->" + sb.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */


        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("zxl", "zxl--->mUrlStr--->" + mUrlStr);
                HttpsURLConnection mHttpsURLConnection = getHttpsURLConnection(ctx, mUrlStr, "GET");
                mHttpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                        boolean result = hv.verify("www.desaysvnevs.club",session);
//                        boolean result = hv.verify("kyfw.12306.cn",session);
//                        boolean result = hv.verify("www.test.com",session);
//                        boolean result = hv.verify("desaysv_appstore_server",session);
                        Log.e("zxl","zxl--->hostname--->"+hostname);
                        Log.e("zxl","zxl--->result--->"+result);
                        return result;
                    }
                });

                try {
                    mHttpsURLConnection.connect();
                    int mResponseCode = mHttpsURLConnection.getResponseCode();
                    Log.e("zxl","zxl--->mResponseCode--->"+mResponseCode);
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
                        System.out.println("zxl--->Thread--->result--->" + sb.toString());
                    }
                } catch (Exception e) {
                    System.out.println("zxl--->Thread--->e--->" + e.toString());
                    e.printStackTrace();
                }

            }
        }).start();

    }

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
            System.out.println("zxl--->getHttpsURLConnection--->sslContext = " + sslContext);
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
        System.out.println("zxl--->getHttpsURLConnection--->connection = " + connection);
        return connection;
    }

    private static SSLContext getSSLContext2(Context context){
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream caInput = new BufferedInputStream(context.getResources().openRawResource(R.raw.ca));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("zxl--->ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(context.getResources().getAssets().open(KEY_STORE_TRUST_PATH), KEY_STORE_TRUST_PASSWORD.toCharArray());
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext mSSLContext = SSLContext.getInstance("TLS");
            mSSLContext.init(null, tmf.getTrustManagers(), null);

            return mSSLContext;

            // Tell the URLConnection to use a SocketFactory from our SSLContext
//            URL url = new URL("https://certs.cac.washington.edu/CAtest/");
//            HttpsURLConnection urlConnection =
//                    (HttpsURLConnection)url.openConnection();
//            urlConnection.setSSLSocketFactory(mSSLContext.getSocketFactory());
//            InputStream in = urlConnection.getInputStream();
//            copyInputStreamToOutputStream(in, System.out);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取SSLContext
     *
     * @param context 上下文
     * @return SSLContext
     */
    private static SSLContext getSSLContext(Context context) {

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
                //trustStore.load(null);

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

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(context.getResources().getAssets().open("ca-cert.pem"));
            final Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.i("Longer", "ca=" + ((X509Certificate) ca).getSubjectDN());
                Log.i("Longer", "key=" + ((X509Certificate) ca).getPublicKey());
            } finally {
                caInput.close();
            }
            trustStore.setCertificateEntry("testkey_desaysv_rn1_nevs_ca",ca);



            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            System.out.println("zxl--->trustStore.containsAlias--->"+trustStore.containsAlias("testkey_desaysv_rn1_nevs_ca"));
            System.out.println("zxl--->trustStore.getCertificate--->"+trustStore.getCertificate("testkey_desaysv_rn1_nevs_ca"));

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
            }else{
                sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            }

            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
