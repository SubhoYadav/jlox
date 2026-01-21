package com.jlox;
// Using a recursive descent parsing technique
/*
    For using this parsing technique, we need an unambiguous stratified CFG with no production rule being
    left-recursive, in order to stratify a grammar, we use the precedence of operators, each production rule
    matches expressions at its precedence level or higher

    below is a list of operators in increasing order of precedence along with their symbols and associativity
    equality        '==', '!='              left
    comparison      '>', '>=', '<', '<='    left
    term            '+', '-'                left
    factor          '/', '*'                left
    unary           '-', '!'                right
    
    then we have a primary expression that holds the highest precedence, and contains all sorts of literals like NUMBER
    STRING, true, false, nil, grouped expressions etc

    Based on the above rules, here is the lox language's grammar
    program := (statement)*EOF
    statement := exprStmt | printStmt
    exprStmt := expression";"
    printStmt := "print" statement ";"
    expression := equality
    equality := comparison (('!=', '==', '?')comparison)*
    comparison := term (('>', '>=', '<', '<=')term)* | term ':' term
    term := factor (('+', '-')factor)*
    factor := unary (('*', '/')unary)*
    unary := ('!', '-')unary | primary
    primary := NUMBER | STRING | '(' expression ')' | "true" | "false" | "nil"

*/

import java.util.List;

public class Parser {
    Parser (List<Token> _tokens) {
        this.tokens = _tokens;
    }

    List<Token> tokens;
    private static class ParserError extends RuntimeException {}
    int curr = 0;

    /* helper methods */
    private Token peek() {
        return tokens.get(curr);
    }

    private Token previous() {
        return tokens.get(curr - 1);
    }

    private Token advance() {
        if (!isAtEnd()) curr++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().tokenType == TokenType.EOF;
    }

    private boolean check(TokenType type) {
        return peek().tokenType == type;
    }

    private boolean match(TokenType ...types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    public Expr startParsing() {
        try {
            return expression();
        }
        catch (ParserError err) {
            return null;
        }
    }

    private Expr expression () { 
        return ternary();
    }
    
    private Expr ternary () {
        Expr expr = equality();

        while (match(TokenType.QUESTION_MARK)) {
            Expr trueBranch = expression();

            if (!match(TokenType.COLON)) {
                throw error(peek(), "expected a ':'");
            }

            // match() has consumed the ':' token
            Expr falseBranch = expression();
            expr = new Expr.Ternary(expr, trueBranch, falseBranch);
        }

        return expr;
    }

    private Expr equality () {
        Expr expr = comparison();
        while (match(TokenType.EQUALS_EQUALS, TokenType.BANG_EQUALS)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison () {
        Expr expr = term();

        while (match(TokenType.GREATER, TokenType.GREATER_EQUALS, TokenType.LESSER, TokenType.LESSER_EQUALS)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term () {
        Expr expr = factor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor () {
        Expr expr = unary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr expr = unary();
            return new Expr.Unary(operator, expr);
        }

        return primary();
    }

    private Expr primary() {
        if (match(TokenType.NIL)) {
            return new Expr.Literal(null);
        }
        else if (match(TokenType.TRUE)) {
            return new Expr.Literal(true);
        }
        else if (match(TokenType.FALSE)) {
            return new Expr.Literal(false);
        }
        else if (match(TokenType.STRING, TokenType.NUMBER)) {
            return new Expr.Literal(previous().literal);
        }
        else if (match(TokenType.LEFT_PARENTHESIS)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PARENTHESIS, "Expected ')' after an expression.");
            return new Expr.Grouping(expr);
        }

        return null;
    }

    private void consume (TokenType tokenType, String message) {
        if (check(tokenType)) {
            advance();
            return;
        }

        throw error(peek(), message);
    }

    private ParserError error(Token token, String message) {
        Jlox.error(token, message);
        return new ParserError();
    }
}
