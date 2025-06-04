package org.trade.contants;

public final class Constants {

    private Constants() {
    }

    public static final String REJECTED_TRADE_FLAGGED = "Trade rejected: Trader is flagged as problem trader.";
    public static final String REJECTED_TRADE_REPORTED = "Trade accepted. Trader flagged for suspicious activity and reported to the regulatory authority.";
    public static final String TRADE_ACCEPTED = "Trade accepted. No suspicious activity detected.";
    public static final String MESSAGE = "message";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred. Please try again later.";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String INVALID_FORMAT = "Invalid date_of_birth format. Expected yyyy-MM-dd.";
}
