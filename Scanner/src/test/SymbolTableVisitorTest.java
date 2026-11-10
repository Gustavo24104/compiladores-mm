package test;

import Scanner.LexScanner;
import ast.NoInicial;
import org.junit.jupiter.api.Test;
import parserLL1.ParserLL1;
import visitor.SymbolTableVisitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SymbolTableVisitorTest {

    private static final Path SYMBOL_DIR = resolveSymbolDir();

    private static Path resolveSymbolDir() {
        for (String c : new String[]{"src/test/esperado/symbolTable", "test/esperado/symbolTable"}) {
            Path p = Paths.get(c);
            if (Files.isDirectory(p)) return p;
        }
        try {
            Path base = Paths.get(
                SymbolTableVisitorTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            );
            for (int i = 1; i <= 6; i++) {
                for (String rel : new String[]{"src/test/esperado/symbolTable", "test/esperado/symbolTable"}) {
                    Path t = base.resolve(rel).normalize();
                    if (Files.isDirectory(t)) return t;
                }
                base = base.getParent();
                if (base == null) break;
            }
        } catch (Exception ignored) {}
        throw new RuntimeException("Diretório 'symbolTable' não encontrado.");
    }

    private String lerEsperado(String arquivo) throws IOException {
        return Files.readString(SYMBOL_DIR.resolve(arquivo)).stripTrailing();
    }

    /* Faz parse + visita e retorna o visitor pronto. */
    private SymbolTableVisitor analisar(String codigo) {
        NoInicial ast = new ParserLL1(new LexScanner(codigo)).parse$();
        SymbolTableVisitor stv = new SymbolTableVisitor();
        ast.accept(stv);
        return stv;
    }

    /* Captura o que printErrors() imprime em System.out. */
    private String capturarErros(SymbolTableVisitor stv) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream antigo = System.out;
        System.setOut(new PrintStream(baos));
        try {
            stv.printErrors();
        } finally {
            System.setOut(antigo);
        }
        return baos.toString().stripTrailing();
    }

    private List<String> erros(SymbolTableVisitor stv) {
        return stv.getErros();
    }

    private static final String PROG_SOBREAMENTO =
        "@Programa com sobreamento\n" +
        "void main() {\n" +
        "  int i = 10;\n" +
        "  int x = 2;\n" +
        "  while(i > 0) {\n" +
        "    double x = 0.5;\n" +
        "    i *= x;\n" +
        "  }\n" +
        "  i *= x;\n" +
        "}\n";

    private static final String PROG_REDECLARACAO =
        "@Erro de redeclaracao\n" +
        "int funcaoErrada(int a, int b) {\n" +
        "  int c = a * b;\n" +
        "  int a = 14;\n" +
        "  return c * a;\n" +
        "}\n";

    private static final String PROG_NAO_DECLARADO =
        "@Declarar antes de usar\n" +
        "void main() {\n" +
        "  x = 2;\n" +
        "  int x = 0;\n" +
        "  x += 1;\n" +
        "}\n";

    @Test
    void validoComSobreamento_semErros() throws IOException {
        SymbolTableVisitor stv = analisar(PROG_SOBREAMENTO);
        assertEquals(0, erros(stv).size(), "Programa com sombreamento não deve gerar erros");
        assertEquals(lerEsperado("validoComSobreamento.txt"), capturarErros(stv));
    }

    @Test
    void redeclaracao_umErro() throws IOException {
        SymbolTableVisitor stv = analisar(PROG_REDECLARACAO);
        assertEquals(1, erros(stv).size(), "Deve reportar exatamente 1 erro de redeclaração");
        assertEquals(lerEsperado("redeclaracao.txt"), capturarErros(stv));
    }

    @Test
    void naoDeclarado_umErro() throws IOException {
        SymbolTableVisitor stv = analisar(PROG_NAO_DECLARADO);
        assertEquals(1, erros(stv).size(), "Deve reportar exatamente 1 erro de nome não declarado");
        assertEquals(lerEsperado("naoDeclarado.txt"), capturarErros(stv));
    }

    private static final String PROG_TRES_ERROS =
        "@Programa com 3 erros\n" +
        "void main() {\n" +
        "  int a = 12;\n" +
        "  string b = \"msg\";\n" +
        "  float a = 14;\n" +
        "  a = double(a);\n" +
        "  string b = \"nova msg\";\n" +
        "}\n";

    private static final String PROG_RECURSIVO =
        "@Programa com procedimento recursivo\n" +
        "int fat(int n) {\n" +
        "  return n * fat(n - 1);\n" +
        "}\n";

    private static final String PROG_USO_APOS_BLOCO =
        "@Programa com uso apos fechamento de bloco\n" +
        "void main() {\n" +
        "  int a = 0;\n" +
        "  for (int i = 0; i < 10; i += 1;) {\n" +
        "    a += i * 2;\n" +
        "  }\n" +
        "  a -= i;\n" +
        "}\n";

    private static final String PROG_MUTUAMENTE_RECURSIVO =
        "@funcoes mutuamente recursivas\n" +
        "void f1(int x) {\n" +
        "  if(x < 5) {\n" +
        "    return;\n" +
        "  }\n" +
        "  f2(x - 2);\n" +
        "}\n" +
        "\n" +
        "void f2(int x) {\n" +
        "  if(x < 0) {\n" +
        "    return;\n" +
        "  }\n" +
        "  f1(x + 1);\n" +
        "}\n";

    private static final String PROG_ESCOPO_COMPARTILHADO =
        "@Funcoes e variaveis compartilham escopo\n" +
        "int a = 12;\n" +
        "float a(int b) {\n" +
        "  return b * 0.5;\n" +
        "}\n";

    @Test
    void tresErros_emOrdemDoTexto() throws IOException {
        SymbolTableVisitor stv = analisar(PROG_TRES_ERROS);
        assertEquals(3, erros(stv).size(), "Deve reportar exatamente 3 erros");
        String saida = capturarErros(stv);
        String esperado = lerEsperado("tresErros.txt");
        assertEquals(esperado, saida, "Os 3 erros devem aparecer na ordem do texto");
    }

    @Test
    void tresErros_primeiroEhRedeclaracaoDeA() {
        SymbolTableVisitor stv = analisar(PROG_TRES_ERROS);
        assertTrue(erros(stv).get(0).contains("a"),
                "Primeiro erro deve envolver 'a' (redeclaração)");
        assertTrue(erros(stv).get(0).contains("já foi declarado"),
                "Primeiro erro deve ser de redeclaração");
    }

    @Test
    void tresErros_segundoEhNaoDeclarado() {
        SymbolTableVisitor stv = analisar(PROG_TRES_ERROS);
        assertTrue(erros(stv).get(1).contains("double"),
                "Segundo erro deve envolver 'double' (não declarado)");
        assertTrue(erros(stv).get(1).contains("não foi declarado"),
                "Segundo erro deve ser de identificador não declarado");
    }

    @Test
    void tresErros_terceiroEhRedeclaracaoDeB() {
        SymbolTableVisitor stv = analisar(PROG_TRES_ERROS);
        assertTrue(erros(stv).get(2).contains("b"),
                "Terceiro erro deve envolver 'b' (redeclaração)");
        assertTrue(erros(stv).get(2).contains("já foi declarado"),
                "Terceiro erro deve ser de redeclaração");
    }

    @Test
    void recursivo_semErros() throws IOException {
        SymbolTableVisitor stv = analisar(PROG_RECURSIVO);
        assertEquals(0, erros(stv).size(), "Procedimento recursivo não deve gerar erros");
        assertEquals(lerEsperado("recursivo.txt"), capturarErros(stv));
    }

    @Test
    void usoAposBloco_erroDeNaoDeclarado() throws IOException {
        SymbolTableVisitor stv = analisar(PROG_USO_APOS_BLOCO);
        assertEquals(1, erros(stv).size(), "Uso de 'i' após fechamento do for deve gerar 1 erro");
        assertTrue(erros(stv).get(0).contains("i"), "O erro deve envolver o identificador 'i'");
        assertEquals(lerEsperado("usoAposBloco.txt"), capturarErros(stv));
    }

    @Test
    void mutuamenteRecursivo_semErros() throws IOException {
        SymbolTableVisitor stv = analisar(PROG_MUTUAMENTE_RECURSIVO);
        assertEquals(0, erros(stv).size(),
                "Funções mutuamente recursivas não devem gerar erros (passagem dupla)");
        assertEquals(lerEsperado("mutuamenteRecursivo.txt"), capturarErros(stv));
    }

    @Test
    void escopoCompartilhado_redeclaracaoEntreFuncaoEVariavel() throws IOException {
        SymbolTableVisitor stv = analisar(PROG_ESCOPO_COMPARTILHADO);
        assertEquals(1, erros(stv).size(),
                "Funções e variáveis compartilham escopo global — deve gerar 1 erro de redeclaração");
        assertTrue(erros(stv).get(0).contains("a"), "O erro deve envolver o identificador 'a'");
        assertEquals(lerEsperado("escopoCompartilhado.txt"), capturarErros(stv));
    }
}
