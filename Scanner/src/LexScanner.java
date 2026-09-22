import java.io.EOFException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class LexScanner {

//    String input;
    int linha = 1, coluna = 0, posAtual = 0;
    ArrayList<Token> output;
    StringBuilder lexema;
    String input;
    HashMap<String, TokenType> palavrasReservadas = new HashMap<>();



    public LexScanner(String input) {
        this.input = input;
        output = new ArrayList<Token>();
        lexema = new StringBuilder();
        palavrasReservadas.put("if", TokenType.IF_KEYWORD);
        palavrasReservadas.put("else", TokenType.ELSE_KEYWORD);
        palavrasReservadas.put("while", TokenType.WHILE_KEYWORD);
        palavrasReservadas.put("for", TokenType.FOR_KEYWORD);
        palavrasReservadas.put("int", TokenType.INT_KEYWORD);
        palavrasReservadas.put("float", TokenType.FLOAT_KEYWORD);
        palavrasReservadas.put("string", TokenType.STRING_KEYWORD);
        palavrasReservadas.put("return", TokenType.RETURN_KEYWORD);
        palavrasReservadas.put("void", TokenType.VOID_KEYWORD);
        palavrasReservadas.put("(", TokenType.ABRE_PARENTESES);
        palavrasReservadas.put(")", TokenType.FECHA_PARENTESES);
        palavrasReservadas.put("{", TokenType.ABRE_CHAVES);
        palavrasReservadas.put("}", TokenType.FECHA_CHAVES);
        palavrasReservadas.put("[", TokenType.ABRE_COLCHETE);
        palavrasReservadas.put("]", TokenType.FECHA_COLCHETE);
        palavrasReservadas.put(";", TokenType.PONTO_E_VIRGULA);
        palavrasReservadas.put(",", TokenType.VIRGULA);
        palavrasReservadas.put(".", TokenType.PONTO);
    }

    public ArrayList<Token> analisar() {
        ArrayList<Token> output = new ArrayList<>();

        while(hasNext()) {
            output.add(nextToken());
        }
        return output;

    }


    private Token analisarOp() {
        char c = advance();
        if(c == '+') {
            return new Token(lexema.toString(), TokenType.PLUS_SYM, linha, coluna);
        } else if(c == '-') {
            return new Token(lexema.toString(), TokenType.MINUS_SYM, linha, coluna);
        } else if (c == '/') {
            return new Token(lexema.toString(), TokenType.DIV_SYM, linha, coluna);
        } else if (c == '*') {
            return new Token(lexema.toString(), TokenType.MULT_SYM, linha, coluna);
        } else if(c== '=') {
            if(hasNext() && peek() == '=') {
                advance();
                lexema.append('=');
                return new Token(lexema.toString(), TokenType.EQ_SYM, linha, coluna);
            }
            return new Token(lexema.toString(), TokenType.ATRIB, linha, coluna);
        }
        return null;
    }


    // ai aqui tem q olhar pro proximo pra saber se continua ou nao
    private Token analisarString() {
        int inicioL = linha, inicioC = coluna;

        if(peek() != '"') {
            System.out.println("Erro léxico! String não iniciada em aspas!");
            return null;
        }

        advance();

        while(hasNext()) {
            char c = advance();
            lexema.append(c);

            if (c == '"') {
                return new Token(lexema.toString(), TokenType.STRING_LIT, inicioL, inicioC);
            }

            if(c == '\n') {
                // TODO: Transformar em exception
                System.out.println("Quebra de linha detectada! String nao encerrada na linha" + inicioL + "e coluna" + inicioC);
                linha++;
                coluna = 1;
                lexema.setLength(0);
            }
        }
        // TODO: Transformar em exception
        System.out.println("ERRO lexico. EOF, string nao encerrada");
        lexema.setLength(0);
        return null;
    }


    private Token analisarLiteral() {

        int inicioL = linha;
        int inicioC = coluna;
        boolean ehFloat = false;
        advance();

        while(hasNext()) {
            char c = peek();
            if(Character.isDigit(c)) {
                lexema.append(c);
                advance();
            } else if ((c == '.') && (!ehFloat)) {
                if(Character.isDigit(lookAhead())) {
                    ehFloat = true;
                    lexema.append(c);
                    advance();
                }
            } else {
                break;
            }
        }
        coluna = posAtual;
        if(ehFloat) {
            return new Token(lexema.toString(), TokenType.FLOAT_LIT, inicioL, inicioC);
        } else {
            return new Token(lexema.toString(), TokenType.INT_LIT, inicioL, inicioC);
        }
    }

    // ai aqui usa um hash de palavras chaves
    private Token analisarIdentificadorOuKeyword() {
        char cAtual;
        // identificadores de 1 letra
        if(palavrasReservadas.get(Character.toString(peek())) != null) {
            return new Token(lexema.toString(), palavrasReservadas.get(Character.toString(advance())), linha, coluna);
        }

        while(hasNext()) {
            if (posAtual + 1 < input.length()) {
                char proxC = lookAhead();
                if (proxC == '+' || proxC == '-' ||
                        proxC == '*' || proxC == '/' || proxC == '=' || proxC == ' ' || proxC == ';' ||
                        (palavrasReservadas.get(Character.toString(proxC)) != null)) {
                    advance();
                    break;
                } else {
                    cAtual = proxC;
                    advance();
                    lexema.append(cAtual);
                }
            } else {
                advance();
            }
        }
        // agr eh definir se eh identificador ou reservado
        TokenType tokenEncontrado = palavrasReservadas.get(lexema.toString());
        Token out;

        if(tokenEncontrado == null) {
            out = new Token(lexema.toString(), TokenType.ID, linha, coluna);
        } else {
            out = new Token(lexema.toString(), tokenEncontrado, linha, coluna);
        }
        lexema.setLength(0);
        return out;
    }

    private char peek() {
        return input.charAt(posAtual);
    }

    // TODO: Falta o "andComments"
    private void skipWhitespace() {
        while(peek() == ' ') {
            advance();
        }
    }

    private Token nextToken() {
        skipWhitespace();
        Token encontrado = null;
        lexema.append(peek());
        if(peek() == '\n') {
            lexema.setLength(0);
            advance();
        }
        else if(peek() == ';'){
           encontrado = new Token(";", TokenType.PONTO_E_VIRGULA, linha, coluna);
            lexema.setLength(0);
            advance();
        }
        else if(peek() == ' ') {
            advance();
            lexema.setLength(0);
        }
        else if(peek() == '"') {
            encontrado = analisarString();
        }
        else if(Character.isDigit(peek())) {
            encontrado = analisarLiteral();
        }
        else if(peek() == '+' || peek() == '-' || peek() == '*' || peek() == '/' || peek() == '=') {
            encontrado = analisarOp();
        }
        else {
            encontrado = analisarIdentificadorOuKeyword();
        }
        if(encontrado != null) {
            lexema.setLength(0);
            return encontrado;
        } else {
            System.out.println("Erro lexico! Caractere inesperado na linha " + linha + " e coluna " + coluna);
            return null;
        }
    }

    private char advance() {
        if(hasNext()){
            char out = input.charAt(posAtual++);
            if(out == '\n') {
                linha++;
                coluna = 1;
            }
            coluna++;
            return out;
        } else {
            throw new RuntimeException("Fim de arquivo!");
        }
    }

    private char lookAhead() {
        if(posAtual + 1 < input.length()){
            return input.charAt(posAtual + 1);
        } else {
            throw new RuntimeException("Fim de arquivo!");
        }
    }

    private boolean hasNext() {
        return posAtual < input.length();
    }

    static void main() {
        LexScanner sc = new LexScanner("if ( 124323.4 3.42 =;= 42 )");
        var Resultados = sc.analisar();
        for (var r : Resultados) {
            System.out.println(r);
        }
    }
}
