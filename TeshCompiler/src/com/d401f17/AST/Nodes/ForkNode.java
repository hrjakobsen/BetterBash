package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

public class ForkNode extends StatementNode {
    StatementNode child;

    public StatementNode getChild() {
        return child;
    }

    public void setChild(StatementNode child) {
        this.child = child;
    }

    public ForkNode(StatementNode child, int lineNum) {
        this.child = child;
        this.lineNum = lineNum;
    }

    public ForkNode(StatementNode child) {
        this.child = child;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
