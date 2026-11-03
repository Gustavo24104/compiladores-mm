package ast;
import visitor.Visitor;

public class ReturnDecl extends Decl {
    private Exp exp;

    public ReturnDecl(int line, int column, Exp exp) {
        super(line, column);
        this.exp = exp;
    }

    public Exp getExp() { return exp; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitReturnDecl(this);
    }
}