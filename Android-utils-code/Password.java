package com.wku.wellcover;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 *@Description
 *     Password Verification 密码验证
 *     1.Request public key from server (by Client) 向服务器请求公钥（客户端）
 *     2.Return public key to client (by Server) 服务器返回公钥（服务器）
 *     3.Encrypt password by public key, return back to server (by Client) 用公钥对密码加密，返回给服务器（客户端）
 *     4.Decrypt password by private key (by Server) 服务器用私钥对密码解密，进行比对，返回密码是否正确（服务器）
 *
 *     @version 0.1
 *     @time 2021/03/27
 *     @issue require api>=26 (function: decryptBASE64)
 *     @issue some functions needs to be implemented in server noted by @server
 *     @require http/tcp to interact with server
 *
 *     @reference https://blog.csdn.net/zhaoxiaojian1213/article/details/79472120
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class Password {
    private static final String KEY_ALGORITHM = "";

    /**
    generate public key and private key
    生成公钥私钥
     @server
     */
    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put("PUBLIC_KEY", publicKey);
        keyMap.put("PRIVATE_KEY", privateKey);
        return keyMap;
    }

    /**
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String key)
            throws Exception {
        // 对公钥解码
        byte[] keyBytes = decryptBASE64(key);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }


    /**
     * 解密<br>
     * 用私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     * @server
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * BASE64解密
     * @throws Exception
     * @RequireAPI 26
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] decryptBASE64(String key) throws Exception {
        byte[] keyBytes = Base64.getUrlDecoder().decode(key);
        return keyBytes;
    }

    /**
     * BASE64加密
     * @throws Exception
     * @RequireAPI 26
     * @server
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptBASE64(byte[] key) throws Exception {
        String keyStr = Base64.getUrlEncoder().encodeToString(key);
        return keyStr;
    }
}
