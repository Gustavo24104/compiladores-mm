package ast;
import visitor.Visitor;
import java.util.List;

public class DeclFunc extends Decl_primaria {
    private Tipo type;
    private String id;
    private List<ParamNo> params;
    private BlocoDecl corpo;

    public DeclFunc(int line, int column, Tipo type, String id, List<ParamNo> params, BlocoDecl corpo) {
        super(line, column);
        this.type = type;
        this.id = id;
        this.params = params;
        this.corpo = corpo;
    }

    public Tipo getType() { return type; }
    public String getId() { return id; }
    public List<ParamNo> getParams() { return params; }
    public No getCorpo() { return corpo; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDeclFunc(this);
    }
}