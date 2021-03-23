package com.example.softwarepatternsca4;

public class OperationDiscount implements Strategy {

    @Override
    public double doOperation(int discount, double price) {
        double tenPOff = price * discount / 100;
        return price - tenPOff;
    }
}
