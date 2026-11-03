package ast;
import visitor.Visitor;

public class AtribExp extends Exp {
    private NoIdentificador id;
    private Exp value;

    public AtribExp(int line, int column, NoIdentificador id, Exp value) {
        super(line, column);
        this.id = id;
        this.value = value;
    }

    public NoIdentificador getId() { return id; }
    public Exp getValue() { return value; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitAtribExp(this);
    }
}