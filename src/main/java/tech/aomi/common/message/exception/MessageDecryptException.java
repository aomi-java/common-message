package tech.aomi.common.message.exception;

public class MessageDecryptException extends MessageException{
    public MessageDecryptException() {
        super();
    }

    public MessageDecryptException(String message) {
        super(message);
    }

    public MessageDecryptException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageDecryptException(Throwable cause) {
        super(cause);
    }

    protected MessageDecryptException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
