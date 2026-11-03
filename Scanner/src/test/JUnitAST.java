package test;

import Scanner.LexScanner;
import ast.NoInicial;
import org.junit.jupiter.api.Test;
import parserLL1.ParserLL1;
import visitor.SourcePrinterVisitor;
import visitor.TreePrinterVisitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class JUnitAST {

    private static final Path ESPERADO_DIR =
            Paths.get("src", "test", "esperado");

    private String lerEsperado(String nomeArquivo) throws IOException {
        Path p = ESPERADO_DIR.resolve(nomeArquivo);
        return normalizarLinhas(Files.readString(p));
    }

    private String normalizarLinhas(String s) {
        String[] linhas = s.split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < linhas.length; i++) {
            String linha = linhas[i].stripTrailing();
            if (i < linhas.length - 1) {
                sb.append(linha).append("\n");
            } else if (!linha.isEmpty()) {
                sb.append(linha);
            }
        }
        return sb.toString();
    }

    private NoInicial parse(String codigo) {
        return new ParserLL1(new LexScanner(codigo)).parse$();
    }

    private String imprimirArvore(NoInicial raiz) {
        TreePrinterVisitor tp = new TreePrinterVisitor();
        raiz.accept(tp);
        return normalizarLinhas(tp.getResult());
    }

    private String imprimirFonte(NoInicial raiz) {
        return normalizarLinhas(raiz.accept(new SourcePrinterVisitor()));
    }

    private String capturarSaida(Runnable r) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream antigo = System.out;
        System.setOut(new PrintStream(baos));
        try {
            r.run();
        } finally {
            System.setOut(antigo);
        }
        return baos.toString();
    }

    private static final String PROG1 =
            "@descobre se um numero eh primo\n" +
            "bool ehPrimo(int x) {\n" +
            "  if (x % 2 == 0) {\n" +
            "    return false;\n" +
            "  }\n" +
            "  for (int i = 3; i < x; i += 2;) {\n" +
            "    if (x % i == 0) {\n" +
            "      return false;\n" +
            "    }\n" +
            "  }\n" +
            "  return true;\n" +
            "}\n" +
            "\n" +
            "@imprimir primos ate 1000\n" +
            "void main() {\n" +
            "  int i = 1;\n" +
            "  while (i <= 1000) {\n" +
            "    if (ehPrimo(i)) {\n" +
            "      print(i);\n" +
            "      i += 1;\n" +
            "    }\n" +
            "  }\n" +
            "}\n";

    private static final String PROG2 =
            "int mdc(int a, int b) {\n" +
            "    if(b == 0) {\n" +
            "      return a;\n" +
            "    } else {\n" +
            "      return mdc(b, a % b);\n" +
            "    }\n" +
            "}\n";

    private static final String PROG3 =
            " void processing() {\n" +
            "  int a;\n" +
            "  int c = 12;\n" +
            "  a = c * 2;\n" +
            "  float _abc = 0.5;\n" +
            "  _abc = a * (1/_abc);\n" +
            "  int d = 1 + 2 + 3;\n" +
            "  if(d == 0) {\n" +
            "    return;\n" +
            "  } else if (a == 2){\n" +
            "    c = 3;\n" +
            "  } else {\n" +
            "    a = 12;\n" +
            "  }\n" +
            "}\n";

    @Test
    void p1_ASTCorrespondeAoEsperado() throws IOException {
        NoInicial raiz = parse(PROG1);
        assertNotNull(raiz, "Parse de p1 não deve retornar null");
        assertEquals(lerEsperado("p1-AST.txt"), imprimirArvore(raiz),
                "A AST de p1 deve corresponder ao arquivo p1-AST.txt");
    }

    @Test
    void p1_FonteReimpressaCorrespondeAoEsperado() throws IOException {
        NoInicial raiz = parse(PROG1);
        assertNotNull(raiz, "Parse de p1 não deve retornar null");
        assertEquals(lerEsperado("p1-fonte.txt"), imprimirFonte(raiz),
                "A fonte reimpressa de p1 deve corresponder ao arquivo p1-fonte.txt");
    }

    @Test
    void p2_ASTCorrespondeAoEsperado() throws IOException {
        NoInicial raiz = parse(PROG2);
        assertNotNull(raiz, "Parse de p2 não deve retornar null");
        assertEquals(lerEsperado("p2-AST.txt"), imprimirArvore(raiz),
                "A AST de p2 deve corresponder ao arquivo p2-AST.txt");
    }

    @Test
    void p2_FonteReimpressaCorrespondeAoEsperado() throws IOException {
        NoInicial raiz = parse(PROG2);
        assertNotNull(raiz, "Parse de p2 não deve retornar null");
        assertEquals(lerEsperado("p2-fonte.txt"), imprimirFonte(raiz),
                "A fonte reimpressa de p2 deve corresponder ao arquivo p2-fonte.txt");
    }

    @Test
    void p3_ASTCorrespondeAoEsperado() throws IOException {
        NoInicial raiz = parse(PROG3);
        assertNotNull(raiz, "Parse de p3 não deve retornar null");
        assertEquals(lerEsperado("p3-AST.txt"), imprimirArvore(raiz),
                "A AST de p3 deve corresponder ao arquivo p3-AST.txt");
    }

    @Test
    void p3_FonteReimpressaCorrespondeAoEsperado() throws IOException {
        NoInicial raiz = parse(PROG3);
        assertNotNull(raiz, "Parse de p3 não deve retornar null");
        assertEquals(lerEsperado("p3-fonte.txt"), imprimirFonte(raiz),
                "A fonte reimpressa de p3 deve corresponder ao arquivo p3-fonte.txt");
    }

    private String arvoreDeExpressao(String expr) {
        NoInicial raiz = parse("void _t() { int _r = " + expr + "; }");
        assertNotNull(raiz);
        return imprimirArvore(raiz);
    }

    @Test
    void precedencia_SomaVsMultiplicacao() {
        String arvore = arvoreDeExpressao("a + b * c");
        int posPlus = arvore.indexOf("BinExp(op=+)");
        int posMult = arvore.indexOf("BinExp(op=*)");
        assertTrue(posPlus >= 0, "Deve conter BinExp(op=+)");
        assertTrue(posMult >= 0, "Deve conter BinExp(op=*)");
        assertTrue(posPlus < posMult,
                "Na pre-ordem, BinExp(+) deve preceder BinExp(*) em 'a + b * c'");
    }

    @Test
    void associatividade_SubtracaoEsquerdaParaDireita() {
        String aMenosBmenosC   = arvoreDeExpressao("a - b - c");
        String aMenosBMenosC   = arvoreDeExpressao("a - (b - c)");
        assertNotEquals(aMenosBmenosC, aMenosBMenosC,
                "a-b-c e a-(b-c) devem produzir ASTs distintas");
        long qtdMinus = aMenosBmenosC.lines()
                .filter(l -> l.contains("BinExp(op=-)")).count();
        assertEquals(2, qtdMinus,
                "a-b-c deve conter exatamente dois nós BinExp(op=-)");
    }

    @Test
    void precedencia_TresArvoresSaoDiferentes() {
        String a1 = arvoreDeExpressao("a + b * c");
        String a2 = arvoreDeExpressao("a - b - c");
        String a3 = arvoreDeExpressao("a - (b - c)");
        assertNotEquals(a1, a2, "a+b*c e a-b-c devem ter ASTs distintas");
        assertNotEquals(a1, a3, "a+b*c e a-(b-c) devem ter ASTs distintas");
        assertNotEquals(a2, a3, "a-b-c e a-(b-c) devem ter ASTs distintas");
    }

    @Test
    void parentesesRedundantesNaoGeramNo() {
        String semParen  = arvoreDeExpressao("a");
        String umParen   = arvoreDeExpressao("(a)");
        String doisParen = arvoreDeExpressao("((a))");
        assertEquals(semParen, umParen,
                "(a) deve produzir a mesma AST que a");
        assertEquals(semParen, doisParen,
                "((a)) deve produzir a mesma AST que a");
        assertFalse(doisParen.toLowerCase().contains("paren"),
                "A AST não deve conter nenhum nó de parênteses");
    }

    private void assertRoundTrip(String codigoOriginal) {
        NoInicial raiz1 = parse(codigoOriginal);
        assertNotNull(raiz1, "Primeiro parse não deve retornar null");

        String fonte1 = imprimirFonte(raiz1);
        assertFalse(fonte1.isBlank(), "Fonte reimpressa não deve estar em branco");

        NoInicial raiz2 = parse(fonte1);
        assertNotNull(raiz2, "Segundo parse (round-trip) não deve retornar null");

        String fonte2 = imprimirFonte(raiz2);
        assertEquals(fonte1, fonte2,
                "O segundo texto reimpresso deve ser idêntico ao primeiro");
        assertEquals(imprimirArvore(raiz1), imprimirArvore(raiz2),
                "As duas ASTs do round-trip devem ser idênticas");
    }

    @Test
    void roundTrip_p1() {
        assertRoundTrip(PROG1);
    }

    @Test
    void roundTrip_p2() {
        assertRoundTrip(PROG2);
    }

    @Test
    void roundTrip_p3() {
        assertRoundTrip(PROG3);
    }

    private void assertInvalidoSemExcecao(String codigo) {
        String[] saida = {""};
        assertDoesNotThrow(() -> saida[0] = capturarSaida(() -> parse(codigo)),
                "O parser não deve propagar exceção");
        assertTrue(saida[0].contains("Erro"),
                "O parser deve reportar erro para entrada inválida");
    }

    @Test
    void regressao_PontoEVirgulaAusente() {
        assertInvalidoSemExcecao("void main() {\n    int a = 10\n}");
    }

    @Test
    void regressao_DeclaracaoSemIdentificador() {
        assertInvalidoSemExcecao("void main() {\n    int = 5;\n}");
    }

    @Test
    void regressao_ParenteseNaoFechado() {
        assertInvalidoSemExcecao("void main() {\n    int a = (5 + 3;\n}");
    }

    @Test
    void regressao_ChavesNaoFechadas() {
        assertInvalidoSemExcecao("void main() {\n    int a = 1;\n");
    }

    @Test
    void regressao_MultiplosErrosNasMesmaRodada() {
        String saida = capturarSaida(() ->
            assertDoesNotThrow(() -> parse(
                "void main() {\n" +
                "    int a = ;\n" +
                "    float b = 3.14;\n" +
                "    int c = (2 + ;\n" +
                "}"
            ))
        );
        long qtdErros = saida.lines().filter(l -> l.startsWith("Erro")).count();
        assertTrue(qtdErros >= 2,
                "Devem ser reportados >= 2 erros em uma rodada; obtidos: " + qtdErros);
    }

    @Test
    void regressao_ExpressaoSemContexto() {
        assertInvalidoSemExcecao("+ + +");
    }

    @Test
    void regressao_RetornoComPalavraReservada() {
        assertInvalidoSemExcecao("void main() {\n    return int;\n}");
    }
}
