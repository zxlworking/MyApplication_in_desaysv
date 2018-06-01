package chief.river.zxl.com.test_retrofit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity {

    private String deviceCode = "";
    private String clientId = "SiuEY9PfqdLiesZZ11CXz4M34qgs5low";
    private String clientSecret = "616iMgae1fHx4Me22U3kvuaS6ADUbviL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("http://39.106.3.112:8080")
                .build();
        RequestDuerOS mRequestDuerOS = mRetrofit.create(RequestDuerOS.class);
        RequestDuerOSDeviceCode mRequestDuerOSDeviceCode = mRetrofit.create(RequestDuerOSDeviceCode.class);


        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            deviceCode = mTelephonyManager.getImei();
        }

        Call<ResponseBody> mRequestDuerOSCall = mRequestDuerOS.request("6818b7661631c0c53219ead25e2c63df",clientId,clientSecret);
        mRequestDuerOSCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println("zxl--->onResponse--->"+response);
                try {
                    String s = new String(response.body().bytes());
                    System.out.println("zxl--->onResponse--->s--->"+s);
                    //{"device_code":"6818b7661631c0c53219ead25e2c63df","user_code":"gxhtywmu","verification_url":"https://openapi.baidu.com/device","qrcode_url":"http://openapi.baidu.com/device/qrcode/ba828f7e705b2244e94639295acf6b73/gxhtywmu","expires_in":1800,"interval":5}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("zxl--->onFailure--->"+t);
            }
        });

//        Call<ResponseBody> mRequestDuerOSDeviceCodeCall = mRequestDuerOSDeviceCode.request("device_code",clientId);
//        mRequestDuerOSDeviceCodeCall.enqueue(new Callback<ResponseBody>() {
//           @Override
//           public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//               System.out.println("zxl--->onResponse--->"+response);
//               try {
//                   String s = new String(response.body().bytes());
//                   System.out.println("zxl--->onResponse--->s--->"+s);
//                   //{"device_code":"6818b7661631c0c53219ead25e2c63df","user_code":"gxhtywmu","verification_url":"https://openapi.baidu.com/device","qrcode_url":"http://openapi.baidu.com/device/qrcode/ba828f7e705b2244e94639295acf6b73/gxhtywmu","expires_in":1800,"interval":5}
//               } catch (IOException e) {
//                   e.printStackTrace();
//               }
//           }
//
//           @Override
//           public void onFailure(Call<ResponseBody> call, Throwable t) {
//               System.out.println("zxl--->onFailure--->"+t);
//           }
//        });


        Request mRequest2 = (Request) Proxy.newProxyInstance(Request.class.getClassLoader(),new Class<?>[] {Request.class}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//				if (method.getDeclaringClass() == Object.class) {
//					System.out.println("invoke Object");
//					return method.invoke(this, args);
//	            }
//				System.out.println("before ProxyRequest request");
//				//Object result = method.invoke(proxy, args);
//				System.out.println("after ProxyRequest request");
                return null;
            }
        });
        System.out.println("zxl--->mRequest2--->"+mRequest2);


        Object MtpObjectInfo = Proxy.newProxyInstance(Request.class.getClassLoader(), new Class<?>[] { Request.class },
                new InvocationHandler() {

                    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("zxl--->xxxx--->");
                        return null;
                    }
                });
        System.out.println("zxl--->MtpObjectInfo--->"+MtpObjectInfo);
    }

    public interface Request{
        @GET("http://39.106.3.112:8080/{path}")
        public Call<ResponseBody> request(@Path("path")String path);
    }

    public interface RequestDuerOS{
        //@GET("https://openapi.baidu.com/oauth/2.0/authorize?client_id=SiuEY9PfqdLiesZZ11CXz4M34qgs5low&qrcode=1&scope=basic&confirm_login=1&redirect_uri=bdconnect%3A%2F%2Fsuccess&display=mobile&response_type=token")
        @GET("https://openapi.baidu.com/oauth/2.0/token?grant_type=device_token")
        public Call<ResponseBody> request(@Query("code") String deviceCode, @Query("client_id")String clientId, @Query("client_secret")String clientSecret);
        //&code={deviceCode}&client_id={clientId}&client_secret={clientSecret}
    }

    public interface RequestDuerOSDeviceCode{
        @GET("https://openapi.baidu.com/oauth/2.0/device/code")
        public Call<ResponseBody> request(@Query("response_type") String responseType, @Query("client_id")String clientId);
    }
}
