package test;

import ast.*;
import visitor.*;
import java.util.ArrayList;

public class ASTtest {
    static void main(String[] args) {
        System.out.println("iniciando testes\n");

        Tipo tipoInt = new Tipo(1, 1, "int");

        NoIdentificador idA = new NoIdentificador(1, 11, "a");
        NoIdentificador idB = new NoIdentificador(1, 15, "b");

        BinExp soma = new BinExp(1, 11, idA, idB, "+");
        DeclVarGlobal declGlobal = new DeclVarGlobal(1, 1, tipoInt, "x", soma);

        ArrayList<Decl> listaDecls = new ArrayList<>();
        listaDecls.add(declGlobal);
        NoInicial raiz = new NoInicial(1, 1, listaDecls);


        System.out.println("TreePrinterVisitor:\n");
        TreePrinterVisitor treePrinter = new TreePrinterVisitor();
        raiz.accept(treePrinter);
        System.out.print(treePrinter.getResult());

        System.out.println("\nSourcePrinterVisitor:\n");
        SourcePrinterVisitor sourcePrinter = new SourcePrinterVisitor();
        String codigoReimpresso = raiz.accept(sourcePrinter);
        System.out.println(codigoReimpresso);
    }
}