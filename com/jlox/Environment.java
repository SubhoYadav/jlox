package com.jlox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Map<String, Object> values = new HashMap<>();

    public void define(Token name, Object value) {
        values.put(name.lexeme, value);
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'." );
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            this.values.put(name.lexeme, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable " + name.lexeme);
    }
}
