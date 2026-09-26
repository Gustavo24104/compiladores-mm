public enum TokenType {
    ID,
    INT_LIT,
    FLOAT_LIT,
    STRING_LIT,
    EQ_SYM, //     ==
    PLUS_SYM, //   +
    MINUS_SYM, //  -
    MULT_SYM, //   *
    DIV_SYM, //    /
    ATRIB, //      =

    // palavras reservadas
    IF_KEYWORD,
    ELSE_KEYWORD,
    FOR_KEYWORD,
    WHILE_KEYWORD,
    INT_KEYWORD,
    FLOAT_KEYWORD,
    BOOL_KEYWORD,
    STRING_KEYWORD,
    RETURN_KEYWORD,
    VOID_KEYWORD,

    // Delimitadores
    ABRE_PARENTESES,
    FECHA_PARENTESES,
    ABRE_CHAVES,
    FECHA_CHAVES,
    ABRE_COLCHETE,
    FECHA_COLCHETE,
    PONTO_E_VIRGULA,
    PONTO,
    VIRGULA,
    EOF, // A ideia eh saber qdo acaba o arquivo e ter o q retornar nesss casos
}