package ast;

import visitor.Visitor;

public abstract class No {
    private int line;
    private int column;

    public No(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() { return line; }
    public int getColumn() { return column; }

    public abstract <T> T accept(Visitor<T> visitor);
}
