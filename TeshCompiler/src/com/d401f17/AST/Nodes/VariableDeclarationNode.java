package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/31/17.
 */
public class VariableDeclarationNode extends StatementNode {
    private SimpleIdentifierNode name;
    private TypeNode type;

    public SimpleIdentifierNode getName() {
        return name;
    }

    public void setName(SimpleIdentifierNode name) {
        this.name = name;
    }

    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    public VariableDeclarationNode(SimpleIdentifierNode name, TypeNode type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
