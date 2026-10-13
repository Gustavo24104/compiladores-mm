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

    void parse$() {
        if(lookAhead.tokenType == TokenType.INT_KEYWORD ||
                lookAhead.tokenType == TokenType.FLOAT_KEYWORD ||
                lookAhead.tokenType == TokenType.BOOL_KEYWORD ||
                lookAhead.tokenType == TokenType.STRING_KEYWORD ||
                lookAhead.tokenType == TokenType.VOID_KEYWORD){ // first de <decl_primaria>
            parseDeclPrim();
            match(TokenType.EOF);
        } else {
            error("Esperado especificador de tipo");
        }
    }

    void parseAtribuicao() {
        if(lookAhead.tokenType == TokenType.ID) {
            parseIdentificador();
            parseAtribuicaoPrime();
        } else {
            error("identificador");
        }
    }

    void parseAtribuicaoPrime() {
        if(lookAhead.tokenType == TokenType.ATRIB_SYM) {
            match(TokenType.ATRIB_SYM);
            parseExpression();
        } else if (lookAhead.tokenType == TokenType.PLUS_EQ_SYM) {
            match(TokenType.PLUS_EQ_SYM);
            parseExpression();

        } else if (lookAhead.tokenType == TokenType.MINUS_EQ_SYM) {
            match(TokenType.MINUS_EQ_SYM);
            parseExpression();

        } else if(lookAhead.tokenType == TokenType.MULT_EQ_SYM) {
            match(TokenType.MULT_EQ_SYM);
            parseExpression();

        } else if(lookAhead.tokenType == TokenType.DIV_EQ_SYM) {
            match(TokenType.DIV_EQ_SYM);
            parseExpression();

        } else {
            error("operacao de atribuicao");
            sync();
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

    void parseExpComp(){
        parseExpression();
        parseExpCompPrime();
    }

    void parseExpCompPrime() {
        if(lookAhead.tokenType == TokenType.EQ_SYM) {
            match(TokenType.EQ_SYM);
            parseExpression();
        } else if (lookAhead.tokenType == TokenType.GEQ_SYM){
            match(TokenType.GEQ_SYM);
            parseExpression();
        } else if (lookAhead.tokenType == TokenType.LEQ_SYM){
            match(TokenType.LEQ_SYM);
            parseExpression();
        } else if (lookAhead.tokenType == TokenType.DIFF_SYM){
            match(TokenType.DIFF_SYM);
            parseExpression();
        } else {
            error("expressao de comparação");
        }
    }

    void parseExpLogico(){
        if(lookAhead.tokenType == TokenType.LOGICAL_NOT_SYM) {
            match(TokenType.LOGICAL_NOT_SYM);
            parseExpression();
        } else {
            parseExpression();
            parseExpLogicoPrime();
        }
    }

    void parseExpLogicoPrime(){
        if(lookAhead.tokenType == TokenType.BITWISE_XOR_SYM) {
            match(TokenType.BITWISE_XOR_SYM);
            parseExpression();
        } else if (lookAhead.tokenType == TokenType.BITWISE_OR_SYM){
            match(TokenType.BITWISE_OR_SYM);
            parseExpression();
        } else if (lookAhead.tokenType == TokenType.BITWSISE_AND_SYM){
            match(TokenType.BITWSISE_AND_SYM);
            parseExpression();
        } else {
            error("expressao de operação bit a bit");
        }
    }


    void parseCond_aberta() {

    }

    void parseCond_fechada() {

    }
    void parseDecl_condicional() {

    }
    void parseInicial(){}
    void parseExpMat(){}
    void parseDeclLaco(){}
    void parseExpression(){}
    void parseLsParam(){}
    void parseLsExpr(){}
    void parseDeclID(){}
    void parseDeclPrim(){}
    void parseDeclBloco(){}
    void parseDeclRetorno(){}

}
