package chief.river.zxl.com.test_http_cpu;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 加密解密处理
 * @author 何喆
 */
public class DESUtil {
	private static final String DESKEY = "H3E1Z0H7EEHVCOTL";//密钥，请不要修改
	// 算法名称
	// 算法名称/加密模式/填充方式
	// DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
	private static final String CIPHER_ALGORITHM = "DES/ECB/NoPadding";
 
	
	

	/**
	 * 接口加密处理 ，加密原文为 时间戳以及约定的特殊字符
	 * @return
	 */
	public static String httpEncryption() {
		try { 
			String time1= System.currentTimeMillis() + "_";
			//String time1 = System.currentTimeMillis() + "_";
			return encrypt(fillTheStrHTTP(time1), DESKEY);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//取得资源对象
		return "";
	}

	/**
	 * 将接口返回的数据进行解密 主要针对FTP信息
	 * @param str
	 * @return
	 */
	public static String decryptData(String str) {
		try {
			String source=decrypt(str, DESKEY);
			int index=source.lastIndexOf(".");
			if(index>-1){
				source=source.substring(0,index);
			}
			return source;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 生成密钥key对象
	 * @param keyStr 密钥字符串
	 * @return 密钥对象
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws Exception
	 */
	private static SecretKey keyGenerator(String keyStr) throws Exception {
		byte input[] = HexString2Bytes(keyStr);
		DESKeySpec desKey = new DESKeySpec(input);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		return securekey;
	}

	private static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}

	// 从十六进制字符串到字节数组转换
	private static byte[] HexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}

	/**
	 * 加密数据 
	 * @param data 待加密数据
	 * @param key 密钥
	 * @return 加密后的数据
	 */
	@SuppressLint("TrulyRandom")
	private static String encrypt(String data, String key) throws Exception {
		Key deskey = keyGenerator(key);
		// 实例化Cipher对象，它用于完成实际的加密操作
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		SecureRandom random = new SecureRandom();
		// 初始化Cipher对象，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, deskey, random);
		byte[] results = cipher.doFinal(data.getBytes()); 
		return encryptBASE64(results);
	}

	/**
	 * 解密数据
	 * @param data  待解密数据
	 * @param key 密钥
	 * @return 解密后的数据
	 */
	private static String decrypt(String data, String key) throws Exception {
		Key deskey = keyGenerator(key);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		// 初始化Cipher对象，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, deskey);
		// 执行解密操作
		//return new String(cipher.doFinal(Base64.decodeBase64(data)));
		return new String(cipher.doFinal(decryptBASE64(data)));
	}

	/**
	 * 填充字符串
	 * @param str
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String fillTheStrHTTP(String str) {
		if (str == null) {
			str = "";
		}
		try {
			int len = str.getBytes("UTF-8").length % 8;
			switch (len) {
			case 1:
				str += "SVSVSVS";
				break;
			case 2:
				str += "SVSVSV";
				break;
			case 3:
				str += "SVSVS";
				break;
			case 4:
				str += "SVSV";
				break;
			case 5:
				str += "SVS";
				break;
			case 6:
				str += "SV";
				break;
			case 7:
				str += "S";
				break;
			default:
				break;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * BASE64 加密
	 * @param encode
	 * @return
	 */
	private static String encryptBASE64(byte[] encode) {
		try {
			return new String(Base64.encode(encode, 0, encode.length, Base64.DEFAULT), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * BASE64 解密
	 * @param str
	 * @return
	 */
	private static byte[] decryptBASE64(String str) {
		byte[] encode = str.getBytes();
		return Base64.decode(encode, 0, encode.length, Base64.DEFAULT);
	}
}