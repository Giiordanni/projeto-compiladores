package com.compiler.semantica;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    // Guardar endereço e tipo da variável
    public static class Symbol {
        public final int address;
        public String type; // "number", "string", "bool", "unknown"

        public Symbol(int address, String type) {
            this.address = address;
            this.type    = type;
        }
    }

    private final Deque<Map<String, Symbol>> scopes = new ArrayDeque<>();
    private int nextAddress = 0;

    public SymbolTable(){
        enterScope(); // Escopo global
    }

    public void enterScope(){
        scopes.push(new HashMap<>());
    }

    public void exitScope(){
        if (scopes.size() > 1) { // Não sair do escopo global
            scopes.pop();
        }
    }

    // Declarar a variável no escopo atual
    public int declare(String name, String type) {
        Map<String, Symbol> current = scopes.peek();
        if (current.containsKey(name)) {
            throw new RuntimeException(
                    "Erro semântico: variável '" + name + "' já declarada neste escopo.");
        }
        current.put(name, new Symbol(nextAddress, type));
        return nextAddress++;
    }

    public int declare(String name) {
        return declare(name, "unknown");
    }

    // Atualiza o tipo de uma variável já declarada
    public void setType(String name, String type) {
        for (Map<String, Symbol> scope : scopes) {
            if (scope.containsKey(name)) {
                scope.get(name).type = type;
                return;
            }
        }
        throw new RuntimeException(
                "Erro semântico: variável '" + name + "' não declarada.");
    }

    // Retorna o símbolo, procurando do escopo mais interno para o mais externo
    public Symbol getSymbol(String name) {
        for (Map<String, Symbol> scope : scopes) {
            if (scope.containsKey(name)) return scope.get(name);
        }
        throw new RuntimeException(
                "Erro semântico: variável '" + name + "' usada sem declaração.");
    }

    // Retorna o tipo da variável
    public String getType(String name) {
        return getSymbol(name).type;
    }

    // Retorna o endereço (para o CodeGenerator)
    public int get(String name) {
        return getSymbol(name).address;
    }

    public void assertDeclared(String name) {
        getSymbol(name); // já lança exceção se não existir
    }

    public boolean isDeclared(String name) {
        for (Map<String, Symbol> scope : scopes) {
            if (scope.containsKey(name)) return true;
        }
        return false;
    }

}
