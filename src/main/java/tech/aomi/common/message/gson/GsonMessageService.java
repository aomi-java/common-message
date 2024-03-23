package tech.aomi.common.message.gson;

import com.google.gson.Gson;
import tech.aomi.common.message.AbstractMessageService;
import tech.aomi.common.message.KeyService;
import tech.aomi.common.message.entity.SignType;

/**
 * @author Sean createAt 2021/7/12
 */
public class GsonMessageService extends AbstractMessageService {


    public GsonMessageService(String base64PublicKey, String base64PrivateKey, String base64OtherPartyPublicKey, String clientId, String timestampFormat, String charset, SignType signType) {
        super(base64PublicKey, base64PrivateKey, base64OtherPartyPublicKey, clientId, timestampFormat, charset, signType);
    }

    public GsonMessageService(String base64PublicKey, String base64PrivateKey, String base64OtherPartyPublicKey, String clientId, String timestampFormat, String charset, SignType signType, KeyService keyService) {
        super(base64PublicKey, base64PrivateKey, base64OtherPartyPublicKey, clientId, timestampFormat, charset, signType, keyService);
    }

    @Override
    public <T> byte[] message2Byte(T payload) {
        Gson gson = new Gson();
        return gson.toJson(payload).getBytes(charset());
    }

    @Override
    public <T> T byte2Message(byte[] payload, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(new String(payload, charset()), clazz);
    }
}
