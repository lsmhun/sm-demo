package hu.lsm.smdemo.exception;

public class MarketClosedException extends RuntimeException {

    public MarketClosedException() {
        super();
    }

    public MarketClosedException(String message) {
        super(message);
    }

    public MarketClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarketClosedException(Throwable cause) {
        super(cause);
    }

    protected MarketClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
