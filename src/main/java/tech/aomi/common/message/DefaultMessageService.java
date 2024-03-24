package tech.aomi.common.message;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tech.aomi.common.message.entity.MessageContent;
import tech.aomi.common.message.entity.RequestMessage;
import tech.aomi.common.message.entity.ResponseMessage;
import tech.aomi.common.message.entity.SignType;
import tech.aomi.common.message.exception.MessageDecryptException;
import tech.aomi.common.message.exception.MessageEncryptException;
import tech.aomi.common.message.exception.MessageSignException;
import tech.aomi.common.message.exception.MessageVerifyException;
import tech.aomi.common.message.utils.RSAUtils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 签名验证服务
 *
 * @author Sean createAt 2021/6/22
 */
@Slf4j
public class DefaultMessageService implements MessageService {

    private final String clientId;
    /**
     * 己方私钥
     */
    private final PrivateKey privateKey;
    private final int privateKeyBlockSize;
    private final PublicKey publicKey;
    private final int publicKeyBlockSize;
    /**
     * 对方公钥
     */
    private final PublicKey otherPartyPublicKey;
    private final int otherPartyPublicKeyBlockSize;

    private final DateTimeFormatter timestampFormat;
    private final Charset charset;
    private final SignType signType;

    private final KeyService keyService;

    private final MessageEncodeDecodeService messageEncodeDecodeService;

    public DefaultMessageService(
            String base64PublicKey,
            String base64PrivateKey,
            String base64OtherPartyPublicKey,
            String clientId,
            MessageEncodeDecodeService messageEncodeDecodeService
    ) {
        this(
                base64PublicKey,
                base64PrivateKey,
                base64OtherPartyPublicKey,
                clientId,
                "yyyy-MM-dd HH:mm:ss.SSS",
                "UTF-8",
                SignType.RSA,
                messageEncodeDecodeService
        );
    }

    public DefaultMessageService(
            String base64PublicKey,
            String base64PrivateKey,
            String base64OtherPartyPublicKey,
            String clientId,
            String timestampFormat,
            String charset,
            SignType signType,
            MessageEncodeDecodeService messageEncodeDecodeService
    ) {
        this(
                base64PublicKey,
                base64PrivateKey,
                base64OtherPartyPublicKey,
                clientId,
                timestampFormat,
                charset,
                signType,
                new DefaultKeyService(),
                messageEncodeDecodeService
        );
    }

    @SneakyThrows
    public DefaultMessageService(
            String base64PublicKey,
            String base64PrivateKey,
            String base64OtherPartyPublicKey,
            String clientId,
            String timestampFormat,
            String charset,
            SignType signType,
            KeyService keyService,
            MessageEncodeDecodeService messageEncodeDecodeService
    ) {
        this.publicKey = RSAUtils.parsePublicKeyWithBase64(base64PublicKey);
        this.publicKeyBlockSize = RSAUtils.getBlockSize(this.publicKey);
        this.privateKey = RSAUtils.parsePrivateKeyWithBase64(base64PrivateKey);
        this.privateKeyBlockSize = RSAUtils.getBlockSize(this.privateKey);
        this.otherPartyPublicKey = RSAUtils.parsePublicKeyWithBase64(base64OtherPartyPublicKey);
        this.otherPartyPublicKeyBlockSize = RSAUtils.getBlockSize(this.otherPartyPublicKey);

        this.clientId = clientId;
        this.timestampFormat = DateTimeFormatter.ofPattern(timestampFormat).withZone(ZoneId.systemDefault());
        this.charset = Charset.forName(Optional.ofNullable(charset).orElse("UTF-8"));
        this.signType = Optional.ofNullable(signType).orElse(SignType.RSA);
        this.keyService = keyService;
        this.messageEncodeDecodeService = messageEncodeDecodeService;
    }

    @Override
    public String requestId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public String randomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public String timestamp() {
        return this.timestampFormat.format(Instant.now());
    }

    @Override
    public Charset charset() {
        return this.charset;
    }

    @Override
    public SignType signType() {
        return this.signType;
    }

    @Override
    public byte[] getSignData(RequestMessage body) {
        String signData = body.getTimestamp() + body.getRandomString() + Optional.ofNullable(body.getPayload()).orElse("");
        LOGGER.debug("请求签名数据: [{}]", signData);
        return sha512(signData);
    }

    @Override
    public byte[] getSignData(ResponseMessage body) {
        String signData = body.getTimestamp() + body.getRandomString() + body.getStatus() + Optional.ofNullable(body.getPayload()).orElse("");
        LOGGER.debug("响应签名数据: [{}]", signData);
        return sha512(signData);
    }

    @Override
    public MessageContent create(Object payload) {
        MessageContent content = new MessageContent();

        RequestMessage message = new RequestMessage();
        byte[] trk = this.keyService.generateAesKey();
        content.setTrk(trk);
        try {
            byte[] encryptKey = this.keyService.publicKeyEncrypt(this.otherPartyPublicKey, this.otherPartyPublicKeyBlockSize, trk);
            message.setTrk(Base64.getEncoder().encodeToString(encryptKey));
        } catch (Exception e) {
            throw new MessageEncryptException("传输密钥TRK加密失败", e);
        }
        Optional.ofNullable(payload).ifPresent(p -> {
            byte[] payloadBytes = this.messageEncodeDecodeService.message2Byte(p);
            content.setRequestPayload(payloadBytes);
            try {
                byte[] encryptPayload = this.keyService.aesEncrypt(trk, payloadBytes);
                message.setPayload(Base64.getEncoder().encodeToString(encryptPayload));
            } catch (Exception e) {
                throw new MessageEncryptException("Payload加密失败", e);
            }
        });

        message.setClientId(this.clientId);
        message.setTimestamp(timestamp());
        message.setRandomString(randomString());
        message.setCharset(charset().name());
        message.setSignType(signType());
        message.setSign(this.sign(message.getSignType(), this.getSignData(message)));

        content.setRequestMessage(message);
        content.setPublicKey(this.publicKey);
        content.setPrivateKey(this.privateKey);
        content.setOtherPartyPublicKey(this.otherPartyPublicKey);
        return content;
    }

    @Override
    public MessageContent parse(Map<String, String> args) {
        return this.parse(new RequestMessage(args));
    }

    @Override
    public MessageContent parse(byte[] args) {
        RequestMessage message = this.messageEncodeDecodeService.byte2Message(args, RequestMessage.class);
        return this.parse(message);
    }

    @Override
    public MessageContent parse(RequestMessage message) {
        MessageContent content = new MessageContent();
        this.verify(message.getSignType(), message.getSign(), this.getSignData(message));

        try {
            byte[] encryptTrk = Base64.getDecoder().decode(message.getTrk());
            byte[] trk = this.keyService.privateKeyDecrypt(this.privateKey, this.privateKeyBlockSize, encryptTrk);
            content.setTrk(trk);
        } catch (Exception e) {
            throw new MessageDecryptException("TRK密钥解密失败", e);
        }

        Optional.ofNullable(message.getPayload()).ifPresent(p -> {
            byte[] encryptPayload = Base64.getDecoder().decode(p);
            var payload = this.keyService.aesDecrypt(content.getTrk(), encryptPayload);
            content.setRequestPayload(payload);
        });

        content.setRequestMessage(message);
        content.setPublicKey(this.publicKey);
        content.setPrivateKey(this.privateKey);
        content.setOtherPartyPublicKey(this.otherPartyPublicKey);

        return content;
    }

    @Override
    public void createResponse(MessageContent content, String status, String describe, Object payload) {
        ResponseMessage message = new ResponseMessage();
        message.setTimestamp(timestamp());
        message.setRandomString(randomString());
        message.setCharset(content.getRequestMessage().getCharset());
        message.setSignType(content.getRequestMessage().getSignType());

        message.setStatus(status);
        message.setDescribe(describe);
        message.setSuccess("0000".equals(status));

        Optional.ofNullable(payload).ifPresent(p -> {
            byte[] payloadBytes = this.messageEncodeDecodeService.message2Byte(p);
            content.setResponsePayload(payloadBytes);
            try {
                byte[] encryptPayload = this.keyService.aesEncrypt(content.getTrk(), payloadBytes);
                message.setPayload(Base64.getEncoder().encodeToString(encryptPayload));
            } catch (Exception e) {
                throw new MessageEncryptException("Payload加密失败", e);
            }
        });

        message.setSign(this.sign(message.getSignType(), this.getSignData(message)));
        content.setResponseMessage(message);
    }

    @Override
    public void parseResponse(MessageContent content, byte[] args) {
        ResponseMessage message = this.messageEncodeDecodeService.byte2Message(args, ResponseMessage.class);
        this.parseResponse(content, message);
    }

    @Override
    public void parseResponse(MessageContent content, ResponseMessage message) {
        this.verify(message.getSignType(), message.getSign(), this.getSignData(message));

        Optional.ofNullable(message.getPayload()).ifPresent(p -> {
            byte[] encryptPayload = Base64.getDecoder().decode(p);
            var payload = this.keyService.aesDecrypt(content.getTrk(), encryptPayload);
            content.setResponsePayload(payload);
        });

        content.setResponseMessage(message);
    }

    protected void verify(SignType signType, String signStr, byte[] signData) {
        try {
            byte[] sign = Base64.getDecoder().decode(signStr);
            var ok = this.keyService.verify(signData, signType, sign, this.otherPartyPublicKey);
            if (!ok) {
                throw new MessageVerifyException("报文验证失败");
            }
        } catch (Exception e) {
            if (e instanceof MessageVerifyException) {
                throw (MessageVerifyException) e;
            }
            throw new MessageVerifyException("报文验证异常", e);
        }
    }

    protected String sign(SignType signType, byte[] signDataBytes) {
        try {
            byte[] signBytes = this.keyService.sign(signDataBytes, signType, this.privateKey);
            return Base64.getEncoder().encodeToString(signBytes);
        } catch (Exception e) {
            throw new MessageSignException("报文签名异常", e);
        }
    }

    @SneakyThrows
    protected byte[] sha512(String data) {
        byte[] dataBytes = data.getBytes(charset());
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.update(dataBytes);
        return digest.digest();
    }

}
