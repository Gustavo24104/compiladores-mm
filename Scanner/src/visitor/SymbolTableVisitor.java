package visitor;

import ast.*;
import symboltable.StackSymbolTable;
import symboltable.Symbol;
import symboltable.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class SymbolTableVisitor implements Visitor<Void> {
    private SymbolTable sst = new StackSymbolTable();
    ArrayList<String> erros = new ArrayList<>();

    public ArrayList<String> getErros() {
        return erros;
    }

    // passagem dupla: corpos de funções são analisados apenas após passar por todas as declarações
    // assim, funções mutuamente recursivas nao causam problemas.
    ArrayList<No> corposAVisitar = new ArrayList<>();
    ArrayList<List<ParamNo>> paramsAVisitar = new ArrayList<>();

    private void erroRedeclaracao(String id, int linhaAgora, int linhaAnterior) {
        erros.add("Erro na linha " + linhaAgora + "! " + id + " já foi declarado na linha " + linhaAnterior);
    }

    private void erroNaoDeclarado(String id, int linha) {
        erros.add("Erro na linha " + linha + "! " + id + " não foi declarado");
    }

    public SymbolTable getSymbolTable() {
        return sst;
    }

    public void printErrors() {
        if(erros.isEmpty()) {
            System.out.println("Passagem de símbolos terminada sem erros");
            return;
        }

        for (var e : erros) {
            System.out.println(e);
        }

    }

    @Override
    public Void visitBinExp(BinExp node) {
        if(node.getLeft() != null) node.getLeft().accept(this);
        if(node.getRight() != null) node.getRight().accept(this);
        return null;
    }

    @Override
    public Void visitUnaryExp(UnaryExp node) {
        if(node.getTerm() != null) node.getTerm().accept(this);
        return null;
    }

    @Override
    public Void visitConstExp(ConstExp node) {
        return null;
    }

    @Override
    public Void visitAtribExp(AtribExp node) {
        if (node.getId() != null) node.getId().accept(this);
        if(node.getValue() != null) node.getValue().accept(this);
        return null;
    }

    @Override
    public Void visitChamadaExp(ChamadaExp node) {
        if (node.getId() != null) node.getId().accept(this);
        for (var m : node.getArgs()) {
            m.accept(this);
        }
        return null;
    }

    @Override
    public Void visitDeclVarGlobal(DeclVarGlobal node) {
        assert sst.level() == 0;
        Symbol newSymbol = new Symbol(node.getId(), node, sst.level(), node.getType());
        if(!sst.declare(newSymbol)) { // redeclaracao
            erroRedeclaracao(node.getId(), node.getLine(),
                    sst.lookupLocal(node.getId()).getDeclNode().getLine());
        }
        return null;
    }

    @Override
    public Void visitDeclVarLocal(DeclVarLocal node) {
        Symbol newSymbol = new Symbol(node.getId(), node, sst.level(), node.getType());
        if(!sst.declare(newSymbol)) { // redeclaracao
            erroRedeclaracao(node.getId(), node.getLine(),
                    sst.lookupLocal(node.getId()).getDeclNode().getLine());
        }
        return null;
    }

    @Override
    public Void visitDeclFunc(DeclFunc node) {
        if(sst.level() != 0) {
            erros.add("Erro na linha " + node.getLine() + "! Funções aninhadas não são permitidas.");
        }

        Symbol newSymbol = new Symbol(node.getId(), node, sst.level(), node.getType());

        if(!sst.declare(newSymbol)) { // redeclaracao
            erroRedeclaracao(node.getId(), node.getLine(),
                    sst.lookupLocal(node.getId()).getDeclNode().getLine());
        }

        paramsAVisitar.add(node.getParams());
        corposAVisitar.add(node.getCorpo());
//        sst.openScope();
//        for (var m : node.getParams()) {
//            m.accept(this);
//        }
//        if(node.getCorpo() != null) node.getCorpo().accept(this);
//        sst.closeScope();
        return null;

    }

    @Override
    public Void visitIfDecl(IfDecl node) {
        if (node.getCondicao() != null) node.getCondicao().accept(this);

        if (node.getElseDecl() != null) {
            node.getElseDecl().accept(this);
        }

        if (node.getThenDecl() != null) {
            sst.openScope();
            node.getThenDecl().accept(this);
            sst.closeScope();
        }
        return null;
    }

    @Override
    public Void visitElseDecl(ElseDecl node) {
        if(node.getCondicao() != null) node.getCondicao().accept(this);
        if(node.getIfDecl() != null) node.getIfDecl().accept(this);
        if(node.getCorpo() != null) {
            sst.openScope();
            node.getCorpo().accept(this);
            sst.closeScope();
        }
        return null;
    }

    @Override
    public Void visitWhileDecl(WhileDecl node) {
        if (node.getCondicao() != null) node.getCondicao().accept(this);

        if (node.getCorpo() != null) {
            sst.openScope();
            node.getCorpo().accept(this);
            sst.closeScope();
        }
        return null;
    }

    @Override
    public Void visitForDecl(ForDecl node) {
        if (node.getInit() != null) {
            sst.openScope();
            node.getInit().accept(this);
        }
        if (node.getCondicao() != null) node.getCondicao().accept(this);
        if (node.getAtrib() != null) node.getAtrib().accept(this);
        if (node.getCorpo() != null) {
            node.getCorpo().accept(this);
            sst.closeScope();
        }
        return null;
    }

    // nao abre nem fecha escopo pq isso fica pros outros métodos (por causa de função)
    @Override
    public Void visitBlocoDecl(BlocoDecl node) {
//        sst.openScope();
        for (var b : node.getDeclaracoes()) {
            b.accept(this);
        }
//        sst.closeScope();
        return null;
    }

    @Override
    public Void visitReturnDecl(ReturnDecl node) {
        if (node.getExp() != null) node.getExp().accept(this);
        return null;
    }

    @Override
    public Void visitComandoExp(ComandoExp node) {
        if (node.getId() != null) node.getId().accept(this);
        if (node.getValue() != null) node.getValue().accept(this);
        return null;
    }

    @Override
    public Void visitTipo(Tipo node) {
        return null;
    }

    @Override
    public Void visitNoIdentificador(NoIdentificador node) {
        node.setDecl(sst.lookup(node.getName()));
        if(node.getDecl() == null) {
            erroNaoDeclarado(node.getName(), node.getLine());
        }
        return null;
    }

    @Override
    public Void visitParamNo(ParamNo node) {
        Symbol newSymbol = new Symbol(node.getId(), node, sst.level(), node.getType());
        if(!sst.declare(newSymbol)) { // redeclaracao
            erroRedeclaracao(node.getId(), node.getLine(),
                    sst.lookupLocal(node.getId()).getDeclNode().getLine());
        }
        return null;
    }

    @Override
    public Void visitNoInicial(NoInicial node) {

        // na primeira passagem não olhamos corpo de função, apenas declaracoes e vai salvando numa lista
        for(var d : node.getDecls()) {
            d.accept(this);
        }

        // na segunda passagem agr checamos os corpos das funções
        for (int i = 0; i < corposAVisitar.size(); i++) {
            sst.openScope();
            for (var m : paramsAVisitar.get(i)) {
                m.accept(this);
            }
            corposAVisitar.get(i).accept(this);
            sst.closeScope();
        }

        return null;
    }
}
