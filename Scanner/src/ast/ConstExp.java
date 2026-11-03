package ast;
import visitor.Visitor;

public class ConstExp extends Exp {
    private Object value;
    private Tipo type;

    public ConstExp(int line, int column, Object value, Tipo type) {
        super(line, column);
        this.value = value;
        this.type = type;
    }

    public Object getValue() { return value; }
    public Tipo getType() { return type; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitConstExp(this);
    }
}