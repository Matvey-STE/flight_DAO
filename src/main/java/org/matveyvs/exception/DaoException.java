package org.matveyvs.exception;

public class DaoException extends RuntimeException {
    public DaoException(Throwable e) {
        super(e);
    }
}
