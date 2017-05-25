package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

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
        this.lineNum = lineNum;
    }

    public AssignmentNode(IdentifierNode variable, ArithmeticExpressionNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
