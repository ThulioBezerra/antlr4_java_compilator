package com.uepb.compiler.data_structures;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Variable> table;

    public SymbolTable(){
        table = new HashMap<>();
    }

    public void insertVariable(String name, Double value){
        table.put(name, new Variable(name, value));
    }

    public boolean exists(String name){
        return table.containsKey(name);
    }

    public Variable get(String name){
        return table.get(name);
    }

}
