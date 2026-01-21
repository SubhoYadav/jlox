package com.jlox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A new line character is represented as \r\n on windows

public class Lexer {
    private String source;
    private List<Token> tokens = new ArrayList<Token>();
    private int start = 0;
    private int curr = 0;
    private int line = 1;

    // Keywords lookup map
    static private Map<String, TokenType> keywords = new HashMap<>();

    // This is a static initializer block.
    // Code inside a static block is executed only once when the class is loaded into memory by the Java Virtual Machine (JVM).
    // It's used to initialize static members (like keywords in this case) that require more complex setup than a simple one-line assignment.

    static {
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("fun", TokenType.FUN);
        keywords.put("for", TokenType.FOR);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    Lexer (String _source) {
        this.source = _source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            scanToken();
        }

        // Append the EOF token at the end
        tokens.add(new Token(TokenType.EOF, null, line, ""));
        return tokens;
    }

    private void scanToken () {
        char c = advanceLexerPointer();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PARENTHESIS); break; 
            case ')': addToken(TokenType.RIGHT_PARENTHESIS); break; 
            case '{': addToken(TokenType.LEFT_BRACE); break; 
            case '}': addToken(TokenType.RIGHT_BRACE); break; 
            case '+': addToken(TokenType.PLUS); break; 
            case '-': addToken(TokenType.MINUS); break; 
            case '*': addToken(TokenType.STAR); break; 
            case '.': addToken(TokenType.DOT); break; 
            case ',': addToken(TokenType.COMMA); break; 
            case ';': addToken(TokenType.SEMI_COLON); break;
            case '?': addToken(TokenType.QUESTION_MARK); break;
            case ':': addToken(TokenType.COLON); break; 
            case '!': 
                addToken(match('=') ? TokenType.BANG_EQUALS : TokenType.BANG); 
            break; 
            case '>': 
                addToken(match('=') ? TokenType.GREATER_EQUALS : TokenType.GREATER); 
            break; 
            case '<':
                addToken(match('=') ? TokenType.LESSER_EQUALS : TokenType.LESSER); 
            break; 
            case '=': 
               addToken(match('=') ? TokenType.EQUALS_EQUALS : TokenType.EQUALS); 
            break;
            case '/':
                if (match('/')) {
                    // we have encountered a single line comment, so shunt all the characters until we reach a new line
                    while (peek() != '\n' && !isAtEnd()) {
                        advanceLexerPointer();
                    }
                }
                else if (match('*')) {
                    // we have encountered a multi line comment, so shunt all the characters until we reach a new line
                    // The multiline comments can be nested too like: /* /* */ */
                    shuntRecursiveMultiLineComments();
                }
                else {
                    addToken(TokenType.SLASH);
                }
            break;
            case ' ':
            case '\r': // carriage return
            case '\t': // tab character
                // ignore ' ', '\r', '\t'
                break;
            case '\n': // line feed
                line++;
            break;
            case '"':
                createStringLiteralToken();
            break;
            default: 
                if (isDigit(c)) {
                    createNumberLiteralToken();
                }
                else if (isAlpha(c)) {
                   createIdentifierLiteralToken();
                }
                else {
                    Jlox.error(line, "Unexpected character" );
                }
            break;
        }

        start = curr;
    }

    private void shuntRecursiveMultiLineComments () {
        while (peek() != '*' && !isAtEnd()) {
            advanceLexerPointer();
            
            if (peek() == '/' && peekNext() == '*') {
                // a nested multiline comment is encountered so call the shuntRecursiveMultiLineComments() function recursively

                // consume the '/'
                advanceLexerPointer();

                // consume the '*'
                advanceLexerPointer();

                shuntRecursiveMultiLineComments();
            }
        }

        if (peek() == '*' && peekNext() == '/') {
            // consume the '*'
            advanceLexerPointer();

            // consume the '/'
            advanceLexerPointer();

            return; // multiline comment ends
        }

        if (isAtEnd()) {
            Jlox.error(line, "unterminated multiline comment");
            return;
        }
    }

    private void addToken(TokenType tokenType) {
        // Caution! this is not recursion, its a call to an overloaded method
        addToken(tokenType, null);
    }

    private void addToken(TokenType tokenType, Object literal) {
        String lexeme = source.substring(start, curr);
        tokens.add(new Token(tokenType, literal, line, lexeme));
    }

    private void createStringLiteralToken () {
        while(peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++; // this is done to support multiline strings

            advanceLexerPointer();
        }

        if (isAtEnd()) {
            // we reached the end of file without encountering a closing '"';
            Jlox.error(line, "Unterminated string literal");
        }
        
        // Move the the curr pointer to shunt the closing '"'
        advanceLexerPointer();

        // create the value for the string literal by removing the enclosing ""
        String value = source.substring(start + 1, curr - 1);
        addToken(TokenType.STRING, value);
    }

    private void createNumberLiteralToken () {
        while(isDigit(peek())) advanceLexerPointer();

        if (peek() == '.' && isDigit(peekNext())) {
            // consume the '.' character in the number
            advanceLexerPointer();

            // read the characters after '.' only if there are digits after '.'
            // here the lookahead value becomes 2 since we are looking at 2 characters in the source using the peekNext() function
            while (isDigit(peek())) advanceLexerPointer();
        }

        addToken(
            TokenType.NUMBER,
            Double.parseDouble(source.substring(start, curr))
        );
    }

    private void createIdentifierLiteralToken () {
        while (isAlphaNumeric(peek())) advanceLexerPointer();

        String literal = source.substring(start, curr);
        TokenType tokenType = keywords.get(literal);

        if (tokenType == null) {
            // the literal is an identifier/variable name
            tokenType = TokenType.IDENTIFIER;
        }

        addToken(tokenType);
    }

    // helper methods
    private boolean isAlphaNumeric (char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha (char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_'; 
    }

    private boolean isDigit (char c) {
        return c >= '0' && c <= '9';
    }

    private char peek () {
        if (isAtEnd()) return '\0'; // null character
        return source.charAt(curr);
    }

    private char peekNext () {
        if (curr + 1 >= source.length()) return '\0';
        return source.charAt(curr + 1);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(curr) != expected) return false;

        curr++;
        return true;
    }

    private char advanceLexerPointer () {
        return source.charAt(curr++);
    }
    private boolean isAtEnd() {
        return curr >= source.length();
    }
}
