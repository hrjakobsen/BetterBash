package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/31/17.
 */
public class VariableDeclarationNode extends StatementNode {
    private SimpleIdentifierNode name;
    private TypeNode typeNode;

    public SimpleIdentifierNode getName() {
        return name;
    }

    public void setName(SimpleIdentifierNode name) {
        this.name = name;
    }

    public TypeNode getTypeNode() {
        return typeNode;
    }

    public void setTypeNode(TypeNode type) {
        this.typeNode = type;
    }

    public VariableDeclarationNode(SimpleIdentifierNode name, TypeNode type, int lineNum) {
        this.name = name;
        this.typeNode = type;
        this.setLine(lineNum);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
