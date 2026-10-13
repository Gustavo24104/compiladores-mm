// Alunos:
// Gustavo Henrique Leal da Fosenca - 12321BCC001
// Osvaldo Pagliolli de Lollo da Silva - 12321BCC047
package parserLL1;
import Scanner.*;

import java.util.HashSet;
import java.util.Set;


public class ParserLL1 {
    private Token lookAhead;
    private LexScanner scanner;
    private Set<TokenType> syncPadrao = new HashSet<>();

    ParserLL1(LexScanner scanner) {
        this.scanner = scanner;
        this.lookAhead = scanner.nextToken();
        syncPadrao.add(TokenType.FECHA_COLCHETE);
        syncPadrao.add(TokenType.PONTO_E_VIRGULA);
        syncPadrao.add(TokenType.FECHA_PARENTESES);
    }


    void match(TokenType expected) {
        if(expected == lookAhead.tokenType) {
            lookAhead = scanner.nextToken();
        } else {
            System.out.println("Erro durante parsing!\nEsperado: " + expected.toString() + "\nEncontrado: " +
                    lookAhead.tokenType.toString());
            sync();
        }
    }

    void error(String msg) {
        System.out.println("Erro durante parsing!\nEsperado: " + msg + "\nEncontrado: "
                + lookAhead.tokenType.toString());

    }

    void sync(Set<TokenType> syncSet) {
        while(!syncSet.contains(lookAhead.tokenType) && lookAhead.tokenType != TokenType.EOF) {
            lookAhead = scanner.nextToken();
        }
    }

    void sync() {
        while(!syncPadrao.contains(lookAhead.tokenType) && lookAhead.tokenType != TokenType.EOF) {
            lookAhead = scanner.nextToken();
        }
    }

    // métodos de parsing...
    void parseConstante() {
        if (lookAhead.tokenType == TokenType.INT_LIT) {
            match(TokenType.INT_LIT);
        } else if (lookAhead.tokenType == TokenType.FLOAT_LIT) {
            match(TokenType.FLOAT_LIT);
        } else if (lookAhead.tokenType == TokenType.STRING_LIT) {
            match(TokenType.STRING_LIT);
        } else if (lookAhead.tokenType == TokenType.BOOL_LIT) {
            match(TokenType.BOOL_LIT);
        } else {
            error("literal ou constante");
            sync();
        }
    }

    void parseIdentificador() {
        if(lookAhead.tokenType == TokenType.ID) {
            match(TokenType.ID);
        } else {
            error("identificador");
        }
    }

    void parseEspecificador_Tipo() {
        if(lookAhead.tokenType == TokenType.INT_KEYWORD) {
            match(TokenType.INT_KEYWORD);
        } else if (lookAhead.tokenType == TokenType.FLOAT_KEYWORD) {
            match(TokenType.FLOAT_KEYWORD);
        } else if (lookAhead.tokenType == TokenType.BOOL_KEYWORD) {
            match(TokenType.BOOL_KEYWORD);
        } else if(lookAhead.tokenType == TokenType.STRING_KEYWORD) {
            match(TokenType.STRING_KEYWORD);
        } else if(lookAhead.tokenType == TokenType.VOID_KEYWORD) {
            match(TokenType.VOID_KEYWORD);
        } else {
            error("especificador de tipo");
            sync();
        }
    }


    void parseDeclaracao() {

    }

    void parseCond_aberta() {

    }

    void parseCond_fechada() {

    }

    void parseDecl_condicional() {

    }

}
