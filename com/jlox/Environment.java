package com.jlox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Map<String, Object> values = new HashMap<>();
    private Environment enclosing;

    Environment () {
        this.enclosing = null;
    }

    Environment (Environment _enclosing) {
        this.enclosing = _enclosing;
    }


    public void define(Token name, Object value) {
        values.put(name.lexeme, value);
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        else if (this.enclosing != null) {
            return this.enclosing.get(name);
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'." );
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            this.values.put(name.lexeme, value);
            return;
        }
        else if (enclosing != null) {
            this.enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable " + name.lexeme);
    }
}
