package visitor;
import ast.*;

public class SourcePrinterVisitor implements Visitor<String> {

    @Override
    public String visitBinExp(BinExp node) {
        String left = node.getLeft() != null ? node.getLeft().accept(this) : "";
        String right = node.getRight() != null ? node.getRight().accept(this) : "";
        return "(" + left + " " + node.getOp() + " " + right + ")";
    }

    @Override
    public String visitUnaryExp(UnaryExp node) {
        String term = node.getTerm() != null ? node.getTerm().accept(this) : "";
        return node.getOp() + term;
    }

    @Override
    public String visitConstExp(ConstExp node) {
        return node.getValue() != null ? node.getValue().toString() : "";
    }

    @Override
    public String visitAtribExp(AtribExp node) {
        String id = node.getId() != null ? node.getId().accept(this) : "";
        String val = node.getValue() != null ? node.getValue().accept(this) : "";
        return id +  " " + node.getOp() + " " + val + ";";
    }

    @Override
    public String visitChamadaExp(ChamadaExp node) {
        String id = node.getId() != null ? node.getId().accept(this) : "";
        StringBuilder argsStr = new StringBuilder();
        if (node.getArgs() != null) {
            for (int i = 0; i < node.getArgs().size(); i++) {
                argsStr.append(node.getArgs().get(i).accept(this));
                if (i < node.getArgs().size() - 1) {
                    argsStr.append(", ");
                }
            }
        }
        return id + "(" + argsStr.toString() + ")";
    }

    @Override
    public String visitDeclVarGlobal(DeclVarGlobal node) {
        String type = node.getType() != null ? node.getType().accept(this) : "";
        String atrib = node.getAtrib() != null ? " = " + node.getAtrib().accept(this) : "";
        return type + " " + node.getId() + atrib + ";";
    }

    @Override
    public String visitDeclVarLocal(DeclVarLocal node) {
        String type = node.getType() != null ? node.getType().accept(this) : "";
        String atrib = node.getAtrib() != null ? " = " + node.getAtrib().accept(this) : "";
        return type + " " + node.getId() + atrib + ";";
    }

    @Override
    public String visitDeclFunc(DeclFunc node) {
        String type = node.getType() != null ? node.getType().accept(this) : "";
        StringBuilder paramsStr = new StringBuilder();
        if (node.getParams() != null) {
            for (int i = 0; i < node.getParams().size(); i++) {
                paramsStr.append(node.getParams().get(i).accept(this));
                if (i < node.getParams().size() - 1) {
                    paramsStr.append(", ");
                }
            }
        }
        String corpo = node.getCorpo() != null ? node.getCorpo().accept(this) : "{}";
        return type + " " + node.getId() + "(" + paramsStr.toString() + ") " + corpo;
    }

    @Override
    public String visitIfDecl(IfDecl node) {
        String cond = node.getCondicao() != null ? node.getCondicao().accept(this) : "";
        String thenD = node.getThenDecl() != null ? node.getThenDecl().accept(this) : "";
        String elseD = node.getElseDecl() != null ? " else " + node.getElseDecl().accept(this) : "";
        return "if (" + cond + ") " + thenD + elseD;
    }

    @Override
    public String visitWhileDecl(WhileDecl node) {
        String cond = node.getCondicao() != null ? node.getCondicao().accept(this) : "";
        String corpo = node.getCorpo() != null ? node.getCorpo().accept(this) : "";
        return "while (" + cond + ") " + corpo;
    }

    @Override
    public String visitForDecl(ForDecl node) {
        String init = node.getInit() != null ? node.getInit().accept(this) : "";
        String cond = node.getCondicao() != null ? node.getCondicao().accept(this) : "";
        String atrib = node.getAtrib() != null ? node.getAtrib().accept(this) : "";
        String corpo = node.getCorpo() != null ? node.getCorpo().accept(this) : "";
        return "for (" + init + " " + cond + "; " + atrib + ") " + corpo;
    }

    @Override
    public String visitBlocoDecl(BlocoDecl node) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        if (node.getDeclaracoes() != null) {
            for (No d : node.getDeclaracoes()) {
                if (d != null) {
                    sb.append("   ").append(d.accept(this)).append("\n");
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String visitReturnDecl(ReturnDecl node) {
        String exp = node.getExp() != null ? node.getExp().accept(this) : "";
        return "return " + exp + ";";
    }

    @Override
    public String visitComandoExp(ComandoExp node) {
        String id = node.getId() != null ? node.getId().accept(this) : "";
        String val = node.getValue() != null ? node.getValue().accept(this) : "";
        String op = node.getOp() != null ? node.getOp() : "";
        return id + op + val + ";";
    }

    @Override
    public String visitTipo(Tipo node) {
        return node.getTipo() != null ? node.getTipo() : "";
    }

    @Override
    public String visitNoIdentificador(NoIdentificador node) {
        return node.getName() != null ? node.getName() : "";
    }

    @Override
    public String visitParamNo(ParamNo node) {
        String type = node.getType() != null ? node.getType().accept(this) : "";
        return type + " " + node.getName();
    }

    @Override
    public String visitNoInicial(NoInicial node) {
        StringBuilder sb = new StringBuilder();
        if (node.getDecls() != null) {
            for (Decl d : node.getDecls()) {
                if (d != null) {
                    sb.append(d.accept(this)).append("\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String visitElseDecl(ElseDecl node) {
        if(node.getIfDecl() != null) {
            return node.getIfDecl().accept(this);
        }
        else if (node.getCorpo() != null) {
            return node.getCorpo().accept(this);
        }
        return "";
    }
}