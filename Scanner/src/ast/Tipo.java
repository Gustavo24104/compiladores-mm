package ast;
import visitor.Visitor;

public class Tipo extends No {
    private String tipo;

    public Tipo(int line, int column, String tipo) {
        super(line, column);
        this.tipo = tipo;
    }

    public String getTipo() { return tipo; }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitTipo(this);
    }
}