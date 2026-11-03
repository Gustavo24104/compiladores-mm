package ast;
import visitor.Visitor;

public class BinExp extends Exp {
    private Exp left;
    private Exp right;
    private String op;

    public BinExp(int line, int column, Exp left, Exp right, String op) {
        super(line, column);
        this.left = left;
        this.right = right;
        this.op = op;
    }

    public Exp getLeft() { return left; }
    public Exp getRight() { return right; }
    public String getOp() { return op; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBinExp(this);
    }
}