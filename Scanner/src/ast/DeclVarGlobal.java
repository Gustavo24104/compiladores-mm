package ast;
import visitor.Visitor;

public class DeclVarGlobal extends Decl_primaria {
    private Tipo type;
    private String id;
    private Exp atrib;

    public DeclVarGlobal(int line, int column, Tipo type, String id, Exp atrib) {
        super(line, column);
        this.type = type;
        this.id = id;
        this.atrib = atrib;
    }

    public Tipo getType() { return type; }
    public String getId() { return id; }
    public Exp getAtrib() { return atrib; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitDeclVarGlobal(this);
    }
}