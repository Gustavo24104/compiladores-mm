package test;

import Scanner.LexScanner;
import ast.BlocoDecl;
import ast.DeclFunc;
import ast.DeclVarLocal;
import ast.NoInicial;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import parserLL1.ParserLL1;
import visitor.SymbolTableVisitor;
import visitor.TypeCheckerVisitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class TypeCheckerVisitorTest {

    private static final Path RAIZ = resolverRaiz();

    private static Path resolverRaiz() {
        Path base = Paths.get("").toAbsolutePath();
        for (; base != null; base = base.getParent()) {
            for (String rel : new String[]{"src/test", "test"}) {
                Path t = base.resolve(rel);
                if (Files.isDirectory(t.resolve("programas/tipos"))) return t;
            }
        }
        throw new IllegalStateException("Diretório test/programas/tipos não encontrado");
    }

    private static String ler(Path p) throws IOException {
        return Files.readString(p).replace("\r\n", "\n").stripTrailing();
    }

    private static String saida(String codigo) {
        NoInicial ast = new ParserLL1(new LexScanner(codigo)).parse$();
        SymbolTableVisitor stv = new SymbolTableVisitor();
        ast.accept(stv);
        TypeCheckerVisitor tcv = new TypeCheckerVisitor();
        ast.accept(tcv);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream antigo = System.out;
        System.setOut(new PrintStream(baos));
        try {
            stv.getErros().forEach(System.out::println);
            tcv.printMensagens();
        } finally {
            System.setOut(antigo);
        }
        return baos.toString().replace("\r\n", "\n").stripTrailing();
    }

    @TestFactory
    Stream<DynamicTest> programasCorrespondemAoEsperado() throws IOException {
        try (Stream<Path> arquivos = Files.list(RAIZ.resolve("programas/tipos"))) {
            List<Path> lista = arquivos.filter(p -> p.toString().endsWith(".txt")).sorted().toList();
            return lista.stream().map(p -> dynamicTest(p.getFileName().toString(), () ->
                    assertEquals(ler(RAIZ.resolve("esperado/tipos").resolve(p.getFileName())),
                            saida(Files.readString(p)))));
        }
    }

    @Test
    void tipoGravadoNasExpressoes() {
        NoInicial ast = new ParserLL1(new LexScanner("void main() {\n  float a = 1 + 2 * 3.0;\n  a;\n}")).parse$();
        ast.accept(new SymbolTableVisitor());
        TypeCheckerVisitor tcv = new TypeCheckerVisitor();
        ast.accept(tcv);
        assertTrue(tcv.getErros().isEmpty());
        DeclFunc f = (DeclFunc) ast.getDecls().get(0);
        DeclVarLocal a = (DeclVarLocal) ((BlocoDecl) f.getCorpo()).getDeclaracoes().get(0);
        assertEquals("float", a.getAtrib().getTipo().getTipo());
    }

    @Test
    void semErroNaoGeraAviso() {
        NoInicial ast = new ParserLL1(new LexScanner("int f(int a) {\n  int b = a;\n  return b;\n}")).parse$();
        ast.accept(new SymbolTableVisitor());
        TypeCheckerVisitor tcv = new TypeCheckerVisitor();
        ast.accept(tcv);
        assertTrue(tcv.getErros().isEmpty());
        assertTrue(tcv.getAvisos().isEmpty());
    }

    @Test
    void entradaIncompletaNaoLancaExcecao() {
        for (String codigo : new String[]{"void main() { int a = ; }", "int f() { return f(; }", "void main() { x = ; y(1; }", ""}) {
            assertDoesNotThrow(() -> {
                NoInicial ast = new ParserLL1(new LexScanner(codigo)).parse$();
                if (ast == null) return;
                ast.accept(new SymbolTableVisitor());
                ast.accept(new TypeCheckerVisitor());
            }, codigo);
        }
    }
}
