public class Token {
    final TokenType tokenType;
    final Object literal;
    final int line;
    final String lexeme;

    Token (TokenType _tokenType, Object _literal, int _line, String _lexeme) {
        this.tokenType = _tokenType;
        this.literal = _literal;
        this.line = _line;
        this.lexeme = _lexeme;
    }

    public String toString () {
        return "Token Type: " + tokenType + " and " + "Lexeme: " + lexeme + " and " + "Literal: " + literal + " and " + "Line: " + line;
    }
}
