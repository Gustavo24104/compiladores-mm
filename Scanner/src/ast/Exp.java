package ast;

public abstract class Exp extends Decl {
    private Tipo tipo;

    public Exp(int line, int column) {
        super(line, column);
    }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
}
