package visitor;

public interface Visitor<T> {

    T visitBinExp(BinExp node);
    T visitUnaryExp(UnaryExp node);
    T visitConstExp(ConstExp node);
    T visitAtribExp(AtribExp node);
    T visitChamadaExp(ChamadaExp node);
    T visitDeclVarGlobal(DeclVarGlobal node);
    T visitDeclVarLocal(DeclVarLocal node);
    T visitDeclFunc(DeclFunc node);
    T visitIfDecl(IfDecl node);
    T visitWhileDecl(WhileDecl node);
    T visitForDecl(ForDecl node);
    T visitBlocoDecl(BlocoDecl node);
    T visitReturnDecl(ReturnDecl node);
    T visitComandoExp(ComandoExp node);
    T visitTipo(Tipo node);
    T visitNoIdentificador(NoIdentificador node);
    T visitParamNo(ParamNo node);
    T visitNoInicial(NoInicial node);


}
