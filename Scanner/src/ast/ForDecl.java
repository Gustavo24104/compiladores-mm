package ast;
import visitor.Visitor;

public class ForDecl extends Decl {
    private Decl init;
    private Exp condicao;
    private Decl atrib;
    private BlocoDecl corpo;

    public ForDecl(int line, int column, Decl init, Exp condicao, Decl atrib, BlocoDecl corpo) {
        super(line, column);
        this.init = init;
        this.condicao = condicao;
        this.atrib = atrib;
        this.corpo = corpo;
    }

    public Decl getInit() { return init; }
    public Exp getCondicao() { return condicao; }
    public Decl getAtrib() { return atrib; }
    public No getCorpo() { return corpo; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitForDecl(this);
    }
}