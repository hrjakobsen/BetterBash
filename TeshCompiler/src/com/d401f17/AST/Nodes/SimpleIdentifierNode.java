package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

public class SimpleIdentifierNode extends IdentifierNode {
    public SimpleIdentifierNode(String name, int lineNum) {
        this.name = name;
        this.lineNum = lineNum;
    }

    public SimpleIdentifierNode(String name) {
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
