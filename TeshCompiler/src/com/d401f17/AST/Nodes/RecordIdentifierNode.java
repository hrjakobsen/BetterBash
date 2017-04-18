package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.List;

/**
 * Created by mathias on 4/4/17.
 */
public class RecordIdentifierNode extends IdentifierNode {
    IdentifierNode child;

    public RecordIdentifierNode(IdentifierNode child, String name, int lineNum) {
        this.child = child;
        this.name = name;
        this.lineNum = lineNum;
    }

    public IdentifierNode getChild() {
        return child;
    }

    public void setChild(IdentifierNode child) {
        this.child = child;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
