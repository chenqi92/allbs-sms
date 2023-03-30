package cn.allbs.sms.exception;

/**
 * 类 SmsException
 * </p>
 *
 * @author ChenQi
 * @since 2022/11/4 17:54
 */
public class SmsException extends RuntimeException {

    /**
     * Instantiates a new SmsException.
     *
     * @param message the message
     */
    public SmsException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new SmsException.
     *
     * @param cause the cause
     */
    public SmsException(final Throwable cause) {
        super(cause);
    }
}
