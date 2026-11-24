# Verificador de tipos (CP5)

`visitor/TypeCheckerVisitor` é um `Visitor<Tipo>` executado depois de `SymbolTableVisitor`. Não usa `instanceof` nem `switch` sobre o tipo do nó.

## Representação dos tipos
- Tipo = `ast.Tipo` com nome `int`, `float`, `string`, `bool`, `void` ou `erro`.
- `Exp` tem o campo `tipo` (`getTipo`/`setTipo`), preenchido por cada `visitXxx` de expressão.
- `Symbol` guarda a assinatura: `getType()` (retorno ou tipo da variável) e `getParamTypes()` (não nulo só para função, preenchido em `SymbolTableVisitor.declararFuncao`).
- `erro` é compatível com tudo: operação com operando `erro` resulta em `erro` sem nova mensagem.

## Onde está cada regra (`TypeCheckerVisitor`)
| Regra | Método |
|---|---|
| LIT-*, ID, ID-ERRO | `visitConstExp`, `visitNoIdentificador` |
| ARIT-*, CONCAT, REL-*, OP-LOG | `visitBinExp` / `resultadoBinario` |
| NEG-LOG | `visitUnaryExp` |
| ATRIB-* | `visitAtribExp` |
| chamada, aridade, argumentos | `visitChamadaExp` |
| EXP-IF, EXP-WHILE, EXP-FOR | `visitIfDecl`, `visitWhileDecl`, `visitForDecl` (`condicao`) |
| EXP-RETURN, return em todos os caminhos | `visitReturnDecl`, `visitDeclFunc` (campo `retorna`) |
| declaração com inicializador | `declaraVar` (o inicializador é visitado antes de declarar o nome, também em `SymbolTableVisitor`) |
| aviso de variável sem valor | `comValor` + `visitNoIdentificador` |

## Decisões
- `int → float` implícito em atribuição, inicialização, argumento, `return` e operandos aritméticos/relacionais.
- `+` com ao menos um operando `string` concatena (resultado `string`).
- `==`/`!=`: numéricos entre si, ou tipos idênticos (`string`, `bool`).
- Chamada de função `void` só é aceita como comando; em expressão é erro.
- Nome de função sem parênteses é erro.
- Função aninhada é erro (`SymbolTableVisitor`); o corpo é analisado no escopo da função externa, então variáveis do nível acima não geram erro em cascata.
- Return em todos os caminhos: um bloco retorna se algum comando retorna; `if` retorna se todos os ramos (com `else`) retornam; `while`/`for` nunca contam.
- Variável sem valor: análise conservadora por fluxo (`if` faz interseção dos ramos, laços descartam atribuições do corpo, ramo que retorna não conta); gera aviso, não erro. Globais e parâmetros contam como inicializados.
- Mensagens: `linha N: descrição` (avisos: `linha N: aviso: descrição`), acumuladas na ordem do texto.

## Testes
- `test/programas/X.txt` com saída esperada `test/esperado/X.txt`, executados por `TypeCheckerVisitorTest`. Programas `dNN-*` correspondem às 11 decisões.
- `TabelaOperadoresTest` testa cada linha da tabela de operadores (aceita e recusada) montando a AST à mão, sem parser.
