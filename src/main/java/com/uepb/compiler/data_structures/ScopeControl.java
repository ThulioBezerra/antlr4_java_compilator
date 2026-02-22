package com.uepb.compiler.data_structures;

import java.util.LinkedList;
import java.util.Optional;

public class ScopeControl {
    private final LinkedList<SymbolTable> scopes;

    public ScopeControl(){
        scopes = new LinkedList<>();
        createNewScope();
    }

    public void createNewScope(){
        scopes.push(new SymbolTable());
    }

    public void dropCurrentScope(){
        scopes.pop();
    }

    public SymbolTable getCurrentScope(){
        return scopes.getFirst();
    }

    public Optional<Variable> findClosestVariable(String name){
        return scopes
            .stream()
            .filter(table -> table.exists(name))
            .map(table -> table.get(name))
            .findFirst();
    }

}
