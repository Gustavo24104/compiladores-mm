import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class LexScannerTest {

    private List<Token> tokenizar(String input) {
        LexScanner scanner = new LexScanner(input);
        List<Token> tokens = new ArrayList<>();
        while (scanner.hasNext()) {
            Token t = scanner.nextToken();
            if (t != null && t.tokenType != TokenType.EOF) {
                tokens.add(t);
            }
        }
        return tokens;
    }

    // identificadores


    @Test
    @DisplayName("Identificador como ID")
    void testId() {
        List<Token> tokens = tokenizar("myVar");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.ID, tokens.get(0).tokenType);
        assertEquals("myVar", tokens.get(0).lexema);
    }

    // literais

    @Test
    @DisplayName("Literal inteiro como INT_LIT")
    void testLiteralInt() {
        List<Token> tokens = tokenizar("42");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.INT_LIT, tokens.get(0).tokenType);
        assertEquals("42", tokens.get(0).lexema);
    }

    @Test
    @DisplayName("Literal float como FLOAT_LIT")
    void testLiteralFloat() {
        List<Token> tokens = tokenizar("3.14");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.FLOAT_LIT, tokens.get(0).tokenType);
        assertEquals("3.14", tokens.get(0).lexema);
    }

    @Test
    @DisplayName("Literal string como STRING_LIT")
    void testLiteralString() {
        List<Token> tokens = tokenizar("\"hello\"");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING_LIT, tokens.get(0).tokenType);
        assertTrue(tokens.get(0).lexema.contains("hello"));
    }


    // operadores

    @Test
    @DisplayName("Operador (+) como PLUS_SYM")
    void testOperadorSimples() {
        List<Token> tokens = tokenizar("+");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.PLUS_SYM, tokens.get(0).tokenType);
        assertEquals("+", tokens.get(0).lexema);
    }

    @Test
    @DisplayName("Operador (>=) como GEQ_SYM")
    void testOperadorCompostoGEQ() {
        List<Token> tokens = tokenizar(">=");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.GEQ_SYM, tokens.get(0).tokenType);
        assertEquals(">=", tokens.get(0).lexema);
    }

    @Test
    @DisplayName("Operador (==) como EQ_SYM")
    void testOperadorIgualdade() {
        List<Token> tokens = tokenizar("==");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.EQ_SYM, tokens.get(0).tokenType);
        assertEquals("==", tokens.get(0).lexema);
    }


    // palavras reservadas

    @Test
    @DisplayName("Palavra reservada 'if' como IF_KEYWORD")
    void testPalavraReservadaIf() {
        List<Token> tokens = tokenizar("if;");
        assertEquals(TokenType.IF_KEYWORD, tokens.get(0).tokenType);
        assertEquals("if", tokens.get(0).lexema);
    }

    @Test
    @DisplayName("Palavra reservada 'while' como WHILE_KEYWORD")
    void testPalavraReservadaWhile() {
        List<Token> tokens = tokenizar("while;");
        assertEquals(TokenType.WHILE_KEYWORD, tokens.get(0).tokenType);
        assertEquals("while", tokens.get(0).lexema);
    }

    @Test
    @DisplayName("Palavra reservada 'return' como RETURN_KEYWORD")
    void testPalavraReservadaReturn() {
        List<Token> tokens = tokenizar("return;");
        assertEquals(TokenType.RETURN_KEYWORD, tokens.get(0).tokenType);
        assertEquals("return", tokens.get(0).lexema);
    }

    // delimitadores

    @Test
    @DisplayName("Delimitador ';' como PONTO_E_VIRGULA")
    void testPontoVirgula() {
        List<Token> tokens = tokenizar(";");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.PONTO_E_VIRGULA, tokens.get(0).tokenType);
    }

    @Test
    @DisplayName("Delimitadores '(' e ')'")
    void testParenteses() {
        List<Token> tokens = tokenizar("(x)");
        assertEquals(3, tokens.size());
        assertEquals(TokenType.ABRE_PARENTESES, tokens.get(0).tokenType);
        assertEquals(TokenType.ID, tokens.get(1).tokenType);
        assertEquals(TokenType.FECHA_PARENTESES, tokens.get(2).tokenType);
    }

    @Test
    @DisplayName("Delimitadores '{' e '}'")
    void testChaves() {
        List<Token> tokens = tokenizar("{x}");
        assertEquals(3, tokens.size());
        assertEquals(TokenType.ABRE_CHAVES, tokens.get(0).tokenType);
        assertEquals(TokenType.FECHA_CHAVES, tokens.get(2).tokenType);
    }

    // erros

    @Test
    @DisplayName("Erro: string não fechada até EOF não gera STRING_LIT")
    void testStringNaoFechadaEOF() {
        List<Token> tokens = tokenizar("\"hello");
        boolean temStringLit = tokens.stream()
                .anyMatch(t -> t.tokenType == TokenType.STRING_LIT);
        assertFalse(temStringLit,
                "String não fechada até EOF nao gera token STRING_LIT");
    }

    @Test
    @DisplayName("Erro/aviso: quebra de linha na string, mas conteudo é preservado")
    void testStringQuebraLinha() {
        List<Token> tokens = tokenizar("\"hello\nworld\"");
        boolean temStringCompleta = tokens.stream()
                .anyMatch(t -> t.tokenType == TokenType.STRING_LIT
                        && t.lexema.contains("hello") && t.lexema.contains("world"));
        assertTrue(temStringCompleta,
                "A string com quebra de linha deve preservar todo o conteúdo lido até então");
    }

    @Test
    @DisplayName("Erro: caractere fora do alfabeto (~) não causa crash")
    void testCaractereForaAlfabeto() {
        LexScanner scanner = new LexScanner("~");
        Token t = scanner.nextToken();
        assertNotNull(t, "Scanner não deve retornar null nem lançar exceção para caractere desconhecido");
    }


    // trecho "realista"

    @Test
    @DisplayName("int x = 42;")
    void testDeclaracaoInteira() {
        List<Token> tokens = tokenizar("int x = 42;");

        assertEquals(5, tokens.size());

        assertEquals(TokenType.INT_KEYWORD, tokens.get(0).tokenType);
        assertEquals("int", tokens.get(0).lexema);

        assertEquals(TokenType.ID, tokens.get(1).tokenType);
        assertEquals("x", tokens.get(1).lexema);

        assertEquals(TokenType.ATRIB_SYM, tokens.get(2).tokenType);
        assertEquals("=", tokens.get(2).lexema);

        assertEquals(TokenType.INT_LIT, tokens.get(3).tokenType);
        assertEquals("42", tokens.get(3).lexema);

        assertEquals(TokenType.PONTO_E_VIRGULA, tokens.get(4).tokenType);
    }

    @Test
    @DisplayName("if (x >= 10) { y = x + 1; }")
    void testExpressaoCompleta() {
        List<Token> tokens = tokenizar("if (x >= 10) { y = x + 1; }");

        assertEquals(14, tokens.size());

        assertEquals(TokenType.IF_KEYWORD,        tokens.get(0).tokenType);
        assertEquals(TokenType.ABRE_PARENTESES,    tokens.get(1).tokenType);
        assertEquals(TokenType.ID,                 tokens.get(2).tokenType);
        assertEquals("x", tokens.get(2).lexema);
        assertEquals(TokenType.GEQ_SYM,            tokens.get(3).tokenType);
        assertEquals(TokenType.INT_LIT,            tokens.get(4).tokenType);
        assertEquals("10", tokens.get(4).lexema);
        assertEquals(TokenType.FECHA_PARENTESES,   tokens.get(5).tokenType);
        assertEquals(TokenType.ABRE_CHAVES,        tokens.get(6).tokenType);
        assertEquals(TokenType.ID,                 tokens.get(7).tokenType);
        assertEquals("y", tokens.get(7).lexema);
        assertEquals(TokenType.ATRIB_SYM,          tokens.get(8).tokenType);
        assertEquals(TokenType.ID,                 tokens.get(9).tokenType);
        assertEquals("x", tokens.get(9).lexema);
        assertEquals(TokenType.PLUS_SYM,           tokens.get(10).tokenType);
        assertEquals(TokenType.INT_LIT,            tokens.get(11).tokenType);
        assertEquals("1", tokens.get(11).lexema);
        assertEquals(TokenType.PONTO_E_VIRGULA,    tokens.get(12).tokenType);
        assertEquals(TokenType.FECHA_CHAVES,       tokens.get(13).tokenType);
    }

    @Test
    @DisplayName("comentário e múltiplas linhas")
    void testMultiplasLinhasComentario() {
        List<Token> tokens = tokenizar("float y = 3.14; @ valor de pi\nint z = 0;");

        assertEquals(10, tokens.size());

        assertEquals(TokenType.FLOAT_KEYWORD,      tokens.get(0).tokenType);
        assertEquals(TokenType.ID,                  tokens.get(1).tokenType);
        assertEquals("y", tokens.get(1).lexema);
        assertEquals(TokenType.ATRIB_SYM,           tokens.get(2).tokenType);
        assertEquals(TokenType.FLOAT_LIT,           tokens.get(3).tokenType);
        assertEquals("3.14", tokens.get(3).lexema);
        assertEquals(TokenType.PONTO_E_VIRGULA,     tokens.get(4).tokenType);

        assertEquals(TokenType.INT_KEYWORD,         tokens.get(5).tokenType);
        assertEquals(TokenType.ID,                  tokens.get(6).tokenType);
        assertEquals("z", tokens.get(6).lexema);
        assertEquals(TokenType.ATRIB_SYM,           tokens.get(7).tokenType);
        assertEquals(TokenType.INT_LIT,             tokens.get(8).tokenType);
        assertEquals("0", tokens.get(8).lexema);
        assertEquals(TokenType.PONTO_E_VIRGULA,     tokens.get(9).tokenType);
    }

    //@Test
    //DisplayName("trecho de código")
    //void testTrechoCodigo() {
    //    List<Token> tokens = tokenizar(" void main() {\n" +
    //        "int a = 13;\n" +
    //        "float b = 3.14;\n" +
    //        "string str = \"hello world!\";\n" +
    //        "print(str);\n" +
    //        "\n" +
    //        "if (a > 17) {\n" +
    //        "    b *= 2;\n" +
    //        "   }\n" +
    //        "}");
    //}
}


