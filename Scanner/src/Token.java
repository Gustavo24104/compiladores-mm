
public class Token {
    public String lexema;
    public int linha, coluna;
    public TokenType tokenType;

    public Token(String lexema, TokenType tokenType, int linha, int coluna){
        this.lexema = lexema;
        this.tokenType = tokenType;
        this.linha = linha;
        this.coluna = coluna;
    }

    public Token(String lexema, TokenType tokenType) {
        linha = -1;
        coluna =-1;
    }

    @Override
    public String toString() {
        return tokenType.name() + "( " + lexema + " ) " + "[ " + linha + ", " + coluna + " ]";
    }
}
