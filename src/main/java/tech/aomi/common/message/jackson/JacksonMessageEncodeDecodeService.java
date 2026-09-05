package tech.aomi.common.message.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.extern.slf4j.Slf4j;
import tech.aomi.common.message.MessageEncodeDecodeService;
import tech.aomi.common.message.exception.MessageConvertException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author Sean createAt 2021/7/11
 */
@Slf4j
public class JacksonMessageEncodeDecodeService implements MessageEncodeDecodeService {

    private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder(
                    JsonFactory.builder().enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN).build())
            // 忽略目标对象不存在的key
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // key 进行排序
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            // 按字母顺序排序属性
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            // 小数位数处理
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            // 不包括NULL数据
            .changeDefaultPropertyInclusion(v -> v
                    .withValueInclusion(JsonInclude.Include.NON_NULL)
                    .withContentInclusion(JsonInclude.Include.NON_NULL))
            .build();

    @Override
    public <T> byte[] message2Byte(T payload) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(payload);
        } catch (JacksonException e) {
            LOGGER.error("对象转byte[]异常", e);
            throw new MessageConvertException(e);
        }
    }

    @Override
    public <T> T byte2Message(byte[] payload, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(payload, clazz);
        } catch (JacksonException e) {
            LOGGER.error("json转对象异常,转换的对象是", e);
            throw new MessageConvertException(e);
        }
    }
}
