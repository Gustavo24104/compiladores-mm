package parserLL1;

import Scanner.LexScanner;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ParserLL1Test {

    @Test
    void testProgramaValidoTiposEAtribuicoes() {
        String codigo = "void main() {\n" +
                        "    int a = 10;\n" +
                        "    float b = 3.14;\n" +
                        "    bool ativo = true;\n" +
                        "    string texto = \"compiladores\";\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }

    @Test
    void testProgramaValidoControleFluxo() {
        String codigo = "void processar() {\n" +
                        "    int i = 0;\n" +
                        "    while (i < 5) {\n" +
                        "        if (i == 2) {\n" +
                        "            i += 1;\n" +
                        "        } else {\n" +
                        "            i = i + 1;\n" +
                        "        }\n" +
                        "    }\n" +
                        "    for (int j = 0; j < 5; j = j + 1) {\n" +
                        "        int x = j;\n" +
                        "    }\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }

    @Test
    void testProgramaValidoFuncoesEProcedimentos() {
        String codigo = "void executar() {\n" +
                        "    return;\n" +
                        "}\n" +
                        "void main() {\n" +
                        "    executar();\n" +
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
    void testProgramaInvalidoTipoDesconhecido() {
        String codigo = "void main() {\n" +
                        "    tipoInvalido x = 5;\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }

    @Test
    void testProgramaInvalidoAtribuicaoIncompleta() {
        String codigo = "void main() {\n" +
                        "    int a = ;\n" +
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
                        "    tipoInvalido b = 20;\n" +
                        "}";
        assertDoesNotThrow(() -> {
            ParserLL1 parser = new ParserLL1(new LexScanner(codigo));
            parser.parse$();
        });
    }
}
