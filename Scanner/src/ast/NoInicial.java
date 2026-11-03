package ast;
import visitor.Visitor;
import java.util.List;

public class NoInicial extends No {
    private List<Decl_primaria> decls;

    public NoInicial(int line, int column, List<Decl_primaria> decls) {
        super(line, column);
        this.decls = decls;
    }

    public List<Decl_primaria> getDecls() { return decls; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitNoInicial(this);
    }
}