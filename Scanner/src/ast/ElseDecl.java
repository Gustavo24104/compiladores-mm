package ast;
import visitor.Visitor;

public class ElseDecl extends Decl {
    private Exp condicao;
    private BlocoDecl corpo;
    private IfDecl ifDecl; // Nomeado como elseDecl pois 'else' é palavra reservada em Java

    public ElseDecl(int line, int column, Exp condicao, BlocoDecl corpo, IfDecl elseDecl) {
        super(line, column);
        this.condicao = condicao;
        this.corpo = corpo;
        this.ifDecl = elseDecl;
    }



    public Exp getCondicao() {
        return condicao;
    }

    public BlocoDecl getCorpo() {
        return corpo;
    }

    public IfDecl getIfDecl() {
        return ifDecl;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitElseDecl(this); // Double Dispatch para o ElseDecl
    }
}