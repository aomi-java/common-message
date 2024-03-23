package tech.aomi.common.message.exception;

public class MessageEncryptException extends MessageException {
    public MessageEncryptException() {
        super();
    }

    public MessageEncryptException(String message) {
        super(message);
    }

    public MessageEncryptException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageEncryptException(Throwable cause) {
        super(cause);
    }

    protected MessageEncryptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
