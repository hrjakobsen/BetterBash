package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

public class FunctionIdentifierNode extends IdentifierNode {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FunctionIdentifierNode(String name, int lineNum) {
        this.name = name;
        this.lineNum = lineNum;
    }

    public FunctionIdentifierNode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
