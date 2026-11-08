package symboltable;

import ast.Tipo;
import ast.Decl;

public class Symbol {
    private String name;
    private String category;
    private Decl declNode;
    private int level;
    private Tipo type;

    public Symbol(String name, String category, Decl declNode, int level) {
        this.name = name;
        this.category = category;
        this.declNode = declNode;
        this.level = level;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public Decl getDeclNode() { return declNode; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public Tipo getType() { return type; }
    public void setType(Tipo type) { this.type = type; }

    @Override
    public String toString() {
        return "Symbol{name='" + name + "', category='" + category + "', level=" + level + "}";
    }
}