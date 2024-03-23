package tech.aomi.common.message.exception;

public class MessageSignException extends MessageException {
    public MessageSignException() {
        super();
    }

    public MessageSignException(String message) {
        super(message);
    }

    public MessageSignException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageSignException(Throwable cause) {
        super(cause);
    }

    protected MessageSignException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
