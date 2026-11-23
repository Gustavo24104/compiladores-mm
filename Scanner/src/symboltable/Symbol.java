package symboltable;

import ast.Tipo;
import ast.Decl;
import java.util.List;

public class Symbol {
    private String name;
    private String category;
    private Decl declNode;
    private Tipo type;
    private List<Tipo> paramTypes;
    private int level;
    private int offset;

    public Symbol(String name, Decl declNode, int level, Tipo type) {
        this.name = name;
        this.declNode = declNode;
        this.level = level;
        this.type = type;
    }

    public Symbol(String name, Decl declNode, int level, int offset) {
        this.name = name;
        this.declNode = declNode;
        this.level = level;
        this.offset = offset;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public void setCategory(String cat) {this.category = cat;}
    public Decl getDeclNode() { return declNode; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public List<Tipo> getParamTypes() { return paramTypes; }
    public void setParamTypes(List<Tipo> paramTypes) { this.paramTypes = paramTypes; }
    public boolean isFuncao() { return paramTypes != null; }
    public Tipo getType() { return type; }
    public void setType(Tipo type) { this.type = type; }

    @Override
    public String toString() {
        return "Symbol{name='" + name + "', level=" + level + "}";
    }
}