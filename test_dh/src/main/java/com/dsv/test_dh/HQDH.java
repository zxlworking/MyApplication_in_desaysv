package com.dsv.test_dh;

/**
 * Created by uidq0955 on 2017/10/31.
 */

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

/**
 * Diffie-Hellman算法(D-H算法)，密钥一致协议。是由公开密钥密码体制的奠基人Diffie和Hellman所提出的一种思想。
 *
 * @author jianggujin
 *
 */
public class HQDH
{
    private static HQDH dh = new HQDH();

    public static HQDH getInstance()
    {
        return dh;
    }

    private HQDH()
    {
    }

    /**
     * 对称算法
     *
     * @author jianggujin
     *
     */
    public static enum HQDHSymmetricalAlgorithm
    {
        DES("DES"), DESede("DESede");
        private String name;

        private HQDHSymmetricalAlgorithm(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }
    }

    private final String ALGORITHM = "DH";

    /**
     * 初始化甲方密钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public HQKeyPair initPartyAKey() throws NoSuchAlgorithmException
    {
        return initPartyAKey(1024);
    }

    /**
     * 初始化甲方密钥
     *
     * @param keySize
     * @return
     * @throws NoSuchAlgorithmException
     */
    public HQKeyPair initPartyAKey(int keySize) throws NoSuchAlgorithmException
    {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        return new HQKeyPair(keyPair);
    }

    /**
     * 初始化乙方密钥
     *
     * @param partyAPublicKey
     *           甲方公钥
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidAlgorithmParameterException
     * @throws Exception
     */
    public HQKeyPair initPartyBKey(byte[] partyAPublicKey) throws Exception
    {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(partyAPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        // 由甲方公钥构建乙方密钥
        DHParameterSpec dhParamSpec = ((DHPublicKey) pubKey).getParams();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyFactory.getAlgorithm());
        keyPairGenerator.initialize(dhParamSpec);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        return new HQKeyPair(keyPair);
    }

    public byte[] encrypt(byte[] data, byte[] publicKey, byte[] privateKey, HQDHSymmetricalAlgorithm algorithm)
            throws Exception
    {
        return encrypt(data, publicKey, privateKey, algorithm.getName());
    }

    public byte[] encrypt(byte[] data, byte[] publicKey, byte[] privateKey, String algorithm) throws Exception
    {
        // 生成本地密钥
        SecretKey secretKey = getSecretKey(publicKey, privateKey, algorithm);

        // 数据加密
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data, byte[] publicKey, byte[] privateKey, HQDHSymmetricalAlgorithm algorithm)
            throws Exception
    {
        return decrypt(data, publicKey, privateKey, algorithm.getName());
    }

    public byte[] decrypt(byte[] data, byte[] publicKey, byte[] privateKey, String algorithm) throws Exception
    {
        // 生成本地密钥
        SecretKey secretKey = getSecretKey(publicKey, privateKey, algorithm);
        // 数据解密
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(data);
    }

    private SecretKey getSecretKey(byte[] publicKey, byte[] privateKey, String algorithm) throws Exception
    {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);

        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        Key priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
        keyAgree.init(priKey);
        keyAgree.doPhase(pubKey, true);

        // 生成本地密钥
        SecretKey secretKey = keyAgree.generateSecret(algorithm);

        return secretKey;
    }
}
