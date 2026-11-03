package ast;
import visitor.Visitor;

public class AtribExp extends Exp {
    private Exp id;
    private Exp value;
    private String op = "";

    public AtribExp(int line, int column, Exp id, Exp value, String op) {
        super(line, column);
        this.id = id;
        this.value = value;
        this.op = op;
    }

    public Exp getId() { return id; }
    public Exp getValue() { return value; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitAtribExp(this);
    }
}