package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by hense on 5/4/17.
 */
public class ProgramNode extends AST {
    private StatementNode child;

    public ProgramNode(StatementNode child) {
        this.child = child;
    }

    public StatementNode getChild() {
        return child;
    }

    public void setChild(StatementNode child) {
        this.child = child;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
