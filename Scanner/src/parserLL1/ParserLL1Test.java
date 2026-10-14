package parserLL1;

import Scanner.LexScanner;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ParserLL1Test {

    @Test
    void testProgramaValidoTiposEAtribuicoes() {
        String codigo = "void main() {\n" +
                        "    int a = 13;\n" +
                        "    float b = 3.14;\n" +
                        "    string str = \"hello world!\";\n" +
                        "    a = 25;\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }

    @Test
    void testProgramaValidoControleFluxo() {
        String codigo = "void processar() {\n" +
                        "    int a = 10;\n" +
                        "    if (a > 5) {\n" +
                        "        a *= 2;\n" +
                        "    } else {\n" +
                        "        a -= 1;\n" +
                        "    }\n" +
                        "    while (a > 0) {\n" +
                        "        a = a - 1;\n" +
                        "    }\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }

    @Test
    void testProgramaValidoFuncoesEProcedimentos() {
        String codigo = "int somar(int x, int y) {\n" +
                        "    return x + y;\n" +
                        "}\n" +
                        "void main() {\n" +
                        "    int resultado = somar(10, 20);\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }

    @Test
    void testProgramaInvalidoPontoEVirgulaAusente() {
        String codigo = "void main() {\n" +
                        "    int a = 10\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }

    @Test
    void testProgramaInvalidoDeclaracaoSemIdentificador() {
        String codigo = "void main() {\n" +
                        "    int = 5;\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }

    @Test
    void testProgramaInvalidoParenteseAusenteCondicao() {
        String codigo = "void main() {\n" +
                        "    if a > 5 {\n" +
                        "        int b = 1;\n" +
                        "    }\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }

    @Test
    void testProgramaComMultiplosErrosModoPanico() {
        String codigo = "void main() {\n" +
                        "    int a = 10\n" +
                        "    int = 5;\n" +
                        "    float b = 3.14\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }
}
