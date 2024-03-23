package tech.aomi.common.message;


import tech.aomi.common.message.entity.MessageContent;
import tech.aomi.common.message.entity.RequestMessage;
import tech.aomi.common.message.entity.ResponseMessage;
import tech.aomi.common.message.entity.SignType;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author Sean createAt 2021/7/9
 */
public interface MessageService {
    /**
     * 创建一个请求ID
     */
    String requestId();

    /**
     * 创建一个随机数
     */
    String randomString();

    /**
     * 创建一个当前时间戳
     */
    String timestamp();

    /**
     * 创建默认的charset
     */
    Charset charset();

    /**
     * 返回起那么类型
     */
    SignType signType();

    /**
     * 获取签名数据
     *
     * @param requestMessage 报文体
     * @return 用于签名的数据
     */
    byte[] getSignData(RequestMessage requestMessage);

    /**
     * 获取签名数据
     *
     * @param responseMessage 报文体
     * @return 用于签名的数据
     */
    byte[] getSignData(ResponseMessage responseMessage);

    MessageContent create(Object payload);

    /**
     * 从map参数中解析
     *
     * @param args 请求参数
     * @return 上下文数据
     */
    MessageContent parse(Map<String, String> args);

    MessageContent parse(byte[] args);

    MessageContent parse(RequestMessage requestMessage);

    void createResponse(MessageContent content, String status, String describe, Object payload);

    void parseResponse(MessageContent content, byte[] args);

    void parseResponse(MessageContent content, ResponseMessage message);

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
