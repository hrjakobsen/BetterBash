package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.List;

public class ArrayElementAssignmentNode extends StatementNode {
    public ArrayAccessNode element;
    public ArithmeticExpressionNode expression;

    public ArithmeticExpressionNode getExpression() {
        return expression;
    }

    public void setExpression(ArithmeticExpressionNode expression) {
        this.expression = expression;
    }

    public ArrayAccessNode getElement() {

        return element;
    }

    public void setElement(ArrayAccessNode element) {
        this.element = element;
    }

    public ArrayElementAssignmentNode(ArrayAccessNode element, ArithmeticExpressionNode expression, int lineNum) {
        this.element = element;
        this.expression = expression;
        this.lineNum = lineNum;
    }

    public ArrayElementAssignmentNode(ArrayAccessNode element, ArithmeticExpressionNode expression) {
        this.element = element;
        this.expression = expression;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
