package com.uepb.compiler;

public class MemoryMapper {

    private int currentAddress = 0;

    public int alloc(){
        return currentAddress++;
    }

    public int getCurrentAddress(){
        return currentAddress;
    }

    public void restoreTo(int marker){
        currentAddress = marker;
    }

}