package com.jlox;
public class RuntimeError extends RuntimeException {
    final Token token;

    RuntimeError (Token _token, String message) {
        super(message);
        this.token = _token;
    }
}
