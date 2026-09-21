import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class LexScanner {

//    String input;
    int linha = 1, coluna = 0, posAtual = 0;
    ArrayList<Token> output;
    StringBuilder lexema;
    String input;
    char cAtual = ' ';


    public LexScanner(String input) {
        this.input = input;
        output = new ArrayList<Token>();
        lexema = new StringBuilder();
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

    // todo operador so tem tamanho 1 (teoricamente), ent da menos problema
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

        int inicioL = linha, inicioC = coluna, ehFloat = 0;
        posAtual++;

        while(posAtual < input.length()) {
            char c = input.charAt(posAtual);
            if(Character.isDigit(c)) {
                lexema.append(c);
                coluna++;
                posAtual++;
            } else if ((c == '.') && (ehFloat == 0)) {
                if((posAtual + 1 < input.length()) && (Character.isDigit(input.charAt(posAtual+1)))) {
                    ehFloat = 1;
                    lexema.append(c);
                    coluna++;
                    posAtual++;
                } 
                System.out.println("Erro léxico! String com números no começo!");
                lexema.setLength(0);
                return null;
            }
            if(ehFloat) {
                return new Token(lexema.toString(), TokenType.FLOAT_LIT, inicioL, inicioC);
                
            } else {
                return new Token(lexema.toString(), TokenType.INT_LIT, inicioL, inicioC);
            }
        }
        return null;
    }

    // ai aqui usa um hash de palavras chaves
    private Token analisarIdentificadorOuKeyword() {
        return null;
    }


    static void main() {
        LexScanner sc = new LexScanner("+-");
        sc.analisar();
    }
}
