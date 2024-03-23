package tech.aomi.common.message;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.aomi.common.message.entity.SignType;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.*;

/**
 * @author Sean createAt 2021/7/10
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultKeyService implements KeyService {

    private static final String AES = "AES";
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";


    private final Integer aesKeyLength;
    private final String aesTransformation;

    private final String rsaTransformation;

    private final String rsaSignAlgorithms;

    public DefaultKeyService() {
        this(
                128,
                "AES/CBC/PKCS5Padding",
                "RSA/ECB/PKCS1Padding",
                "SHA512WithRSA"
        );
    }

    @SneakyThrows
    @Override
    public byte[] generateAesKey() {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(aesKeyLength, SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM));
        SecretKey key = generator.generateKey();
        return key.getEncoded();
    }

    @SneakyThrows
    @Override
    public byte[] aesEncrypt(byte[] key, byte[] plaintext) {
        SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        secureRandom.setSeed(key);
        KeyGenerator kgen = KeyGenerator.getInstance(AES);
        kgen.init(aesKeyLength, secureRandom);

        SecretKeySpec secretKeySpec = new SecretKeySpec(kgen.generateKey().getEncoded(), AES);

        Cipher cipher = Cipher.getInstance(aesTransformation);
        IvParameterSpec iv = new IvParameterSpec(key);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

        return cipher.doFinal(plaintext);
    }

    @SneakyThrows
    @Override
    public byte[] aesDecrypt(byte[] key, byte[] ciphertext) {
        SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        secureRandom.setSeed(key);
        KeyGenerator kgen = KeyGenerator.getInstance(AES);
        kgen.init(aesKeyLength, secureRandom);

        SecretKeySpec secretKeySpec = new SecretKeySpec(kgen.generateKey().getEncoded(), AES);

        Cipher cipher = Cipher.getInstance(aesTransformation);
        IvParameterSpec iv = new IvParameterSpec(key);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        return cipher.doFinal(ciphertext);
    }

    @Override
    public byte[] publicKeyEncrypt(PublicKey publicKey, int blockSize, byte[] plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance(rsaTransformation);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int offset = 0; offset < plaintext.length; offset += blockSize) {
            int inputLen = plaintext.length - offset;
            if (inputLen > blockSize) {
                inputLen = blockSize;
            }
            byte[] block = cipher.doFinal(plaintext, offset, inputLen);
            outputStream.write(block);
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte[] privateKeyDecrypt(PrivateKey privateKey, int blockSize, byte[] ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance(rsaTransformation);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int offset = 0; offset < ciphertext.length; offset += blockSize) {
            int inputLen = ciphertext.length - offset;
            if (inputLen > blockSize) {
                inputLen = blockSize;
            }
            byte[] block = cipher.doFinal(ciphertext, offset, inputLen);
            outputStream.write(block);
        }
        return outputStream.toByteArray();
    }

    @Override
    public byte[] sign(byte[] message, SignType signType, Key key) throws Exception {
        switch (signType) {
            case RSA:
                return rsaSign(message, (PrivateKey) key);
        }
        return null;
    }

    @Override
    public boolean verify(byte[] message, SignType signType, byte[] sign, Key key) throws Exception {
        switch (signType) {
            case RSA:
                return rsaVerify(message, sign, (PublicKey) key);
        }
        return false;
    }

    private byte[] rsaSign(byte[] message, PrivateKey privateKey) throws Exception {
        if (null == message) {
            LOGGER.warn("签名数据为NULL");
            return new byte[0];
        }
        Signature signature = Signature.getInstance(rsaSignAlgorithms);
        signature.initSign(privateKey);
        signature.update(message);
        return signature.sign();

    }

    private boolean rsaVerify(byte[] message, byte[] sign, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(rsaSignAlgorithms);
        signature.initVerify(publicKey);
        signature.update(message);
        return signature.verify(sign);
    }
}