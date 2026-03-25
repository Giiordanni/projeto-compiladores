package com.compiler.visitor;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private final Map<String, Integer> table = new HashMap<>();
    private int nextAddress = 0;

    // Declara uma nova variável; lança exceção se já declarada.
    public int declare(String var) {
        if (table.containsKey(var)) {
            throw new RuntimeException("Variável já declarada: " + var);
        }
        table.put(var, nextAddress);
        return nextAddress++;
    }

    // Retorna o endereço de uma variável declarada.
    public int get(String var) {
        assertDeclared(var);
        return table.get(var);
    }

    // Verifica se a variável foi declarada; lança RuntimeException caso contrário.
    public void assertDeclared(String var) {
        if (!table.containsKey(var)) {
            throw new RuntimeException(
                    "Erro semântico: variável '" + var + "' usada sem declaração."
            );
        }
    }

    // Retorna true se a variável já foi declarada.
    public boolean isDeclared(String var) {
        return table.containsKey(var);
    }
}
