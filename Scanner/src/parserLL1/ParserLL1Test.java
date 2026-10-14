package parserLL1;

import Scanner.LexScanner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ParserLL1Test {

    @Test
    public void testProgramaValidoTiposVariaveis() {
        String codigo = "int x = 10; float y = 5.5; string s = \"compiladores\"; bool ativo = true;";
        LexScanner scanner = new LexScanner(codigo);
        ParserLL1 parser = new ParserLL1(scanner);
        assertDoesNotThrow(() -> parser.parse$());
    }

    @Test
    public void testProgramaValidoControleFluxo() {
        String codigo = "void main() {" +
                "    int i = 0;" +
                "    while (i < 10) {" +
                "        if (i == 5) {" +
                "            i += 1;" +
                "        } else {" +
                "            i = i + 1;" +
                "        }" +
                "    }" +
                "    for (int j = 0; j < 5; j = j + 1;) {" +
                "        i = i * 2;" +
                "    }" +
                "}";
        LexScanner scanner = new LexScanner(codigo);
        ParserLL1 parser = new ParserLL1(scanner);
        assertDoesNotThrow(() -> parser.parse$());
    }

    @Test
    public void testProgramaValidoProcedimentosFuncoes() {
        String codigo = "int soma(int a, int b) {" +
                "    return a + b;" +
                "}" +
                "void main() {" +
                "    int resultado = soma(15, 25);" +
                "}";
        LexScanner scanner = new LexScanner(codigo);
        ParserLL1 parser = new ParserLL1(scanner);
        assertDoesNotThrow(() -> parser.parse$());
    }

    @Test
    public void testProgramaInvalidoPontoVirgulaFaltando() {
        String codigo = "int x = 10";
        LexScanner scanner = new LexScanner(codigo);
        ParserLL1 parser = new ParserLL1(scanner);
        assertDoesNotThrow(() -> parser.parse$());
    }

    @Test
    public void testProgramaInvalidoParenteseCondicional() {
        String codigo = "void main() { if (x > 5 { x = 0; } }";
        LexScanner scanner = new LexScanner(codigo);
        ParserLL1 parser = new ParserLL1(scanner);
        assertDoesNotThrow(() -> parser.parse$());
    }

    @Test
    public void testProgramaInvalidoAtribuicaoMalformada() {
        String codigo = "int x = + ;";
        LexScanner scanner = new LexScanner(codigo);
        ParserLL1 parser = new ParserLL1(scanner);
        assertDoesNotThrow(() -> parser.parse$());
    }

    @Test
    public void testRecuperacaoModoPanicoMultiplosErros() {
        String codigo = "int a = + ;" +
                "void func(int { return 0; }" +
                "float b = 10.5";
        LexScanner scanner = new LexScanner(codigo);
        ParserLL1 parser = new ParserLL1(scanner);
        assertDoesNotThrow(() -> parser.parse$());
    }
}
