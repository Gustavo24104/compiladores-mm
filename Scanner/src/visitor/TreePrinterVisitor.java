package visitor;
import ast.*;

public class TreePrinterVisitor implements Visitor<Void> {
    private StringBuilder sb = new StringBuilder();
    private int indentLevel = 0;

    private void printIndent() {
        for (int i = 0; i < indentLevel; i++) {
            sb.append("   "); // 3 espaços de indentação por nível
        }
    }

    @Override
    public Void visitBinExp(BinExp node) {
        printIndent();
        sb.append("BinExp(op=").append(node.getOp()).append(")\n");
        indentLevel++;
        if (node.getLeft() != null) node.getLeft().accept(this);
        if (node.getRight() != null) node.getRight().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitUnaryExp(UnaryExp node) {
        printIndent();
        sb.append("UnaryExp(op=").append(node.getOp()).append(")\n");
        indentLevel++;
        if (node.getTerm() != null) node.getTerm().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitConstExp(ConstExp node) {
        printIndent();
        sb.append("ConstExp(value=").append(node.getValue()).append(")\n");
        if (node.getType() != null) {
            indentLevel++;
            node.getType().accept(this);
            indentLevel--;
        }
        return null;
    }

    @Override
    public Void visitAtribExp(AtribExp node) {
        printIndent();
        sb.append("AtribExp(op=").append(node.getOp()).append(")\n");
        indentLevel++;
        if (node.getId() != null) node.getId().accept(this);
        if (node.getValue() != null) node.getValue().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitChamadaExp(ChamadaExp node) {
        printIndent();
        sb.append("ChamadaExp\n");
        indentLevel++;
        if (node.getId() != null) node.getId().accept(this);
        if (node.getArgs() != null) {
            for (Exp arg : node.getArgs()) {
                if (arg != null) arg.accept(this);
            }
        }
        indentLevel--;
        return null;
    }

    @Override
    public Void visitDeclVarGlobal(DeclVarGlobal node) {
        printIndent();
        sb.append("DeclVarGlobal(id=").append(node.getId()).append(")\n");
        indentLevel++;
        if (node.getType() != null) node.getType().accept(this);
        if (node.getAtrib() != null) node.getAtrib().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitDeclVarLocal(DeclVarLocal node) {
        printIndent();
        sb.append("DeclVarLocal(id=").append(node.getId()).append(")\n");
        indentLevel++;
        if (node.getType() != null) node.getType().accept(this);
        if (node.getAtrib() != null) node.getAtrib().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitDeclFunc(DeclFunc node) {
        printIndent();
        sb.append("DeclFunc(id=").append(node.getId()).append(")\n");
        indentLevel++;
        if (node.getType() != null) node.getType().accept(this);
        if (node.getParams() != null) {
            for (ParamNo p : node.getParams()) {
                if (p != null) p.accept(this);
            }
        }
        if (node.getCorpo() != null) node.getCorpo().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitIfDecl(IfDecl node) {
        printIndent();
        sb.append("IfDecl\n");
        indentLevel++;
        if (node.getCondicao() != null) node.getCondicao().accept(this);
        if (node.getThenDecl() != null) node.getThenDecl().accept(this);
        if (node.getElseDecl() != null) node.getElseDecl().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitWhileDecl(WhileDecl node) {
        printIndent();
        sb.append("WhileDecl\n");
        indentLevel++;
        if (node.getCondicao() != null) node.getCondicao().accept(this);
        if (node.getCorpo() != null) node.getCorpo().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitForDecl(ForDecl node) {
        printIndent();
        sb.append("ForDecl\n");
        indentLevel++;
        if (node.getInit() != null) node.getInit().accept(this);
        if (node.getCondicao() != null) node.getCondicao().accept(this);
        if (node.getAtrib() != null) node.getAtrib().accept(this);
        if (node.getCorpo() != null) node.getCorpo().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitBlocoDecl(BlocoDecl node) {
        printIndent();
        sb.append("BlocoDecl\n");
        indentLevel++;
        if (node.getDeclaracoes() != null) {
            for (No d : node.getDeclaracoes()) {
                if (d != null) d.accept(this);
            }
        }
        indentLevel--;
        return null;
    }

    @Override
    public Void visitReturnDecl(ReturnDecl node) {
        printIndent();
        sb.append("ReturnDecl\n");
        indentLevel++;
        if (node.getExp() != null) node.getExp().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitComandoExp(ComandoExp node) {
        printIndent();
        String op = node.getOp() != null ? node.getOp() : "";
        sb.append("ComandoExp").append("(").append(op).append(")\n");
        indentLevel++;
        if (node.getId() != null) node.getId().accept(this);
        if (node.getValue() != null) node.getValue().accept(this);
        indentLevel--;
        return null;
    }

    @Override
    public Void visitTipo(Tipo node) {
        printIndent();
        sb.append("Tipo(").append(node.getTipo()).append(")\n");
        return null;
    }

    @Override
    public Void visitNoIdentificador(NoIdentificador node) {
        printIndent();
        sb.append("NoIdentificador(").append(node.getName()).append(")\n");
        return null;
    }

    @Override
    public Void visitParamNo(ParamNo node) {
        printIndent();
        sb.append("ParamNo(").append(node.getName()).append(")\n");
        if (node.getType() != null) {
            indentLevel++;
            node.getType().accept(this);
            indentLevel--;
        }
        return null;
    }

    @Override
    public Void visitNoInicial(NoInicial node) {
        printIndent();
        sb.append("NoInicial\n");
        indentLevel++;
        if (node.getDecls() != null) {
            for (Decl d : node.getDecls()) {
                if (d != null) d.accept(this);
            }
        }
        indentLevel--;
        return null;
    }

    @Override
    public Void visitElseDecl(ElseDecl node) {
        printIndent();
        sb.append("ElseDecl\n");
        indentLevel++;
        if (node.getCorpo() != null) {
            node.getCorpo().accept(this);
        } if (node.getIfDecl() != null) {
            node.getIfDecl().accept(this);
        }
        indentLevel--;
        return null;
    }

    public String getResult() {
        return sb.toString();
    }
}