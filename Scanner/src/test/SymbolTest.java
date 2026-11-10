package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import symboltable.StackSymbolTable;
import symboltable.Symbol;

import static org.junit.jupiter.api.Assertions.*;


public class SymbolTest {
    private StackSymbolTable table;

    @BeforeEach
    void setUp() {
        table = new StackSymbolTable();
    }

    @Test
    void testDeclararEBuscar() {
        // Testa a declaração de um símbolo e a busca com lookup
        Symbol s = new Symbol("x", null, 0, null);
        assertTrue(table.declare(s), "Deveria declarar com sucesso num escopo vazio.");
        assertEquals(s, table.lookup("x"), "O símbolo retornado deve ser o mesmo que foi declarado.");
    }

    @Test
    void testSombreamentoERestauracao() {
        // Declara 'x' no escopo global
        Symbol globalX = new Symbol("x", null, 0, null);
        table.declare(globalX);

        // Abre um novo escopo e declara 'x' novamente (sombreamento)
        table.openScope();
        Symbol localX = new Symbol("x", null, 1, null);
        assertTrue(table.declare(localX), "Deve permitir declarar 'x' novamente em um escopo interno.");

        // Verifica se a busca resolve para o 'x' mais interno
        assertEquals(localX, table.lookup("x"), "Lookup deve retornar o símbolo do escopo mais interno (sombreamento).");

        // Fecha o escopo e verifica se o 'x' global foi restaurado
        table.closeScope();
        assertEquals(globalX, table.lookup("x"), "Lookup deve retornar o símbolo global após fechar o escopo interno.");
    }

    @Test
    void testRedeclaracaoNoMesmoEscopo() {
        // Testa a tentativa de redeclarar o mesmo nome no mesmo escopo
        Symbol s1 = new Symbol("x", null, 0, null);
        assertTrue(table.declare(s1));

        Symbol s2 = new Symbol("x", null, 0, null);
        assertFalse(table.declare(s2), "O método declare() deve retornar false ao tentar redeclarar o mesmo nome.");

        // Garante que o primeiro símbolo permaneceu na tabela
        assertEquals(s1, table.lookupLocal("x"), "O primeiro símbolo declarado deve permanecer intacto após tentativa falha.");
    }

    @Test
    void testLookupLocalNaoEnxergaEscopoExterno() {
        // Declara no escopo externo
        Symbol globalX = new Symbol("x", null, 0, null);
        table.declare(globalX);

        table.openScope();

        // lookupLocal não pode enxergar 'x' do nível 0
        assertNull(table.lookupLocal("x"), "lookupLocal não deve encontrar símbolos de escopos externos.");

        // lookup comum ainda deve enxergar
        assertEquals(globalX, table.lookup("x"), "lookup normal deve encontrar o símbolo do escopo externo.");
    }

    @Test
    void testLookupNomeInexistente() {
        assertNull(table.lookup("variavel_magica"), "Deve retornar null para um nome não declarado.");
        assertNull(table.lookupLocal("variavel_magica"), "lookupLocal deve retornar null para um nome não declarado.");
    }

    @Test
    void testLevel() {
        // O nível inicial, após o construtor, deve ser 0 (pois openScope é chamado no construtor)
        assertEquals(0, table.level());

        table.openScope();
        assertEquals(1, table.level());

        table.openScope();
        assertEquals(2, table.level());

        table.closeScope();
        assertEquals(1, table.level());

        table.closeScope();
        assertEquals(0, table.level());

        // Teste de resiliência: closeScope não deve deixar o level negativo devido ao Math.max(0, scopes.size() - 1)
        table.closeScope();
        assertEquals(0, table.level(), "O nível não deve ficar negativo, mesmo estourando o fechamento de escopos.");
    }

}
