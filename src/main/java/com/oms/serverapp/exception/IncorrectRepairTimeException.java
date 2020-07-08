package com.oms.serverapp.exception;

public class IncorrectRepairTimeException extends Exception {

    public IncorrectRepairTimeException() {
    }

    public IncorrectRepairTimeException(String message) {
        super(message);
    }

    public IncorrectRepairTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectRepairTimeException(Throwable cause) {
        super(cause);
    }

    public IncorrectRepairTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
