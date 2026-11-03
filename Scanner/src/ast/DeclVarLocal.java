package ast;
import visitor.Visitor;

public class DeclVarLocal extends Decl {
    private Tipo type;
    private String id;
    private Exp atrib;

    public DeclVarLocal(int line, int column, Tipo type, String id, Exp atrib) {
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
        return visitor.visitDeclVarLocal(this);
    }
}