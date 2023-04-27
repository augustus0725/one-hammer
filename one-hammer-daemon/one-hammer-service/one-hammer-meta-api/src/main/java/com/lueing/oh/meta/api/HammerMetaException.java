package com.lueing.oh.meta.api;

public class HammerMetaException extends Exception {
    public HammerMetaException(String message) {
        super(message);
    }

    public HammerMetaException(Exception e) {
        super(e);
    }
}
