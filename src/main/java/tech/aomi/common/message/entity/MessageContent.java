package tech.aomi.common.message.entity;

import lombok.Getter;
import lombok.Setter;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 请求报文上下文
 *
 * @author Sean createAt 2021/6/23
 */
@Getter
@Setter
public class MessageContent {

    public static final String MESSAGE_CONTEXT = "AOMI@MESSAGE_CONTEXT";

    /**
     * 传输秘钥明文
     */
    private byte[] trk;

    /**
     * 请求参数明文
     */
    private byte[] requestPayload;
    private RequestMessage requestMessage;

    private byte[] responsePayload;
    private ResponseMessage responseMessage;

    /**
     * 己方私钥
     */
    private PrivateKey privateKey;
    /**
     * 己方公钥
     */
    private PublicKey publicKey;

    /**
     * 对方公钥
     */
    private PublicKey otherPartyPublicKey;
}
