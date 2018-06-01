package com.dsv.test_dh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;

import static android.util.Base64.DEFAULT;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DHTest mDHTest = new DHTest();
        try {
            mDHTest.encode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DHTest
    {
        HQDH dh = HQDH.getInstance();

        public void encode() throws Exception
        {
            byte[] data = "jianggujin".getBytes();
            HQKeyPair keyPairA = dh.initPartyAKey();
            System.out.println("zxl--->甲方私钥：" + Base64.encodeToString(keyPairA.getPrivateKey(),DEFAULT));
            System.out.println("zxl--->甲方公钥：" + Base64.encodeToString(keyPairA.getPublicKey(),DEFAULT));
            HQKeyPair keyPairB = dh.initPartyBKey(keyPairA.getPublicKey());
            //HQKeyPair keyPairB = dh.initPartyAKey();
            System.out.println("zxl--->乙方私钥：" + Base64.encodeToString(keyPairB.getPrivateKey(),DEFAULT));
            System.out.println("zxl--->乙方公钥：" + Base64.encodeToString(keyPairB.getPublicKey(),DEFAULT));

            HQDH.HQDHSymmetricalAlgorithm[] algorithms = HQDH.HQDHSymmetricalAlgorithm.values();
            for (HQDH.HQDHSymmetricalAlgorithm algorithm : algorithms)
            {
                System.out.println("zxl--->=========================================");
                System.out.println("zxl--->"+algorithm);
                byte[] result = dh.encrypt(data, keyPairB.getPublicKey(), keyPairA.getPrivateKey(), algorithm);
                System.out.println("zxl--->加密：" + Base64.encodeToString(result,DEFAULT));
                System.out.println(
                        "zxl--->解密：" + new String(dh.decrypt(result, keyPairA.getPublicKey(), keyPairB.getPrivateKey(), algorithm)));
            }
        }
    }
}
