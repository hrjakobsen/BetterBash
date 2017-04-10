package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/15/17.
 */
public class AssignmentNode extends StatementNode {
    public IdentifierNode variable;
    public ArithmeticExpressionNode expression;

    public IdentifierNode getVariable() {
        return variable;
    }

    public void setVariable(IdentifierNode variable) {
        this.variable = variable;
    }

    public ArithmeticExpressionNode getExpression() {
        return expression;
    }

    public void setExpression(ArithmeticExpressionNode expression) {
        this.expression = expression;
    }

    public AssignmentNode(IdentifierNode variable, ArithmeticExpressionNode expression, int lineNum) {
        this.variable = variable;
        this.expression = expression;
        this.setLine(lineNum);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
