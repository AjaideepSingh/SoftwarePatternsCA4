package com.example.softwarepatternsca4;

public class Context {
    private final Strategy strategy;

    public Context(Strategy strategy) {
        this.strategy = strategy;
    }

    public double executeStrategy(int discount,double price) {
        return strategy.doOperation(discount,price);
    }
}
