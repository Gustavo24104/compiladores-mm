package ast;

import visitor.Visitor;
import java.util.List;

public class BlocoDecl extends Decl {
    private List<No> declaracoes;

    public BlocoDecl(int line, int column, List<No> declaracoes) {
        super(line, column);
        this.declaracoes = declaracoes;
    }

    public List<No> getDeclaracoes() { return declaracoes; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBlocoDecl(this);
    }
}