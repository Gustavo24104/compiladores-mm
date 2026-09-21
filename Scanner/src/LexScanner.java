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
    char cAtual = ' ';
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

        while(posAtual < input.length()) {
            cAtual = input.charAt(posAtual); // nota q o posAtual so eh atualizado dps
            coluna += 1;
            Token encontrado = null;

            lexema.append(cAtual);
            if(cAtual == '\n') {
                coluna = 1;
                linha += 1;
                lexema.setLength(0);
                posAtual++;
                continue;
            }

            else if(cAtual == ';'){
                output.add(new Token(";", TokenType.PONTO_E_VIRGULA, linha, coluna));
                lexema.setLength(0);
                posAtual++;
                continue;
            }

            else if(cAtual == ' ') {
                posAtual++;
                lexema.setLength(0);
                continue;
            }

            else if(cAtual == '"') {
                encontrado = analisarString();
            }

            else if(Character.isDigit(cAtual)) {
                encontrado = analisarLiteral();
            }

            else if(cAtual == '+' || cAtual == '-' || cAtual == '*' || cAtual == '/' || cAtual == '=') {
                encontrado = analisarOp();
            }

            else {
                encontrado = analisarIdentificadorOuKeyword();
            }
            if(encontrado != null) {
                output.add(encontrado);
                lexema.setLength(0);
            } else {
                System.out.println("Erro lexico! Caractere inesperado na linha " + linha + " e coluna " + coluna);
            }
        }
        return output;

    }


    private Token analisarOp() {
        if(cAtual == '+') {
            posAtual++;
            return new Token(lexema.toString(), TokenType.PLUS_SYM, linha, coluna);
        } else if(cAtual == '-') {
            posAtual++;
            return new Token(lexema.toString(), TokenType.MINUS_SYM, linha, coluna);
        } else if (cAtual == '/') {
            posAtual++;
            return new Token(lexema.toString(), TokenType.DIV_SYM, linha, coluna);
        } else if (cAtual == '*') {
            posAtual++;
            return new Token(lexema.toString(), TokenType.MULT_SYM, linha, coluna);
        } else if(cAtual == '=') {
            posAtual++;

            if(posAtual < input.length() && input.charAt(posAtual) == '=') {
                posAtual++;
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

        if(cAtual != '"') {
            System.out.println("Erro léxico! String não iniciada em aspas!");
            return null;
        }

        posAtual++;

        while(posAtual < input.length()) {
            char c = input.charAt(posAtual);
            lexema.append(c);
            coluna++;
            posAtual++;

            if (c == '"') {
                return new Token(lexema.toString(), TokenType.STRING_LIT, inicioL, inicioC);
            }

            if(c == '\n') {
                System.out.println("Quebra de linha detectada! String nao encerrada na linha" + inicioL + "e coluna" + inicioC);
                linha++;
                coluna = 1;
                lexema.setLength(0);

            }
        }

        System.out.println("ERRO lexico. EOF, string nao encerrada");
        lexema.setLength(0);
        return null;
    }

    private Token analisarLiteral() {

        int inicioL = linha;
        int inicioC = coluna;
        boolean ehFloat = false;
        posAtual++;

        while(posAtual < input.length()) {
            char c = input.charAt(posAtual);
            if(Character.isDigit(c)) {
                lexema.append(c);
                posAtual++;
            } else if ((c == '.') && (!ehFloat)) {
                if((posAtual + 1 < input.length()) && (Character.isDigit(input.charAt(posAtual+1)))) {
                    ehFloat = true;
                    lexema.append(c);
                    posAtual++;
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
        // identificadores de 1 letra
        if(palavrasReservadas.get(Character.toString(cAtual)) != null) {
            posAtual++;
            return new Token(lexema.toString(), palavrasReservadas.get(Character.toString(cAtual)), linha, coluna);
        }

        while(posAtual < input.length()) {
            if (posAtual + 1 < input.length()) {
                char proxC = input.charAt(posAtual + 1);
                if (proxC == '+' || proxC == '-' ||
                        proxC == '*' || proxC == '/' || proxC == '=' || proxC == ' ' || proxC == ';' ||
                        (palavrasReservadas.get(Character.toString(proxC)) != null)) {
                    posAtual++;
                    break;
                } else {
                    cAtual = proxC;
                    posAtual++;
                    lexema.append(cAtual);
                }
            } else {
                posAtual++;
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
        coluna = posAtual;
        lexema.setLength(0);
        return out;
    }


    static void main() {
        LexScanner sc = new LexScanner("if ( 12;42 )");
        var Resultados = sc.analisar();
        for (var r : Resultados) {
            System.out.println(r);
        }
    }
}
