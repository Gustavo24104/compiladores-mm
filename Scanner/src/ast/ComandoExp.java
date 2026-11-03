package ast;
import visitor.Visitor;

public class ComandoExp extends Exp {
    private Exp id;
    private String op;
    private Exp value;

    public ComandoExp(int line, int column, Exp id, Exp value, String op) {
        super(line, column);
        this.id = id;
        this.value = value;
        this.op = op;
    }

    public Exp getId() { return id; }
    public Exp getValue() { return value; }
    public String getOp() {return op; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitComandoExp(this);
    }
}