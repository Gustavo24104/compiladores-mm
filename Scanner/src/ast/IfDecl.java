package ast;
import visitor.Visitor;

public class IfDecl extends Decl {
    private Exp condicao;
    private BlocoDecl thenDecl;
    private ElseDecl elseDecl;

    public IfDecl(int line, int column, Exp condicao, BlocoDecl thenDecl, ElseDecl elseDecl) {
        super(line, column);
        this.condicao = condicao;
        this.thenDecl = thenDecl;
        this.elseDecl = elseDecl;
    }

    public Exp getCondicao() { return condicao; }
    public BlocoDecl getThenDecl() { return thenDecl; }
    public ElseDecl getElseDecl() { return elseDecl; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitIfDecl(this);
    }
}