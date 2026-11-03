package ast;
import visitor.Visitor;

public class WhileDecl extends Decl {
    private Exp condicao;
    private No corpo;

    public WhileDecl(int line, int column, Exp condicao, No corpo) {
        super(line, column);
        this.condicao = condicao;
        this.corpo = corpo;
    }

    public Exp getCondicao() { return condicao; }
    public No getCorpo() { return corpo; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitWhileDecl(this);
    }
}