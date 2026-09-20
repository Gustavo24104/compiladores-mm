import java.util.ArrayList;


public class LexScanner {

    String input;
    ArrayList<Token> output;
    States estadoAtual;
    String lexema;
    char nextChar;

    enum States {
        inicial,
        whitespace,
        identificador,
        palavraReservada,
        string,
        operador,
        literal

    }

    public LexScanner(String input) {
        estadoAtual = States.inicial;

    }




    static void main() {
        LexScanner sc = new LexScanner("abc");
    }
}
