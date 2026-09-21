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
            }

            else if(cAtual == ';'){
                output.add(new Token(";", TokenType.PONTO_E_VIRGULA, linha, coluna));
                lexema.setLength(0);
            }

            else if(cAtual == '"') {
                encontrado = analisarString();
            }

            else if(Character.isDigit(cAtual)) {
                encontrado = analisarLiteral();
            }

            else if(!Character.isAlphabetic(cAtual)) {
                encontrado = analisarOp();
            }

            else {
                encontrado = analisarIdentificadorOuKeyword();
            }
            if(encontrado != null) {
                output.add(encontrado);
                System.out.println(encontrado);
                lexema.setLength(0);
            } else {
                System.out.println("Erro lexico! Caractere inesperado na linha " + linha + " e coluna " + coluna);
            }
        }
        return output;

    }


    private Token analisarOp() {
        System.out.println("operador");
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
                return new Token(lexema.toString(), TokenType.ATRIB, linha, coluna);
            }

            return new Token(lexema.toString(), TokenType.EQ_SYM, linha, coluna);
        }
        return null;
    }


    // ai aqui tem q olhar pro proximo pra saber se continua ou nao
    private Token analisarString() {
        int inicioL = linha, inicioC = coluna;

        if(posAtual != '"') {
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
        return null;
    }

    // ai aqui usa um hash de palavras chaves
    private Token analisarIdentificadorOuKeyword() {
        posAtual++;

        while (posAtual < input.length() && cAtual != '+' && cAtual != '-' &&
            cAtual != '*' && cAtual != '/' && cAtual != '=' && cAtual != ' ' && cAtual != ';') {

        }

        System.out.println(lexema.toString());
        return null;
    }


    static void main() {
        LexScanner sc = new LexScanner("id");
        sc.analisar();
    }
}
