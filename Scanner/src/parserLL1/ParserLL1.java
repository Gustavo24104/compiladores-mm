// Alunos:
// Gustavo Henrique Leal da Fosenca - 12321BCC001
// Osvaldo Pagliolli de Lollo da Silva - 12321BCC047
package parserLL1;
import Scanner.*;
import ast.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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


    Token match(TokenType expected) {
        Token matched = lookAhead;
        if(expected == lookAhead.tokenType) {
            lookAhead = scanner.nextToken();
            return matched;
        } else {
            System.out.println("Erro durante parsing!\nEsperado: " + expected.toString() + "\nEncontrado: " +
                    lookAhead.tokenType.toString());
            sync();
            return null;
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
        match(lookAhead.tokenType);
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
    ConstExp parseConstante() {
        Token anterior = lookAhead;
        if (lookAhead.tokenType == TokenType.INT_LIT) {
            int found = Integer.parseInt(lookAhead.lexema);
            match(TokenType.INT_LIT);
            return new ConstExp(anterior.linha, anterior.coluna, found,
                    new Tipo(anterior.linha, anterior.coluna, "int"));
        } else if (lookAhead.tokenType == TokenType.FLOAT_LIT) {
            float found = Float.parseFloat(lookAhead.lexema);
            match(TokenType.FLOAT_LIT);
            return new ConstExp(anterior.linha, anterior.coluna, found,
                    new Tipo(anterior.linha, anterior.coluna, "float"));
        } else if (lookAhead.tokenType == TokenType.STRING_LIT) {
            String found = lookAhead.lexema;
            match(TokenType.STRING_LIT);
            return new ConstExp(anterior.linha, anterior.coluna, found,
                    new Tipo(anterior.linha, anterior.coluna, "string"));
        } else if (lookAhead.tokenType == TokenType.BOOL_LIT) {
            boolean found = Boolean.parseBoolean(lookAhead.lexema);
            match(TokenType.BOOL_LIT);
            return new ConstExp(anterior.linha, anterior.coluna, found,
                    new Tipo(anterior.linha, anterior.coluna, "string"));
        } else {
            error("literal ou constante");
            sync();
            return null;
        }
    }

    Exp parseIdentificador() {
        if(lookAhead.tokenType == TokenType.ID) {
            Token anterior = lookAhead;
            match(TokenType.ID);
            NoIdentificador ni = new NoIdentificador(anterior.linha, anterior.coluna, anterior.lexema);
            return parseIdentificadorPrime(ni);
        } else {
            error("identificador");
            sync();
            return null;
        }
    }

    Exp parseIdentificadorPrime(NoIdentificador e) {
        if(lookAhead.tokenType == TokenType.ABRE_PARENTESES) {
            Token anterior = match(TokenType.ABRE_PARENTESES);
            List<Exp> ls = parseLsExps();
            match(TokenType.FECHA_PARENTESES);
            return new ChamadaExp(anterior.linha, anterior.coluna, e, ls);
        } else { // prod vazia
            return e;
        }
    }

    NoInicial parse$() {
        if(lookAhead.tokenType == TokenType.INT_KEYWORD ||
                lookAhead.tokenType == TokenType.FLOAT_KEYWORD ||
                lookAhead.tokenType == TokenType.BOOL_KEYWORD ||
                lookAhead.tokenType == TokenType.STRING_KEYWORD ||
                lookAhead.tokenType == TokenType.VOID_KEYWORD){ // first de <decl_primaria>
            List<Decl> ls = parseDeclPrim();
            Token anterior = match(TokenType.EOF);
            System.out.println("Parsing terminado!");
            return new NoInicial(anterior.linha, anterior.coluna, ls);
        } else {
            error("Esperado especificador de tipo");
            sync();
            return null;
        }
    }

    Tipo parseEspecificador_Tipo() {
        if(lookAhead.tokenType == TokenType.INT_KEYWORD) {
            Token anterior = match(TokenType.INT_KEYWORD);
            return new Tipo(anterior.linha, anterior.coluna, "int");
        } else if (lookAhead.tokenType == TokenType.FLOAT_KEYWORD) {
            Token anterior = match(TokenType.FLOAT_KEYWORD);
            return new Tipo(anterior.linha, anterior.coluna, "float");
        } else if (lookAhead.tokenType == TokenType.BOOL_KEYWORD) {
            Token anterior = match(TokenType.BOOL_KEYWORD);
            return new Tipo(anterior.linha, anterior.coluna, "bool");
        } else if(lookAhead.tokenType == TokenType.STRING_KEYWORD) {
            Token anterior = match(TokenType.STRING_KEYWORD);
            return new Tipo(anterior.linha, anterior.coluna, "string");
        } else if(lookAhead.tokenType == TokenType.VOID_KEYWORD) {
            Token anterior = match(TokenType.VOID_KEYWORD);
            return new Tipo(anterior.linha, anterior.coluna, "void");
        } else {
            error("especificador de tipo");
            sync();
            return null;
        }
    }

    Exp parseExpression(){
        return parseExp_or();
    }

    Exp parseExp_or() {
        Exp left = parseExp_and();
        return parseExp_orPrime(left);
    }

    Exp parseExp_orPrime(Exp e){
        if(lookAhead.tokenType == TokenType.LOGICAL_OR_SYM) {
            Token ant = match(TokenType.LOGICAL_OR_SYM);
            Exp right = parseExp_and();
            Exp newLeft = new BinExp(ant.linha, ant.coluna, e, right, "||");
            return parseExp_orPrime(newLeft);
        } else { // prod vazia
            return e;
        }
    }

    Exp parseExp_and(){
        Exp left = parseExp_bitor();
        return parseExp_andPrime(left);
    }

    Exp parseExp_andPrime(Exp e) {
        if(lookAhead.tokenType == TokenType.LOGICAL_AND_SYM) {
            Token ant = match(TokenType.LOGICAL_AND_SYM);
            Exp r = parseExp_bitor();
            return parseExp_andPrime(new BinExp(ant.linha, ant.coluna, e, r, "&&"));
        } else { // prod vazia
            return e;
        }
    }

    Exp parseExp_bitor() {
        Exp l = parseExp_bitXor();
        return parseExp_bitorPrime(l);
    }

    Exp parseExp_bitorPrime(Exp e) {
        if(lookAhead.tokenType == TokenType.BITWISE_OR_SYM) {
            Token ant = match(TokenType.BITWISE_OR_SYM);
            Exp r = parseExp_bitXor();
            return parseExp_bitorPrime(new BinExp(ant.linha, ant.coluna, e, r, "|"));
        } else { // prod vazia
            return e;
        }
    }

    Exp parseExp_bitXor() {
        Exp l = parseExp_bitAnd();
        return parseExp_bitXorPrime(l);
    }

    Exp parseExp_bitXorPrime(Exp e) {
        if(lookAhead.tokenType == TokenType.BITWISE_XOR_SYM) {
            Token anterior = match(TokenType.BITWISE_XOR_SYM);
            Exp r =parseExp_bitAnd();
            return parseExp_bitXorPrime(new BinExp(anterior.linha, anterior.coluna, e, r, "^"));
        } else { // prod vazia
            return e;
        }
    }

    Exp parseExp_bitAnd() {
        Exp l = parseExp_igualdade();
        return parseExp_bitAndPrime(l);
    }

    Exp parseExp_bitAndPrime(Exp e) {
        if(lookAhead.tokenType == TokenType.BITWSISE_AND_SYM) {
            Token ant = match(TokenType.BITWSISE_AND_SYM);
            Exp r = parseExp_igualdade();
            return parseExp_bitAndPrime(new BinExp(ant.linha, ant.coluna, e, r, "&"));
        } else { // prod vazia
            return e;
        }
    }

    Exp parseExp_igualdade() {
        Exp l = parseExp_rel();
        return parseExp_igualdadePrime(l);
    }

    Exp parseExp_igualdadePrime(Exp e) {
        if(lookAhead.tokenType == TokenType.EQ_SYM) {
            Token ant = match(TokenType.EQ_SYM);
            Exp r = parseExp_rel();
            return parseExp_igualdadePrime(new BinExp(ant.linha, ant.coluna, e, r, "=="));
        } else if (lookAhead.tokenType == TokenType.DIFF_SYM) {
            Token ant = match(TokenType.DIFF_SYM);
            Exp r = parseExp_rel();
            return parseExp_igualdadePrime(new BinExp(ant.linha, ant.coluna, e, r, "!="));
        }
        else { // prod vazia
            return e;
        }
    }

    Exp parseExp_rel() {
        Exp l = parseExp_Add();
        return parseExp_relPrime(l);
    }

    Exp parseExp_relPrime(Exp e) {
        if(lookAhead.tokenType == TokenType.GEQ_SYM) {
            Token anterior = match(TokenType.GEQ_SYM);
            Exp r = parseExp_Add();
            return parseExp_relPrime(new BinExp(anterior.linha, anterior.coluna, e, r, ">="));
        } else if (lookAhead.tokenType == TokenType.LEQ_SYM) {
            Token anterior = match(TokenType.LEQ_SYM);
            Exp r = parseExp_Add();
            return parseExp_relPrime(new BinExp(anterior.linha, anterior.coluna, e, r, "<="));
        } else if (lookAhead.tokenType == TokenType.LESS_SYM) {
            Token anterior = match(TokenType.LESS_SYM);
            Exp r = parseExp_Add();
            return parseExp_relPrime(new BinExp(anterior.linha, anterior.coluna, e, r, "<"));
        } else if (lookAhead.tokenType == TokenType.GREAT_SYM) {
            Token anterior = match(TokenType.GREAT_SYM);
            Exp r = parseExp_Add();
            return parseExp_relPrime(new BinExp(anterior.linha, anterior.coluna, e, r, ">"));
        }
        else { // prod vazia
            return e;
        }
    }

    Exp parseExp_Add() {
        Exp l = parseExp_mult();
        return parseExp_AddPrime(l);
    }

    Exp parseExp_AddPrime(Exp e) {
        if(lookAhead.tokenType == TokenType.PLUS_SYM) {
            Token anterior = match(TokenType.PLUS_SYM);
            Exp r = parseExp_mult();
            return parseExp_AddPrime(new BinExp(anterior.linha, anterior.coluna, e, r, "+"));
        } else if (lookAhead.tokenType == TokenType.MINUS_SYM) {
            Token anterior = match(TokenType.MINUS_SYM);
            Exp r = parseExp_mult();
            return parseExp_AddPrime(new BinExp(anterior.linha, anterior.coluna, e, r, "-"));
        } else if(lookAhead.tokenType == TokenType.MODULO_SYM) {
            Token anterior = match(TokenType.MODULO_SYM);
            Exp r = parseExp_mult();
            return parseExp_AddPrime(new BinExp(anterior.linha, anterior.coluna, e, r, "%"));
        }
        else { // prod vazia
            return e;
        }
    }

    Exp parseExp_mult() {
        Exp l = parseExp_Unaria();
        return parseExp_multPrime(l);
    }

    Exp parseExp_multPrime(Exp e) {
        if(lookAhead.tokenType == TokenType.MULT_SYM) {
            Token anterior = match(TokenType.MULT_SYM);
            Exp r = parseExp_Unaria();
            return parseExp_multPrime(new BinExp(anterior.linha, anterior.coluna, e, r, "*"));
        } else if (lookAhead.tokenType == TokenType.DIV_SYM) {
            Token anterior = match(TokenType.DIV_SYM);
            Exp r = parseExp_Unaria();
            return parseExp_multPrime(new BinExp(anterior.linha, anterior.coluna, e, r, "/"));
        }
        else { // prod vazia
            return e;
        }
    }

    Exp parseExp_Unaria() {
        if(lookAhead.tokenType == TokenType.LOGICAL_NOT_SYM) {
            Token anterior = match(TokenType.LOGICAL_NOT_SYM);
            Exp e = parseExp_Unaria();
            return new UnaryExp(anterior.linha, anterior.coluna, "!", e);
        } else if (firstPrimaria(lookAhead.tokenType)) {
            return parsePrimaria();
        } else {
            error("expressao mal formada");
            sync();
            return null;
        }
    }

    Exp parsePrimaria() {
        if(lookAhead.tokenType == TokenType.ABRE_PARENTESES) {
            match(TokenType.ABRE_PARENTESES);
            Exp e = parseExpression();
            match(TokenType.FECHA_PARENTESES);
            return e;
        } else if(lookAhead.tokenType == TokenType.ID) {
            return parseIdentificador();
        } else if(firstConstante(lookAhead.tokenType)) {
            return parseConstante();
        } else {
            error("expressao mal formada");
            sync();
            return null;
        }
    }

    IfDecl parseDecl_condicional() {
        if(lookAhead.tokenType == TokenType.IF_KEYWORD) {
            Token m = match(TokenType.IF_KEYWORD);
            match(TokenType.ABRE_PARENTESES);
            Exp e = parseExpression();
            match(TokenType.FECHA_PARENTESES);
            BlocoDecl bd = parseDeclBloco();
            ElseDecl ed = parseElse();
            return new IfDecl(m.linha, m.coluna, e, bd, ed);
        } else {
            error("declaracao condicional");
            sync();
            return null;
        }
    }

    ElseDecl parseElse() {
        if(lookAhead.tokenType == TokenType.ELSE_KEYWORD) {
            match(TokenType.ELSE_KEYWORD);
            return parseElsePrime();
        } else { // prod vazia
            return null;
        }
    }

    ElseDecl parseElsePrime() {
        if(lookAhead.tokenType == TokenType.ABRE_CHAVES) { // first de <decl_bloco>
            BlocoDecl bd = parseDeclBloco();
            return new ElseDecl(bd.getLine(), bd.getColumn(), null, bd, null);
        } else if (lookAhead.tokenType == TokenType.IF_KEYWORD) { // first de <decl_condicional>
            IfDecl id = parseDecl_condicional();
            return new ElseDecl(id.getLine(), id.getColumn(), id.getCondicao(), id.getThenDecl(), id);
        } else {
            error("else errado");
            sync();
            return null;
        }
    }

    Decl parseDeclLaco() {
        if(lookAhead.tokenType == TokenType.WHILE_KEYWORD) {
            Token m = match(TokenType.WHILE_KEYWORD);
            match(TokenType.ABRE_PARENTESES);
            Exp e = parseExpression();
            match(TokenType.FECHA_PARENTESES);
            BlocoDecl bd = parseDeclBloco();

            return new WhileDecl(m.linha, m.coluna, e, bd);
        } else if (lookAhead.tokenType == TokenType.FOR_KEYWORD) {
            Token m = match(TokenType.FOR_KEYWORD);
            match(TokenType.ABRE_PARENTESES);
            Decl d = parseDeclID();
            Exp e = parseExpression();
            match(TokenType.PONTO_E_VIRGULA);
            Exp ce = parseComando_exp();
            match(TokenType.FECHA_PARENTESES);
            BlocoDecl bd = parseDeclBloco();

            return new ForDecl(m.linha, m.coluna, d, e, ce, bd);
        } else {
            error("declaracao de laco (for ou while)");
            sync();
            return null;
        }
    }

    List<Decl> parseDeclaracao() {
        List<Decl> ld = new ArrayList<>();

        while(true) {
            if (firstEspecificacao_tipo(lookAhead.tokenType)) {
                ld.add(parseDeclID());
            } else if (lookAhead.tokenType == TokenType.RETURN_KEYWORD) {
                ld.add(parseDeclRetorno());
            } else if (firstExp_unaria(lookAhead.tokenType)) {
                ld.add(parseComando_exp());
            } else if (lookAhead.tokenType == TokenType.IF_KEYWORD) {
                ld.add(parseDecl_condicional());
            } else if (lookAhead.tokenType == TokenType.WHILE_KEYWORD || lookAhead.tokenType == TokenType.FOR_KEYWORD) {
                ld.add(parseDeclLaco());
            } else if (lookAhead.tokenType == TokenType.FECHA_CHAVES || lookAhead.tokenType == TokenType.EOF) { // prod vazia
                break;
            } else {
                error("declaração válida (tipo, retorno, atribuição, condição ou laço)");
                sync();
                break;
            }
        }
        return ld;
    }

    Exp parseComando_exp(){
        Exp l = parseExpression();
        Exp exp = parseComando_expPrime(l);
        match(TokenType.PONTO_E_VIRGULA);
        return exp;
    }

    Exp parseComando_expPrime(Exp l) {
        if(lookAhead.tokenType == TokenType.ATRIB_SYM) {
            match(TokenType.ATRIB_SYM);
            Exp r = parseExpression();
            return new AtribExp(lookAhead.linha, lookAhead.coluna, l, r, "=");
        } else if (lookAhead.tokenType == TokenType.PLUS_EQ_SYM) {
            match(TokenType.PLUS_EQ_SYM);
            Exp r = parseExpression();
            return new AtribExp(lookAhead.linha, lookAhead.coluna, l, r, "+=");

        } else if (lookAhead.tokenType == TokenType.MINUS_EQ_SYM) {
            match(TokenType.MINUS_EQ_SYM);
            Exp r = parseExpression();
            return new AtribExp(lookAhead.linha, lookAhead.coluna, l, r, "-=");

        } else if(lookAhead.tokenType == TokenType.MULT_EQ_SYM) {
            match(TokenType.MULT_EQ_SYM);
            Exp r = parseExpression();
            return new AtribExp(lookAhead.linha, lookAhead.coluna, l, r, "*=");

        } else if(lookAhead.tokenType == TokenType.DIV_EQ_SYM) {
            match(TokenType.DIV_EQ_SYM);
            Exp r = parseExpression();
            return new AtribExp(lookAhead.linha, lookAhead.coluna, l, r, "/=");

        } else { // prod vazia
            return l;
        }
    }

    List<ParamNo> parseParams() {
        List<ParamNo> lp = new ArrayList<>();
        if(firstEspecificacao_tipo(lookAhead.tokenType)) {
            Tipo t = parseEspecificador_Tipo();
            String lex = lookAhead.lexema;
            Exp ni = parseIdentificador();
            lp.add(new ParamNo(t.getLine(), t.getColumn(), lex, t));

            while(lookAhead.tokenType == TokenType.VIRGULA) {
                match(TokenType.VIRGULA);

                Tipo proxT = parseEspecificador_Tipo();
                lex = lookAhead.lexema;
                Exp proxId = parseIdentificador();
                lp.add(new ParamNo(proxT.getLine(), proxT.getColumn(), lex, proxT));
            }
        }
        return lp;
//        if(firstEspecificacao_tipo(lookAhead.tokenType)) {
//            parseLsParam(lp);
//            return lp;
//        } else { // prod vazia
//            return lp;
//        }
    }

//    void parseLsParam(List<ParamNo> lp){
//        if(firstEspecificacao_tipo(lookAhead.tokenType)) {
//            Tipo t = parseEspecificador_Tipo();
//            Exp ni = parseIdentificador();
//            parseLsParamPrime();
//        } else {
//            error("lista de parametros");
//            sync();
//        }
//    }
//
//    void parseLsParamPrime() {
//        if(lookAhead.tokenType == TokenType.VIRGULA) {
//            match(TokenType.VIRGULA);
//            parseLsParam();
//        } else { //prod vazia
//            return;
//        }
//    }

//    List<Exp> parseLsExpr(List<Exp> le){
//        if(firstExp_unaria(lookAhead.tokenType)) {
//            le.add(parseExpression());
//            return parseLsExprPrime(le);
//        } else {
//            error("lista de expressoes");
//            sync();
//            return null;
//        }
//    }
//
//    List<Exp> parseLsExprPrime(List<Exp> le){
//        if(lookAhead.tokenType == TokenType.VIRGULA) {
//            match(TokenType.VIRGULA);
//            parseLsExpr(le);
//            return le;
//        } else { // prod vazia
//            return le;
//        }
//    }

    List<Exp> parseLsExps() {
        List<Exp> le = new ArrayList<>();
        if(firstExp_unaria(lookAhead.tokenType)) {
            le.add(parseExpression());
            while(lookAhead.tokenType == TokenType.VIRGULA) {
                match(TokenType.VIRGULA);
                le.add(parseExpression());
            }
        }
        return le;
//        if(firstExp_unaria(lookAhead.tokenType)) {
//            parseLsExpr(le);
//            return le;
//        } else { // prod vazia
//            return le;
//        }
    }

    Decl parseDeclID(){
        if(firstEspecificacao_tipo(lookAhead.tokenType)) {
            Tipo t = parseEspecificador_Tipo();
            Token anterior = match(TokenType.ID);
            return parseDeclIDPrime(t, anterior);
        } else {
            error("declaração de identificador");
            sync();
            return null;
        }
    }

    Decl parseDeclIDPrime(Tipo tipo, Token token) {
        if(lookAhead.tokenType == TokenType.ABRE_PARENTESES) {
            match(TokenType.ABRE_PARENTESES);
            List<ParamNo> lp = parseParams();
            match(TokenType.FECHA_PARENTESES);
            BlocoDecl bd = parseDeclBloco();

            return new DeclFunc(bd.getLine(), bd.getColumn(), tipo, token.lexema, lp, bd);
        } else if(lookAhead.tokenType == TokenType.ATRIB_SYM) {
            match(TokenType.ATRIB_SYM);
            Exp e = parseExpression();
            match(TokenType.PONTO_E_VIRGULA);

            return new DeclVarLocal(token.linha, token.coluna, tipo, token.lexema, e);
        } else if (lookAhead.tokenType == TokenType.PONTO_E_VIRGULA) {
            match(TokenType.PONTO_E_VIRGULA);
            return new DeclVarLocal(token.linha, token.coluna, tipo, token.lexema, null);
        } else {
            error("declaracao de ID");
            sync();
            return null;
        }
    }

    List<Decl> parseDeclPrim(){
        List<Decl> ls = new ArrayList<>();
        while (firstEspecificacao_tipo(lookAhead.tokenType)) {
            Decl dcl = parseDeclID();
            ls.add(dcl);
        }
        return ls;
//        if(firstEspecificacao_tipo(lookAhead.tokenType)) {
//            parseDeclID();
//            parseDeclPrim();
//        } else { // prod vazia
//            return;
//        }
    }

    BlocoDecl parseDeclBloco(){
        if(lookAhead.tokenType == TokenType.ABRE_CHAVES) {
            match(TokenType.ABRE_CHAVES);
            List<Decl> ld = parseDeclaracao();
            Token anterior = match(TokenType.FECHA_CHAVES);
            return new BlocoDecl(anterior.linha, anterior.coluna, ld);
        } else {
            error("declaracao em bloco (esqueceu de fechar ou abrir chaves?)");
            sync();
            return null;
        }
    }


    ReturnDecl parseDeclRetorno(){
        if(lookAhead.tokenType == TokenType.RETURN_KEYWORD) {
            Token anterior = match(TokenType.RETURN_KEYWORD);
            ReturnDecl rd = new ReturnDecl(anterior.linha, anterior.coluna, null);
            return parseDeclRetornoPrime(rd);
        } else {
            error("return");
            sync();
            return null;
        }
    }

    ReturnDecl parseDeclRetornoPrime(ReturnDecl rd) {
        if(firstExp_unaria(lookAhead.tokenType)) {
            Exp e = parseExpression();
            match(TokenType.PONTO_E_VIRGULA);
            return new ReturnDecl(rd.getLine(), rd.getColumn(), e);
        } else if(lookAhead.tokenType == TokenType.PONTO_E_VIRGULA) {
            match(TokenType.PONTO_E_VIRGULA);
            return rd;
        } else {
            error("expressao ou ;");
            sync();
            return null;
        }
    }

    static void main() {
        String input = """
                @descobre se um numero eh primo\s
                bool ehPrimo(int x) {
                  if (x % 2 == 0) {
                    return false;
                  }
                  for (int i = 3; i < x; i += 2;) {
                    else if (x % i == 0) {
                      return false;
                    }
                  }
                  return true;
                }
                
                @imprimir primos ate 1000
                void main() {
                  int i = 1;
                  while (i <= 1000) {
                    if (ehPrimo(i)) {
                      print(i);
                      i += 1;
                    }
                  }
                }\s""";
        ParserLL1 pl1 = new ParserLL1(new LexScanner(input));
        pl1.parse$();
    }

}
