package tech.aomi.common.message.fastjson;

import com.alibaba.fastjson2.JSON;
import tech.aomi.common.message.AbstractMessageService;
import tech.aomi.common.message.KeyService;
import tech.aomi.common.message.entity.SignType;

/**
 * @author Sean createAt 2021/7/12
 */
public class FastJsonMessageService extends AbstractMessageService {
    public FastJsonMessageService(String base64PublicKey, String base64PrivateKey, String base64OtherPartyPublicKey, String clientId, String timestampFormat, String charset, SignType signType) {
        super(base64PublicKey, base64PrivateKey, base64OtherPartyPublicKey, clientId, timestampFormat, charset, signType);
    }

    public FastJsonMessageService(String base64PublicKey, String base64PrivateKey, String base64OtherPartyPublicKey, String clientId, String timestampFormat, String charset, SignType signType, KeyService keyService) {
        super(base64PublicKey, base64PrivateKey, base64OtherPartyPublicKey, clientId, timestampFormat, charset, signType, keyService);
    }

    @Override
    public <T> byte[] message2Byte(T payload) {
        return JSON.toJSONBytes(payload);
    }

    @Override
    public <T> T byte2Message(byte[] payload, Class<T> clazz) {
        return JSON.parseObject(payload, clazz);
    }
}
