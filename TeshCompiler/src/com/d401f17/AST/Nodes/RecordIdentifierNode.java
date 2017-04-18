package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.List;

/**
 * Created by mathias on 4/4/17.
 */
public class RecordIdentifierNode extends IdentifierNode {
    IdentifierNode child;
    SimpleIdentifierNode name;

    public RecordIdentifierNode(IdentifierNode child, SimpleIdentifierNode name, int lineNum) {
        this.child = child;
        this.name = name;
        this.lineNum = lineNum;
    }

    public SimpleIdentifierNode getName() {

        return name;
    }

    public void setName(SimpleIdentifierNode name) {
        this.name = name;
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
