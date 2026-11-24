package test;

import ast.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import symboltable.Symbol;
import visitor.TypeCheckerVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class TabelaOperadoresTest {
    private static final String[][] NUM_NUM = {{"int", "int"}, {"int", "float"}, {"float", "int"}, {"float", "float"}};
    private static final String[][] COM_FLOAT = {{"int", "float"}, {"float", "int"}, {"float", "float"}};
    private static final String[][] TODOS = {
            {"int", "int"}, {"int", "float"}, {"int", "string"}, {"int", "bool"},
            {"float", "int"}, {"float", "float"}, {"float", "string"}, {"float", "bool"},
            {"string", "int"}, {"string", "float"}, {"string", "string"}, {"string", "bool"},
            {"bool", "int"}, {"bool", "float"}, {"bool", "string"}, {"bool", "bool"}};

    private final List<DynamicTest> testes = new ArrayList<>();

    private static Tipo t(String nome) { return new Tipo(1, 1, nome); }

    private static Exp c(String tipo) { return new ConstExp(1, 1, null, t(tipo)); }

    private static String resultado(TypeCheckerVisitor v, Tipo t) {
        return v.getErros().isEmpty() ? t.getTipo() : "erro";
    }

    private static String aceitaOuErro(TypeCheckerVisitor v) {
        return v.getErros().isEmpty() ? "ok" : "erro";
    }

    private static String binario(String op, String l, String r) {
        TypeCheckerVisitor v = new TypeCheckerVisitor();
        return resultado(v, new BinExp(1, 1, c(l), c(r), op).accept(v));
    }

    private static String unario(String op, String tipo) {
        TypeCheckerVisitor v = new TypeCheckerVisitor();
        return resultado(v, new UnaryExp(1, 1, op, c(tipo)).accept(v));
    }

    private static String atribuicao(String op, String alvo, String valor) {
        NoIdentificador id = new NoIdentificador(1, 1, "x");
        id.setDecl(new Symbol("x", new DeclVarLocal(1, 1, t(alvo), "x", null), 0, t(alvo)));
        TypeCheckerVisitor v = new TypeCheckerVisitor();
        new AtribExp(1, 1, id, c(valor), op).accept(v);
        return aceitaOuErro(v);
    }

    private void caso(String nome, String esperado, Supplier<String> atual) {
        testes.add(dynamicTest(nome + " -> " + esperado, () -> assertEquals(esperado, atual.get())));
    }

    private void binarios(String[] ops, String[][] pares, String esperado) {
        for (String op : ops)
            for (String[] p : pares)
                caso(p[0] + " " + op + " " + p[1], esperado, () -> binario(op, p[0], p[1]));
    }

    private void atribuicoes(String[] ops, String[][] pares, String esperado) {
        for (String op : ops)
            for (String[] p : pares)
                caso(p[0] + " " + op + " " + p[1], esperado, () -> atribuicao(op, p[0], p[1]));
    }

    private static String[][] pares(String[][] base, String[][] excluir) {
        List<String[]> r = new ArrayList<>();
        for (String[] p : base) {
            boolean exclui = false;
            for (String[] e : excluir) exclui |= p[0].equals(e[0]) && p[1].equals(e[1]);
            if (!exclui) r.add(p);
        }
        return r.toArray(new String[0][]);
    }

    @TestFactory
    List<DynamicTest> linhasDaTabelaDeOperadores() {
        String[][] intInt = {{"int", "int"}};
        String[][] concat = {{"string", "string"}, {"string", "int"}, {"string", "float"}, {"string", "bool"},
                {"int", "string"}, {"float", "string"}, {"bool", "string"}};
        String[][] relOk = {{"int", "int"}, {"int", "float"}, {"float", "int"}, {"float", "float"}, {"string", "string"}};
        String[][] boolBool = {{"bool", "bool"}};

        binarios(new String[]{"+", "-", "*", "/", "%", "&", "|", "^"}, intInt, "int");
        binarios(new String[]{"+", "-", "*", "/", "&", "|", "^"}, COM_FLOAT, "float");
        binarios(new String[]{"+"}, concat, "string");
        binarios(new String[]{"<", "<=", ">", ">=", "==", "!="}, relOk, "bool");
        binarios(new String[]{"==", "!="}, boolBool, "bool");
        binarios(new String[]{"&&", "||"}, boolBool, "bool");

        binarios(new String[]{"%"}, pares(TODOS, intInt), "erro");
        binarios(new String[]{"-", "*", "/", "&", "|", "^"}, pares(TODOS, NUM_NUM), "erro");
        binarios(new String[]{"+"}, pares(pares(TODOS, NUM_NUM), concat), "erro");
        binarios(new String[]{"<", "<=", ">", ">="}, pares(TODOS, relOk), "erro");
        binarios(new String[]{"==", "!="}, pares(pares(TODOS, relOk), boolBool), "erro");
        binarios(new String[]{"&&", "||"}, pares(TODOS, boolBool), "erro");

        caso("! bool", "bool", () -> unario("!", "bool"));
        for (String tipo : new String[]{"int", "float", "string"}) caso("! " + tipo, "erro", () -> unario("!", tipo));

        String[] compostos = {"+=", "-=", "*=", "/="};
        String[][] atribSimples = {{"int", "int"}, {"float", "int"}, {"float", "float"}, {"string", "string"}, {"bool", "bool"}};
        String[][] atribComposta = {{"int", "int"}, {"float", "int"}, {"float", "float"}};
        atribuicoes(new String[]{"="}, atribSimples, "ok");
        atribuicoes(compostos, atribComposta, "ok");
        atribuicoes(new String[]{"="}, pares(TODOS, atribSimples), "erro");
        atribuicoes(compostos, pares(TODOS, atribComposta), "erro");
        return testes;
    }
}
