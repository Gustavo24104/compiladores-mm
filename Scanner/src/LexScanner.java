import java.util.ArrayList;
import java.util.HashMap;

public class LexScanner {

//    String input;
    int linha = 1, coluna = 0, posAtual = 0;
    StringBuilder lexema;
    String input;
    HashMap<String, TokenType> palavrasReservadas = new HashMap<>();
    HashMap<String, TokenType> operadores = new HashMap<>();


    private void popularPalavrasReservadas() {
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

    private void popularOperadores() {
        operadores.put("==", TokenType.EQ_SYM);
        operadores.put("+", TokenType.PLUS_SYM);
        operadores.put("-", TokenType.MINUS_SYM);
        operadores.put("*", TokenType.MULT_SYM);
        operadores.put("/", TokenType.DIV_SYM);
        operadores.put("%", TokenType.MODULO_SYM);
        operadores.put("=", TokenType.ATRIB_SYM);
        operadores.put(">", TokenType.GREAT_SYM);
        operadores.put("<", TokenType.LESS_SYM);
        operadores.put(">=", TokenType.GEQ_SYM);
        operadores.put("<=", TokenType.LEQ_SYM);
        operadores.put("!=", TokenType.DIFF_SYM);
        operadores.put("+=", TokenType.PLUS_EQ_SYM);
        operadores.put("-=", TokenType.MINUS_EQ_SYM);
        operadores.put("*=", TokenType.MULT_EQ_SYM);
        operadores.put("/=", TokenType.DIV_EQ_SYM);
        operadores.put("||", TokenType.LOGICAL_OR_SYM);
        operadores.put("&&", TokenType.LOGICAL_AND_SYM);
        operadores.put("!", TokenType.LOGICAL_NOT_SYM);
        operadores.put("^", TokenType.BITWISE_XOR_SYM);
        operadores.put("&", TokenType.BITWSISE_AND_SYM);
        operadores.put("|", TokenType.BITWISE_OR_SYM);
    }

    public LexScanner(String input) {
        this.input = input;
        lexema = new StringBuilder();
        popularPalavrasReservadas();
        popularOperadores();
    }

    private boolean isOp(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '<' ||
                c == '>' || c == '!' || c == '=' || c == '%' ||
                c == '|' || c == '&';
    }

    // cada função aqui funciona como um dos estados


    private Token analisarOp() {
        char c = advance();
        char proxC = peek();
        StringBuilder opInteiro = new StringBuilder();
        opInteiro.append(c);

        if(isOp(proxC)) {
            opInteiro.append(proxC);
            advance();
        }

        TokenType tipo = operadores.get(opInteiro.toString());
        if(tipo != null) {
            return new Token(opInteiro.toString(), tipo, linha, coluna);
        } else {
            return null; // nao devia acontecer...
        }

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
//        coluna = posAtual;
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
        if(hasNext()) {
            return input.charAt(posAtual);
        }
        return ' ';
    }

    private void skipWhitespaceAndComments() {
        while(peek() == ' ' || peek() == '\n') {
            advance();
        }
        if(peek() == '@') {
            while(hasNext() && peek() != '\n') {
                advance();
            }
            // chegou no \n precisa de um ultimo advance
            if(hasNext() && peek() == '\n') {
                advance();
            }
        }
    }


    public Token nextToken() {
        try {
            skipWhitespaceAndComments();
            Token encontrado = null;
            lexema.append(peek());
//            if(peek() == '\n') {
//                lexema.setLength(0);
//                advance();
//            }
            /*else*/ if(peek() == ';'){
                encontrado = new Token(";", TokenType.PONTO_E_VIRGULA, linha, coluna);
                lexema.setLength(0);
                advance();
            }
            else if(peek() == '"') {
                encontrado = analisarString();
            }
            else if(Character.isDigit(peek())) {
                encontrado = analisarLiteral();
            }
            else if(isOp(peek())) {
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
        } catch (Exception e) {
            return new Token("\0", TokenType.EOF, linha, coluna);
        }
    }

    private char advance() {
        if(hasNext()){
            char out = input.charAt(posAtual++);
            if(out == '\n') {
                linha++;
                coluna = 0;
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

    public boolean hasNext() {
        return posAtual < input.length();
    }

    static void main() {
        LexScanner sc = new LexScanner("string txt = \"hello world\"; \n @comentario \n int a += 12; \n printf(txt);");
        var resultados = new ArrayList<Token>();

        while(sc.hasNext()) {
            resultados.add(sc.nextToken());
        }

        for (var r : resultados) {
            System.out.println(r);
        }
    }
}
