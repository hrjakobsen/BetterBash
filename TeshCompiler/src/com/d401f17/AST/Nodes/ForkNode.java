package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/31/17.
 */
public class ForkNode extends StatementNode {
    StatementNode child;

    public StatementNode getChild() {
        return child;
    }

    public void setChild(StatementNode child) {
        this.child = child;
    }

    public ForkNode(StatementNode child) {
        this.child = child;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
