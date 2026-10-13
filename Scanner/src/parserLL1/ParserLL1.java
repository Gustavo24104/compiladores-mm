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
            System.out.println("Erro durante parsing! Esperado: " + expected.toString() + "\nEncontrado: " +
                    lookAhead.tokenType.toString());
            sync(syncPadrao);
        }
    }

    void sync(Set<TokenType> syncSet) {
        while(!syncSet.contains(lookAhead.tokenType) && lookAhead.tokenType != TokenType.EOF) {
            lookAhead = scanner.nextToken();
        }
    }

    // métodos de parsing...

}
