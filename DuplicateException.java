package com.arman.mapapp;

@SuppressWarnings("serial")
class DuplicateException extends Exception {

    public DuplicateException() {
        super();
    }

    public DuplicateException(String message) {
        super(message);
    }
}