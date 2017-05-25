package com.d401f17.AST.Nodes;

public abstract class IdentifierNode extends ArithmeticExpressionNode {
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
