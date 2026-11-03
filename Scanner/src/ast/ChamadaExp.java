package ast;
import visitor.Visitor;
import java.util.List;

public class ChamadaExp extends Exp {
    private NoIdentificador id;
    private List<Exp> args;

    public ChamadaExp(int line, int column, NoIdentificador id, List<Exp> args) {
        super(line, column);
        this.id = id;
        this.args = args;
    }

    public NoIdentificador getId() { return id; }
    public List<Exp> getArgs() { return args; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitChamadaExp(this);
    }
}