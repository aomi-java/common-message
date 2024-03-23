package tech.aomi.common.message.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;

/**
 */
@Slf4j
public class RSAUtils {

    public static final String ALGORITHM = "RSA";

    /**
     * 随机生成一对长度为2048位的RSA秘钥
     */
    public static void generateRSAKeyPair() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            KeyPair keyPair = gen.generateKeyPair();
            PublicKey pubKey = keyPair.getPublic();
            PrivateKey priKey = keyPair.getPrivate();
            String pubEncBase64 = Base64.getEncoder().encodeToString(pubKey.getEncoded());
            String priEncBase64 = Base64.getEncoder().encodeToString(priKey.getEncoded());
            System.out.println("publicKey: " + pubEncBase64);
            System.out.println("privateKey: " + priEncBase64);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从base64字符串种解析公钥
     *
     * @param base64PublicKey base64 表示的公钥
     */
    public static PublicKey parsePublicKeyWithBase64(String base64PublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(pubX509);
    }

    /**
     * 从 base64字符串中解析私钥
     *
     * @param base64PrivateKey 私钥
     */
    public static PrivateKey parsePrivateKeyWithBase64(String base64PrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec pubX509 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(pubX509);
    }

    public static int getBlockSize(Key key) throws InvalidKeySpecException, NoSuchAlgorithmException {
        int keySize = getKeySize(key);
        int blockSize = keySize / 8;
        if (key instanceof PublicKey) {
            return blockSize - 11;
        }
        return blockSize;
    }

    /**
     * 获取密钥的长度
     *
     * @param key 密钥信息
     * @return 密钥长度
     * @throws NoSuchAlgorithmException 无效的算法
     * @throws InvalidKeySpecException  无效的key
     */
    public static int getKeySize(java.security.Key key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFact = KeyFactory.getInstance(key.getAlgorithm());
        if (key instanceof PublicKey) {
            RSAPublicKeySpec keySpec = keyFact.getKeySpec(key, RSAPublicKeySpec.class);
            return keySpec.getModulus().toString(2).length();
        } else if (key instanceof PrivateKey) {
            RSAPrivateCrtKeySpec keySpec = keyFact.getKeySpec(key, RSAPrivateCrtKeySpec.class);
            return keySpec.getModulus().toString(2).length();
        }
        return 0;
    }
}
