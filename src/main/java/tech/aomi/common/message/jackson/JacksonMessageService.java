package tech.aomi.common.message.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import tech.aomi.common.message.AbstractMessageService;
import tech.aomi.common.message.KeyService;
import tech.aomi.common.message.entity.SignType;
import tech.aomi.common.message.exception.MessageConvertException;

import java.io.IOException;

/**
 * @author Sean createAt 2021/7/11
 */
@Slf4j
public class JacksonMessageService extends AbstractMessageService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER
                // 忽略目标对象不存在的key
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                // key 进行排序
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                // 按字母顺序排序属性
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                // 防止金额使用科学计数法
                .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
                // 小数位数处理
                .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
                // 不包括NULL数据
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        ;
    }

    public JacksonMessageService(String base64PublicKey, String base64PrivateKey, String base64OtherPartyPublicKey, String clientId, String timestampFormat, String charset, SignType signType) {
        super(base64PublicKey, base64PrivateKey, base64OtherPartyPublicKey, clientId, timestampFormat, charset, signType);
    }

    public JacksonMessageService(String base64PublicKey, String base64PrivateKey, String base64OtherPartyPublicKey, String clientId, String timestampFormat, String charset, SignType signType, KeyService keyService) {
        super(base64PublicKey, base64PrivateKey, base64OtherPartyPublicKey, clientId, timestampFormat, charset, signType, keyService);
    }


    @Override
    public <T> byte[] message2Byte(T payload) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(payload);
        } catch (JsonProcessingException e) {
            LOGGER.error("对象转byte[]异常", e);
            throw new MessageConvertException(e);
        }
    }

    @Override
    public <T> T byte2Message(byte[] payload, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(payload, clazz);
        } catch (IOException e) {
            LOGGER.error("json转对象异常,转换的对象是", e);
            throw new MessageConvertException(e);
        }
    }
}
