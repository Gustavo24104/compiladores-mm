package test;
import Scanner.LexScanner;
import ast.NoInicial;
import parserLL1.ParserLL1;
import visitor.SymbolTableVisitor;


public class SymbolTableVisitorTest {
    private SymbolTableVisitor stv = new SymbolTableVisitor();

    private NoInicial parse(String codigo) {
        NoInicial ast = new ParserLL1(new LexScanner(codigo)).parse$();
        ast.accept(stv);
        return ast;
    }


}
