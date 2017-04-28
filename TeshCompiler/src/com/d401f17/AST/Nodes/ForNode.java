package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/16/17.
 */
public class ForNode extends StatementNode {
    private SimpleIdentifierNode variable;
    private ArithmeticExpressionNode array;
    private AST statements;

    public AST getStatements() {
        return statements;
    }

    public void setVariable(SimpleIdentifierNode variable) {
        this.variable = variable;
    }

    public ArithmeticExpressionNode getArray() {
        return array;
    }

    public SimpleIdentifierNode getVariable() {
        return variable;
    }

    public void setStatements(AST statements) {
        this.statements = statements;
    }

    public void setArray(ArithmeticExpressionNode array) {
        this.array = array;
    }

    public ForNode(SimpleIdentifierNode variable, ArithmeticExpressionNode array, AST statements, int lineNum) {
        this.variable = variable;
        this.array = array;
        this.statements = statements;
        this.lineNum = lineNum;
    }

    public ForNode(SimpleIdentifierNode variable, ArithmeticExpressionNode array, AST statements) {
        this.variable = variable;
        this.array = array;
        this.statements = statements;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
