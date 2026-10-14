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
        match(TokenType.PONTO_E_VIRGULA);
    }

    boolean firstEspecificacao_tipo(TokenType tt) {
        return tt == TokenType.INT_KEYWORD ||
                tt == TokenType.FLOAT_KEYWORD ||
                tt == TokenType.BOOL_KEYWORD ||
                tt == TokenType.STRING_KEYWORD ||
                tt  == TokenType.VOID_KEYWORD;
    }

    boolean firstConstante(TokenType tt) {
        return tt == TokenType.INT_LIT ||
                tt == TokenType.FLOAT_LIT ||
                tt == TokenType.STRING_LIT ||
                tt == TokenType.BOOL_LIT;
    }


    boolean firstExp_unaria(TokenType tt) {
        return tt == TokenType.LOGICAL_NOT_SYM ||
                tt == TokenType.ABRE_PARENTESES ||
                tt == TokenType.ID ||
                firstConstante(tt);
    }

    boolean firstPrimaria(TokenType tt) {
        return tt == TokenType.ABRE_PARENTESES ||
                tt == TokenType.ID ||
                firstConstante(tt);
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
            parseIdentificadorPrime();
        } else {
            error("identificador");
            sync();
        }
    }

    void parseIdentificadorPrime() {
        if(lookAhead.tokenType == TokenType.ABRE_PARENTESES) {
            match(TokenType.ABRE_PARENTESES);
            parseLsExps();
            match(TokenType.FECHA_PARENTESES);
        } else { // prod vazia
            return;
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
            System.out.println("Parsing terminado!");
        } else {
            error("Esperado especificador de tipo");
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

    void parseExpression(){
        parseExp_or();
    }

    void parseExp_or() {
        parseExp_and();
        parseExp_orPrime();
    }

    void parseExp_orPrime(){
        if(lookAhead.tokenType == TokenType.LOGICAL_OR_SYM) {
            match(TokenType.LOGICAL_OR_SYM);
            parseExp_and();
            parseExp_orPrime();
        } else { // prod vazia
            return;
        }
    }

    void parseExp_and(){
        parseExp_bitor();
        parseExp_andPrime();
    }

    void parseExp_andPrime() {
        if(lookAhead.tokenType == TokenType.LOGICAL_AND_SYM) {
            match(TokenType.LOGICAL_AND_SYM);
            parseExp_bitor();
            parseExp_andPrime();
        } else { // prod vazia
            return;
        }
    }

    void parseExp_bitor() {
            parseExp_bitXor();
            parseExp_bitorPrime();
    }

    void parseExp_bitorPrime() {
        if(lookAhead.tokenType == TokenType.BITWISE_OR_SYM) {
            match(TokenType.BITWISE_OR_SYM);
            parseExp_bitXor();
            parseExp_bitorPrime();
        } else { // prod vazia
            return;
        }
    }

    void parseExp_bitXor() {
        parseExp_bitAnd();
        parseExp_bitXorPrime();
    }

    void parseExp_bitXorPrime() {
        if(lookAhead.tokenType == TokenType.BITWISE_XOR_SYM) {
            match(TokenType.BITWISE_XOR_SYM);
            parseExp_bitAnd();
            parseExp_bitXorPrime();
        } else { // prod vazia
            return;
        }
    }

    void parseExp_bitAnd() {
        parseExp_igualdade();
        parseExp_bitAndPrime();
    }

    void parseExp_bitAndPrime() {
        if(lookAhead.tokenType == TokenType.BITWSISE_AND_SYM) {
            match(TokenType.BITWSISE_AND_SYM);
            parseExp_igualdade();
            parseExp_bitAndPrime();
        } else { // prod vazia
            return;
        }
    }

    void parseExp_igualdade() {
        parseExp_rel();
        parseExp_igualdadePrime();
    }

    void parseExp_igualdadePrime() {
        if(lookAhead.tokenType == TokenType.EQ_SYM) {
            match(TokenType.EQ_SYM);
            parseExp_rel();
            parseExp_igualdadePrime();
        } else if (lookAhead.tokenType == TokenType.DIFF_SYM) {
            match(TokenType.DIFF_SYM);
            parseExp_rel();
            parseExp_igualdadePrime();
        }
        else { // prod vazia
            return;
        }
    }

    void parseExp_rel() {
        parseExp_Add();
        parseExp_relPrime();
    }

    void parseExp_relPrime() {
        if(lookAhead.tokenType == TokenType.GEQ_SYM) {
            match(TokenType.GEQ_SYM);
            parseExp_Add();
            parseExp_relPrime();
        } else if (lookAhead.tokenType == TokenType.LEQ_SYM) {
            match(TokenType.LEQ_SYM);
            parseExp_Add();
            parseExp_relPrime();
        } else if (lookAhead.tokenType == TokenType.LESS_SYM) {
            match(TokenType.LESS_SYM);
            parseExp_Add();
            parseExp_relPrime();
        } else if (lookAhead.tokenType == TokenType.GREAT_SYM) {
            match(TokenType.GREAT_SYM);
            parseExp_Add();
            parseExp_relPrime();
        }
        else { // prod vazia
            return;
        }
    }

    void parseExp_Add() {
            parseExp_mult();
            parseExp_AddPrime();
    }

    void parseExp_AddPrime() {
        if(lookAhead.tokenType == TokenType.PLUS_SYM) {
            match(TokenType.PLUS_SYM);
            parseExp_mult();
            parseExp_AddPrime();
        } else if (lookAhead.tokenType == TokenType.MINUS_SYM) {
            match(TokenType.MINUS_SYM);
            parseExp_mult();
            parseExp_AddPrime();
        }
        else { // prod vazia
            return;
        }
    }

    void parseExp_mult() {
            parseExp_Unaria();
            parseExp_multPrime();
    }

    void parseExp_multPrime() {
        if(lookAhead.tokenType == TokenType.MULT_SYM) {
            match(TokenType.MULT_SYM);
            parseExp_Unaria();
            parseExp_multPrime();
        } else if (lookAhead.tokenType == TokenType.DIV_SYM) {
            match(TokenType.DIV_SYM);
            parseExp_Unaria();
            parseExp_multPrime();
        }
        else { // prod vazia
            return;
        }
    }

    void parseExp_Unaria() {
        if(lookAhead.tokenType == TokenType.LOGICAL_NOT_SYM) {
            match(TokenType.LOGICAL_NOT_SYM);
            parseExp_Unaria();
        } else if (firstPrimaria(lookAhead.tokenType)) {
            parsePrimaria();
        } else {
            error("expressao mal formada");
            sync();
        }
    }

    void parsePrimaria() {
        if(lookAhead.tokenType == TokenType.ABRE_PARENTESES) {
            match(TokenType.ABRE_PARENTESES);
            parseExpression();
            match(TokenType.FECHA_PARENTESES);
        } else if(lookAhead.tokenType == TokenType.ID) {
            parseIdentificador();
        } else if(firstConstante(lookAhead.tokenType)) {
            parseConstante();
        } else {
            error("expressao mal formada");
            sync();
        }
    }

    void parseDecl_condicional() {
        if(lookAhead.tokenType == TokenType.IF_KEYWORD) {
            match(TokenType.IF_KEYWORD);
            match(TokenType.ABRE_PARENTESES);
            parseExpression();
            match(TokenType.FECHA_PARENTESES);
            parseDeclBloco();
            parseElse();
        } else {
            error("declaracao condicional");
            sync();
        }
    }

    void parseElse() {
        if(lookAhead.tokenType == TokenType.ELSE_KEYWORD) {
            match(TokenType.ELSE_KEYWORD);
            parseElsePrime();
        } else { // prod vazia
            return;
        }
    }

    void parseElsePrime() {
        if(lookAhead.tokenType == TokenType.ABRE_CHAVES) { // first de <decl_bloco>
            parseDeclBloco();
        } else if (lookAhead.tokenType == TokenType.IF_KEYWORD) { // first de <decl_condicional>
            parseDecl_condicional();
        } else {
            error("else errado");
            sync();
        }
    }

    void parseDeclLaco() {
        if(lookAhead.tokenType == TokenType.WHILE_KEYWORD) {
            match(TokenType.WHILE_KEYWORD);
            match(TokenType.ABRE_PARENTESES);
            parseExpression();
            match(TokenType.FECHA_PARENTESES);
            parseDeclBloco();
        } else if (lookAhead.tokenType == TokenType.FOR_KEYWORD) {
            match(TokenType.FOR_KEYWORD);
            match(TokenType.ABRE_PARENTESES);
            parseDeclID();
            parseExpression();
            match(TokenType.PONTO_E_VIRGULA);
            parseComando_exp();
            match(TokenType.FECHA_PARENTESES);
            parseDeclBloco();
        } else {
            error("declaracao de laco (for ou while)");
            sync();
        }
    }

    void parseDeclaracao() {
        if (firstEspecificacao_tipo(lookAhead.tokenType)) {
            parseDeclID();
            parseDeclaracao();
        } else if (lookAhead.tokenType == TokenType.RETURN_KEYWORD) {
            parseDeclRetorno();
            parseDeclaracao();
        } else if (firstExp_unaria(lookAhead.tokenType)) {
            parseComando_exp();
            parseDeclaracao();
        } else if (lookAhead.tokenType == TokenType.IF_KEYWORD) {
            parseDecl_condicional();
            parseDeclaracao();
        } else if (lookAhead.tokenType == TokenType.WHILE_KEYWORD || lookAhead.tokenType == TokenType.FOR_KEYWORD) {
            parseDeclLaco();
            parseDeclaracao();
        } else if (lookAhead.tokenType == TokenType.FECHA_CHAVES || lookAhead.tokenType == TokenType.EOF) { // prod vazia
            return;
        } else {
            error("declaração válida (tipo, retorno, atribuição, condição ou laço)");
            sync();
        }
    }

    void parseComando_exp(){
        parseExpression();
        parseComando_expPrime();
        match(TokenType.PONTO_E_VIRGULA);
    }

    void parseComando_expPrime() {
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

        } else { // prod vazia
            return;
        }
    }

    void parseLsParam(){
        if(firstEspecificacao_tipo(lookAhead.tokenType)) {
            parseEspecificador_Tipo();
            parseIdentificador();
            parseLsParamPrime();
        } else {
            error("lista de parametros");
            sync();
        }
    }

    void parseLsParamPrime() {
        if(lookAhead.tokenType == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            parseLsParam();
        } else { //prod vazia
            return;
        }
    }

    void parseLsExpr(){
        if(firstExp_unaria(lookAhead.tokenType)) {
            parseExpression();
            parseLsExprPrime();
        } else {
            error("lista de expressoes");
            sync();
        }
    }

    void parseLsExprPrime(){
        if(lookAhead.tokenType == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            parseLsExpr();
        } else { // prod vazia
            return;
        }
    }

    void parseLsExps() {
        if(firstExp_unaria(lookAhead.tokenType)) {
            parseLsExpr();
        } else { // prod vazia
            return;
        }
    }

    void parseParams() {
        if(firstEspecificacao_tipo(lookAhead.tokenType)) {
            parseLsParam();
        } else { // prod vazia
            return;
        }
    }

    void parseDeclID(){
        if(firstEspecificacao_tipo(lookAhead.tokenType)) {
            parseEspecificador_Tipo();
            match(TokenType.ID);
            parseDeclIDPrime();
        } else {
            error("declaração de identificador");
            sync();
        }
    }

    void parseDeclIDPrime() {
        if(lookAhead.tokenType == TokenType.ABRE_PARENTESES) {
            match(TokenType.ABRE_PARENTESES);
            parseParams();
            match(TokenType.FECHA_PARENTESES);
            parseDeclBloco();
        } else if(lookAhead.tokenType == TokenType.ATRIB_SYM) {
            match(TokenType.ATRIB_SYM);
            parseExpression();
            match(TokenType.PONTO_E_VIRGULA);
        } else if (lookAhead.tokenType == TokenType.PONTO_E_VIRGULA) {
            match(TokenType.PONTO_E_VIRGULA);
        } else {
            error("declaracao de ID");
            sync();
        }
    }

    void parseDeclPrim(){
        if(firstEspecificacao_tipo(lookAhead.tokenType)) {
            parseDeclID();
            parseDeclPrim();
        } else { // prod vazia
            return;
        }
    }

    void parseDeclBloco(){
        if(lookAhead.tokenType == TokenType.ABRE_CHAVES) {
            match(TokenType.ABRE_CHAVES);
            parseDeclaracao();
            match(TokenType.FECHA_CHAVES);
        } else {
            error("declaracao em bloco (esqueceu de fechar ou abrir chaves?)");
            sync();
        }
    }


    void parseDeclRetorno(){
        if(lookAhead.tokenType == TokenType.RETURN_KEYWORD) {
            match(TokenType.RETURN_KEYWORD);
            parseDeclRetornoPrime();
        } else {
            error("return");
            sync();
        }
    }

    void parseDeclRetornoPrime() {
        if(firstExp_unaria(lookAhead.tokenType)) {
            parseExpression();
            match(TokenType.PONTO_E_VIRGULA);
        } else if(lookAhead.tokenType == TokenType.PONTO_E_VIRGULA) {
            match(TokenType.PONTO_E_VIRGULA);
        } else {
            error("expressao ou ;");
            sync();
        }
    }

    static void main() {
        String input = "void processar() {\n" +
                "    abcd i = 0;\n" +
                "    while (i < 5) {\n" +
                "        i = i + 1;\n" +
                "    }\n" +
                "    for (int j = 0; j < 5; j = j + 1;) {\n" +
                "        int x = j;\n" +
                "    }\n" +
                "}";
        ParserLL1 pl1 = new ParserLL1(new LexScanner(input));
        pl1.parse$();
    }

}
