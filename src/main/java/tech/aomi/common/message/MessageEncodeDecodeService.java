package tech.aomi.common.message;

/**
 * 报文转换服务
 */
public interface MessageEncodeDecodeService {
    /**
     * 报文转换为字节
     *
     * @param payload 请求参数
     * @param <T>     请求参数类型
     * @return 报文字节数据
     */
    <T> byte[] message2Byte(T payload);

    /**
     * 字节转换为报文
     *
     * @param payload 报文数据
     * @param <T>     报文类型
     * @return 报文实体
     */
    <T> T byte2Message(byte[] payload, Class<T> clazz);

}
