package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/16/17.
 */
public class ForNode extends TypedStatementNode {
    private SimpleIdentifierNode variable;
    private AST array;
    private AST statements;

    public AST getStatements() {
        return statements;
    }

    public void setVariable(SimpleIdentifierNode variable) {
        this.variable = variable;
    }

    public AST getArray() {
        return array;
    }

    public SimpleIdentifierNode getVariable() {
        return variable;
    }

    public void setStatements(AST statements) {
        this.statements = statements;
    }

    public void setArray(AST array) {
        this.array = array;
    }

    public ForNode(SimpleIdentifierNode variable, AST array, AST statements) {
        this.variable = variable;
        this.array = array;
        this.statements = statements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
