package visitor;

import ast.*;
import symboltable.Symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeCheckerVisitor implements Visitor<Tipo> {
    private static final Tipo INT = new Tipo(0, 0, "int");
    private static final Tipo FLOAT = new Tipo(0, 0, "float");
    private static final Tipo STRING = new Tipo(0, 0, "string");
    private static final Tipo BOOL = new Tipo(0, 0, "bool");
    private static final Tipo ERRO = new Tipo(0, 0, "erro");

    private final List<String> erros = new ArrayList<>();
    private final List<String> avisos = new ArrayList<>();
    private Set<No> comValor = new HashSet<>();
    private Tipo retornoAtual;
    private boolean retorna;
    private boolean escrita;
    private NoIdentificador ultimoId;

    public List<String> getErros() { return erros; }
    public List<String> getAvisos() { return avisos; }

    public void printMensagens() {
        erros.forEach(System.out::println);
        avisos.forEach(System.out::println);
        if (erros.isEmpty()) System.out.println("Verificação de tipos terminada sem erros");
    }

    private void erro(No n, String msg) {
        erros.add("linha " + n.getLine() + ": " + msg);
    }

    private static boolean is(Tipo t, String nome) { return t.getTipo().equals(nome); }
    private static boolean isErro(Tipo t) { return t == ERRO; }
    private static boolean isNum(Tipo t) { return is(t, "int") || is(t, "float"); }

    private static boolean compativel(Tipo destino, Tipo origem) {
        return destino.getTipo().equals(origem.getTipo()) || (is(destino, "float") && is(origem, "int"));
    }

    private static String nome(Tipo t) { return t.getTipo(); }

    private Tipo marca(Exp n, Tipo t) {
        n.setTipo(t);
        return t;
    }

    private Tipo expr(Exp e) {
        if (e == null) return ERRO;
        Tipo t = e.accept(this);
        if (t != null && is(t, "void")) {
            erro(e, "expressão do tipo void não pode ser usada como valor");
            return marca(e, ERRO);
        }
        return t == null ? ERRO : t;
    }

    private void visita(No n) {
        if (n != null) n.accept(this);
    }

    private boolean ramo(No n) {
        retorna = false;
        visita(n);
        return retorna;
    }

    private void condicao(String comando, Exp e) {
        Tipo t = expr(e);
        if (!isErro(t) && !is(t, "bool")) {
            erro(e, "condição de '" + comando + "' deve ser bool, mas é " + nome(t));
        }
    }

    private Tipo aritmetico(Tipo l, Tipo r) {
        if (is(l, "int") && is(r, "int")) return INT;
        return isNum(l) && isNum(r) ? FLOAT : ERRO;
    }

    private Tipo resultadoBinario(String op, Tipo l, Tipo r) {
        switch (op) {
            case "+":
                if (is(l, "string") || is(r, "string")) return STRING;
                return aritmetico(l, r);
            case "-": case "*": case "/": case "^": case "&": case "|":
                return aritmetico(l, r);
            case "%":
                return is(l, "int") && is(r, "int") ? INT : ERRO;
            case "<": case "<=": case ">": case ">=":
                return (isNum(l) && isNum(r)) || (is(l, "string") && is(r, "string")) ? BOOL : ERRO;
            case "==": case "!=":
                return (isNum(l) && isNum(r)) || (is(l, r.getTipo()) && !is(l, "void")) ? BOOL : ERRO;
            case "&&": case "||":
                return is(l, "bool") && is(r, "bool") ? BOOL : ERRO;
            default:
                return ERRO;
        }
    }

    @Override
    public Tipo visitBinExp(BinExp node) {
        Tipo l = expr(node.getLeft());
        Tipo r = expr(node.getRight());
        if (isErro(l) || isErro(r)) return marca(node, ERRO);
        Tipo res = resultadoBinario(node.getOp(), l, r);
        if (isErro(res)) {
            erro(node, "operador '" + node.getOp() + "' não se aplica a " + nome(l) + " e " + nome(r));
        }
        return marca(node, res);
    }

    @Override
    public Tipo visitUnaryExp(UnaryExp node) {
        Tipo t = expr(node.getTerm());
        if (isErro(t)) return marca(node, ERRO);
        if (node.getOp().equals("!") && is(t, "bool")) return marca(node, BOOL);
        erro(node, "operador '" + node.getOp() + "' não se aplica a " + nome(t));
        return marca(node, ERRO);
    }

    @Override
    public Tipo visitConstExp(ConstExp node) {
        return marca(node, node.getType() == null ? ERRO : node.getType());
    }

    @Override
    public Tipo visitAtribExp(AtribExp node) {
        Tipo v = expr(node.getValue());
        boolean simples = node.getOp().equals("=");
        ultimoId = null;
        escrita = simples;
        Tipo a = node.getId() == null ? ERRO : node.getId().accept(this);
        escrita = false;
        if (node.getId() == null || ultimoId != node.getId()) {
            erro(node, "lado esquerdo da atribuição deve ser uma variável");
            return marca(node, ERRO);
        }
        Symbol s = ultimoId.getDecl();
        if (s != null && !s.isFuncao()) comValor.add(s.getDeclNode());
        if (isErro(a) || isErro(v)) return marca(node, ERRO);
        boolean ok;
        if (simples) {
            ok = compativel(a, v);
        } else if (is(a, "int")) {
            ok = is(v, "int");
        } else {
            ok = is(a, "float") && isNum(v);
        }
        if (!ok) {
            erro(node, "operador '" + node.getOp() + "' não se aplica a " + nome(a) + " e " + nome(v));
            return marca(node, ERRO);
        }
        return marca(node, a);
    }

    @Override
    public Tipo visitChamadaExp(ChamadaExp node) {
        NoIdentificador id = node.getId();
        List<Tipo> args = new ArrayList<>();
        for (Exp e : node.getArgs()) args.add(expr(e));
        Symbol s = id.getDecl();
        if (s == null) return marca(node, ERRO);
        if (!s.isFuncao()) {
            erro(node, "'" + id.getName() + "' não é uma função");
            return marca(node, ERRO);
        }
        List<Tipo> params = s.getParamTypes();
        if (params.size() != args.size()) {
            erro(node, "'" + id.getName() + "' espera " + params.size() + " argumento(s), recebeu " + args.size());
        } else {
            for (int i = 0; i < args.size(); i++) {
                Tipo a = args.get(i);
                if (!isErro(a) && !compativel(params.get(i), a)) {
                    erro(node, "argumento " + (i + 1) + " de '" + id.getName() + "': esperado "
                            + nome(params.get(i)) + ", veio " + nome(a));
                }
            }
        }
        return marca(node, s.getType());
    }

    @Override
    public Tipo visitNoIdentificador(NoIdentificador node) {
        ultimoId = node;
        boolean alvo = escrita;
        escrita = false;
        Symbol s = node.getDecl();
        if (s == null) return marca(node, ERRO);
        if (s.isFuncao()) {
            erro(node, "'" + node.getName() + "' é uma função e deve ser chamada com parênteses");
            return marca(node, ERRO);
        }
        if (!alvo && !comValor.contains(s.getDeclNode())) {
            avisos.add("linha " + node.getLine() + ": aviso: variável '" + node.getName() + "' pode ser usada sem valor");
            comValor.add(s.getDeclNode());
        }
        return marca(node, s.getType() == null ? ERRO : s.getType());
    }

    private void declaraVar(No node, Tipo tipo, String id, Exp inicializador) {
        if (inicializador != null) {
            Tipo v = expr(inicializador);
            if (!isErro(v) && !compativel(tipo, v)) {
                erro(node, "inicialização de '" + id + "': esperado " + nome(tipo) + ", veio " + nome(v));
            }
        }
        if (inicializador != null || retornoAtual == null) comValor.add(node);
    }

    @Override
    public Tipo visitDeclVarGlobal(DeclVarGlobal node) {
        declaraVar(node, node.getType(), node.getId(), node.getAtrib());
        return null;
    }

    @Override
    public Tipo visitDeclVarLocal(DeclVarLocal node) {
        declaraVar(node, node.getType(), node.getId(), node.getAtrib());
        return null;
    }

    @Override
    public Tipo visitParamNo(ParamNo node) {
        comValor.add(node);
        return null;
    }

    @Override
    public Tipo visitDeclFunc(DeclFunc node) {
        Tipo retornoAnterior = retornoAtual;
        retornoAtual = node.getType();
        node.getParams().forEach(p -> p.accept(this));
        retorna = false;
        visita(node.getCorpo());
        if (!is(node.getType(), "void") && !retorna) {
            erro(node, "função '" + node.getId() + "' pode terminar sem retornar valor");
        }
        retornoAtual = retornoAnterior;
        retorna = false;
        return null;
    }

    @Override
    public Tipo visitReturnDecl(ReturnDecl node) {
        Tipo v = node.getExp() == null ? null : expr(node.getExp());
        if (retornoAtual == null) {
            erro(node, "return fora de função");
        } else if (is(retornoAtual, "void")) {
            if (v != null) erro(node, "função void não pode retornar valor");
        } else if (v == null) {
            erro(node, "return sem valor em função que retorna " + nome(retornoAtual));
        } else if (!isErro(v) && !compativel(retornoAtual, v)) {
            erro(node, "return: esperado " + nome(retornoAtual) + ", veio " + nome(v));
        }
        retorna = true;
        return null;
    }

    @Override
    public Tipo visitIfDecl(IfDecl node) {
        condicao("if", node.getCondicao());
        Set<No> antes = new HashSet<>(comValor);
        boolean rThen = ramo(node.getThenDecl());
        Set<No> aposThen = comValor;
        comValor = new HashSet<>(antes);
        boolean rElse = node.getElseDecl() != null && ramo(node.getElseDecl());
        Set<No> aposElse = comValor;
        if (rThen && rElse) {
            aposThen.addAll(aposElse);
            comValor = aposThen;
        } else if (rThen) {
            comValor = aposElse;
        } else if (rElse) {
            comValor = aposThen;
        } else {
            aposThen.retainAll(aposElse);
            comValor = aposThen;
        }
        retorna = rThen && rElse;
        return null;
    }

    @Override
    public Tipo visitElseDecl(ElseDecl node) {
        retorna = false;
        if (node.getCorpo() != null) visita(node.getCorpo());
        else visita(node.getIfDecl());
        return null;
    }

    @Override
    public Tipo visitWhileDecl(WhileDecl node) {
        condicao("while", node.getCondicao());
        Set<No> antes = new HashSet<>(comValor);
        ramo(node.getCorpo());
        comValor = antes;
        retorna = false;
        return null;
    }

    @Override
    public Tipo visitForDecl(ForDecl node) {
        visita(node.getInit());
        condicao("for", node.getCondicao());
        visita(node.getAtrib());
        Set<No> antes = new HashSet<>(comValor);
        ramo(node.getCorpo());
        comValor = antes;
        retorna = false;
        return null;
    }

    @Override
    public Tipo visitBlocoDecl(BlocoDecl node) {
        boolean r = false;
        for (Decl d : node.getDeclaracoes()) {
            r |= ramo(d);
        }
        retorna = r;
        return null;
    }

    @Override
    public Tipo visitComandoExp(ComandoExp node) {
        Tipo t = node.getId() == null ? ERRO : node.getId().accept(this);
        return marca(node, t == null ? ERRO : t);
    }

    @Override
    public Tipo visitTipo(Tipo node) {
        return null;
    }

    @Override
    public Tipo visitNoInicial(NoInicial node) {
        node.getDecls().forEach(this::visita);
        return null;
    }
}
