package com.xziying.appstore.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/**
 * RSAUtil RAS加密类
 *
 * @author : xziying
 * @create : 2021-04-11 13:04
 */
public class RSAUtil {

    public static byte[] decrypt(byte[] bytes, String privateKey) throws Exception{
        //64位解码加密后的字符串
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(bytes);
    }


    public static byte[] decryptLong(byte[] bytes, String publicKey) throws Exception{
        int MAX_ENCRYPT_BLOCK = 128;
        if (bytes.length % MAX_ENCRYPT_BLOCK != 0){
            return new byte[0];
        }
        ByteArrayOutputStream returnValue = new ByteArrayOutputStream();


        for (int i = 0; i < bytes.length; i+=MAX_ENCRYPT_BLOCK) {
            byte[] decrypt = decrypt(Arrays.copyOfRange(bytes, i, i + MAX_ENCRYPT_BLOCK), publicKey);
            returnValue.write(decrypt);
        }
        return returnValue.toByteArray();
    }


}
