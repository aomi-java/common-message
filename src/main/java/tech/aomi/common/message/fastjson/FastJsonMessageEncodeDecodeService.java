package tech.aomi.common.message.fastjson;

import com.alibaba.fastjson2.JSON;
import tech.aomi.common.message.MessageEncodeDecodeService;

/**
 * @author Sean createAt 2021/7/12
 */
public class FastJsonMessageEncodeDecodeService implements MessageEncodeDecodeService {
    @Override
    public <T> byte[] message2Byte(T payload) {
        return JSON.toJSONBytes(payload);
    }

    @Override
    public <T> T byte2Message(byte[] payload, Class<T> clazz) {
        return JSON.parseObject(payload, clazz);
    }
}
