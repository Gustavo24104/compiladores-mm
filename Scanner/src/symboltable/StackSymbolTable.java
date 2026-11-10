package symboltable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class StackSymbolTable implements SymbolTable {

    private final Deque<Map<String, Symbol>> scopes = new ArrayDeque<>();

    public StackSymbolTable() {
        openScope();
    }

    @Override
    public void openScope() {
        scopes.push(new HashMap<>());
    }

    @Override
    public void closeScope() {
        if (!scopes.isEmpty()) {
            scopes.pop();
        }
    }

    @Override
    public int level() {
        return Math.max(0, scopes.size() - 1);
    }

    @Override
    public boolean declare(Symbol s) { // falso: já existe aqui
        if (scopes.isEmpty()) {
            openScope();
        }
        assert scopes.peek() != null;
        return scopes.peek().putIfAbsent(s.getName(), s) == null;
    }

    @Override
    public Symbol lookupLocal(String name) {
        if (scopes.isEmpty()) {
            return null;
        }
        return scopes.peek().get(name);
    }

    @Override
    public Symbol lookup(String name) {
        for (Map<String, Symbol> m : scopes) {
            Symbol s = m.get(name);
            if (s != null) {
                return s;
            }
        }
        return null;
    }
}