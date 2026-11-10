package ast;
import visitor.Visitor;

public class ParamNo extends DeclVarLocal {
//    private String name;
//    private Tipo type;

    public ParamNo(int line, int column, String name, Tipo type) {
        super(line, column, type, name, null);
//        this.name = name;
//        this.type = type;
    }

//    public String getName() { return name; }
//    public Tipo getType() { return type; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitParamNo(this);
    }
}