package tech.aomi.common.message.exception;

/**
 * 报文验证异常
 */
public class MessageVerifyException extends MessageException {

    public MessageVerifyException() {
        super();
    }

    public MessageVerifyException(String message) {
        super(message);
    }

    public MessageVerifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageVerifyException(Throwable cause) {
        super(cause);
    }

    protected MessageVerifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
