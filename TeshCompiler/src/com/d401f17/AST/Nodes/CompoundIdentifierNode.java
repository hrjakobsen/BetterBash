package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.List;

/**
 * Created by mathias on 4/4/17.
 */
public class CompoundIdentifierNode extends IdentifierNode {
    List<SimpleIdentifierNode> identifiers;

    public List<SimpleIdentifierNode> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<SimpleIdentifierNode> identifiers) {
        this.identifiers = identifiers;
    }

    public CompoundIdentifierNode(List<SimpleIdentifierNode> identifiers, int lineNum) {
        this.identifiers = identifiers;
        this.lineNum = lineNum;
    }

    public CompoundIdentifierNode(List<SimpleIdentifierNode> identifiers) {
        this.identifiers = identifiers;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
