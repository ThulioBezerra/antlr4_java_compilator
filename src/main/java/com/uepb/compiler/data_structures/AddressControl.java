package com.uepb.compiler.data_structures;

public class AddressControl {
    private int currentAddress = 0;

    public int allocate(){
        return currentAddress++;
    }

    public int getMarker(){
        return currentAddress;
    }

    public void restoreMarker(int marker){
        currentAddress = marker;
    }

}
