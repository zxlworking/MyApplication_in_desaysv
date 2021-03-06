package com.dsv.test_dh;

import java.security.KeyPair;

/**
 * 密钥对
 *
 * @author jianggujin
 *
 */
public class HQKeyPair
{
    private byte[] privateKey;
    private byte[] publicKey;

    public HQKeyPair()
    {
    }

    public HQKeyPair(KeyPair keyPair)
    {
        this.privateKey = keyPair.getPrivate().getEncoded();
        this.publicKey = keyPair.getPublic().getEncoded();
    }

    public HQKeyPair(byte[] privateKey, byte[] publicKey)
    {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public byte[] getPrivateKey()
    {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey)
    {
        this.privateKey = privateKey;
    }

    public byte[] getPublicKey()
    {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey)
    {
        this.publicKey = publicKey;
    }
}
