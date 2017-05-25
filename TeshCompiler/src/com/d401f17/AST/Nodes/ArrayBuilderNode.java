package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

public class ArrayBuilderNode extends ArithmeticExpressionNode {
    public SimpleIdentifierNode variable;
    public ArithmeticExpressionNode expression;
    public ArithmeticExpressionNode array;

    public ArrayBuilderNode(SimpleIdentifierNode variable, ArithmeticExpressionNode array, ArithmeticExpressionNode expression, int lineNum) {
        this.variable = variable;
        this.expression = expression;
        this.array = array;
        this.lineNum = lineNum;
    }

    public ArrayBuilderNode(SimpleIdentifierNode variable, ArithmeticExpressionNode array, ArithmeticExpressionNode expression) {
        this.variable = variable;
        this.expression = expression;
        this.array = array;
    }

    public SimpleIdentifierNode getVariable() {
        return variable;
    }

    public void setVariable(SimpleIdentifierNode variable) {
        this.variable = variable;
    }

    public ArithmeticExpressionNode getExpression() {
        return expression;
    }

    public void setExpression(ArithmeticExpressionNode expression) {
        this.expression = expression;
    }

    public ArithmeticExpressionNode getArray() {
        return array;
    }

    public void setArray(ArithmeticExpressionNode array) {
        this.array = array;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
