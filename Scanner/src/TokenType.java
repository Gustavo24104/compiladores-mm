public enum TokenType {
    // literais
    ID,
    INT_LIT,
    FLOAT_LIT,
    STRING_LIT,

    // operadores
    EQ_SYM, //             ==
    PLUS_SYM, //           +
    MINUS_SYM, //          -
    MULT_SYM, //           *
    DIV_SYM, //            /
    MODULO_SYM, //         %
    ATRIB_SYM, //          =
    GREAT_SYM, //          >
    LESS_SYM, //           <
    GEQ_SYM, //            >=
    LEQ_SYM, //            <=
    DIFF_SYM, //           !=
    PLUS_EQ_SYM, //        +=
    MINUS_EQ_SYM, //       -=
    MULT_EQ_SYM, //        *=
    DIV_EQ_SYM, //         /=
    LOGICAL_OR_SYM, //     ||
    LOGICAL_AND_SYM, //    &&
    LOGICAL_NOT_SYM, //    !
    BITWISE_XOR_SYM, //    ^
    BITWSISE_AND_SYM, //   &
    BITWISE_OR_SYM, //     |


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