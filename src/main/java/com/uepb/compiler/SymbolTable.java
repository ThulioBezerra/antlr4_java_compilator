package com.uepb.compiler;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Variables> table = new HashMap<>();

    public void insert(String name, int address, boolean isConstant) {
        table.put(name, new Variables(name, address, isConstant));
    }

    public boolean exists(String name) {
        return table.containsKey(name);
    }

    public Variables get(String name) {
        return table.get(name);
    }

}