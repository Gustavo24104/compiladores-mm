package ast;
import symboltable.Symbol;
import visitor.Visitor;

public class NoIdentificador extends Exp {
    private Symbol decl;
    private String name;
    private Tipo type;

    public NoIdentificador(int line, int column, String name) {
        super(line, column);
        this.name = name;
        this.type = null;
    }

    public NoIdentificador(int line, int column, String name, Tipo t) {
        super(line, column);
        this.name = name;
        this.type = null;
        type = t;
    }

    public String getName() { return name; }
    public Tipo getType() { return type; }
    public void setType(Tipo type) { this.type = type; }
    public Symbol getDecl () { return decl; }
    public void setDecl(Symbol decl) { this.decl = decl; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitNoIdentificador(this);
    }
}