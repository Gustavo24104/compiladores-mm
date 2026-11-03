package ast;
import visitor.Visitor;

public class UnaryExp extends Exp {
    private String op;
    private Exp term;

    public UnaryExp(int line, int column, String op, Exp term) {
        super(line, column);
        this.op = op;
        this.term = term;
    }

    public String getOp() { return op; }
    public Exp getTerm() { return term; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitUnaryExp(this);
    }
}