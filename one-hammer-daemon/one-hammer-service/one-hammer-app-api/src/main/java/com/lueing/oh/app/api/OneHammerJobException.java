package com.lueing.oh.app.api;

public class OneHammerJobException extends Exception {
    public OneHammerJobException(String message) {
        super(message);
    }

    public OneHammerJobException(Exception e) {
        super(e);
    }
}
