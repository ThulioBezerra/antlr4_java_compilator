package com.uepb.compiler;

import java.util.LinkedList;
import java.util.Optional;

public class ScopeControl {
    private final LinkedList<SymbolTable> pilha;

    public ScopeControl(){
        pilha = new LinkedList<>();
        createScope();
    }

    public void createScope(){
        pilha.push(new SymbolTable());
    }

    public void dropScope(){
        pilha.pop();
    }

    public SymbolTable getCurrentScope(){
        return pilha.peek();
    }

    public Optional<Variables> lookup(String name){
        return pilha.stream()
            .filter((table) -> table.exists(name))
            .map((table) -> table.get(name))
            .findFirst();
    }

}
