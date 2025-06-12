public enum TokenType {
    // single character token
    LEFT_PARENTHESIS, RIGHT_PARENTHESIS, DOT, COMMA, SEMI_COLON,
    LEFT_BRACE, RIGHT_BRACE, 

    // arithmetic operators
    STAR, SLASH, PLUS, MINUS,

    // Literals
    IDENTIFIER, NUMBER, STRING,

    //Operators or 2 character tokens
    BANG, BANG_EQUALS, GREATER, GREATER_EQUALS,
    LESSER, LESSER_EQUALS,

    // Keywords.
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
}


// contains methods: name(), ordinal(), values()
// implicitly extends java.lang.Enum class so extending other classes is not allowed
// can contain beheaviour and fields much like classes in java


