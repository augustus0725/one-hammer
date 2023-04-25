package com.lueing.oh.app.api;

public class OneHammerException extends Exception {
    public OneHammerException(String message) {
        super(message);
    }

    public OneHammerException(Exception e) {
        super(e);
    }
}
