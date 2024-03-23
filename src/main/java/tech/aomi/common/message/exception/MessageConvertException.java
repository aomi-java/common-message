package tech.aomi.common.message.exception;

public class MessageConvertException extends MessageException {
    public MessageConvertException() {
        super();
    }

    public MessageConvertException(String message) {
        super(message);
    }

    public MessageConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageConvertException(Throwable cause) {
        super(cause);
    }

    protected MessageConvertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
