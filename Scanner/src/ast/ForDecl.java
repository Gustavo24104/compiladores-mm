package ast;
import visitor.Visitor;

public class ForDecl extends Decl {
    private Decl init;
    private Exp condicao;
    private Exp atrib;
    private No corpo;

    public ForDecl(int line, int column, Decl init, Exp condicao, Exp atrib, No corpo) {
        super(line, column);
        this.init = init;
        this.condicao = condicao;
        this.atrib = atrib;
        this.corpo = corpo;
    }

    public Decl getInit() { return init; }
    public Exp getCondicao() { return condicao; }
    public Exp getAtrib() { return atrib; }
    public No getCorpo() { return corpo; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitForDecl(this);
    }
}