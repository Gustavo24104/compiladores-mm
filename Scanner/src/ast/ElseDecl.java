package ast;
import visitor.Visitor;

public class ElseDecl extends Decl {
    private Exp condicao;
    private BlocoDecl corpo;
    private ElseDecl elseDecl; // Nomeado como elseDecl pois 'else' é palavra reservada em Java

    public ElseDecl(int line, int column, Exp condicao, BlocoDecl corpo, ElseDecl elseDecl) {
        super(line, column);
        this.condicao = condicao;
        this.corpo = corpo;
        this.elseDecl = elseDecl;
    }

    public Exp getCondicao() {
        return condicao;
    }

    public BlocoDecl getCorpo() {
        return corpo;
    }

    public ElseDecl getElseDecl() {
        return elseDecl;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitElseDecl(this); // Double Dispatch para o ElseDecl
    }
}