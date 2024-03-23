package tech.aomi.common.message;


import tech.aomi.common.message.entity.SignType;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Sean createAt 2021/7/9
 */
public interface KeyService {

    /**
     * 生成AES秘钥
     */
    byte[] generateAesKey();

    byte[] aesEncrypt(byte[] key, byte[] plaintext);

    byte[] aesDecrypt(byte[] key, byte[] ciphertext);

    byte[] publicKeyEncrypt(PublicKey publicKey, int blockSize, byte[] plaintext) throws Exception;

    byte[] privateKeyDecrypt(PrivateKey privateKey, int blockSize, byte[] ciphertext) throws Exception;

    byte[] sign(byte[] message, SignType signType, Key key) throws Exception;

    boolean verify(byte[] message, SignType signType, byte[] sign, Key key) throws Exception;

}