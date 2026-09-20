public class Scanner {
    enum States {
        inicial,
        whitespace,
        identificador,
        palavraReservada,
        string,
        operador,
        literal
    }

    public Scanner() {

    }

    States estadoAtual;
    String lexema;
    char nextChar;


    static void main() {
        Scanner sc = new Scanner();
    }
}
