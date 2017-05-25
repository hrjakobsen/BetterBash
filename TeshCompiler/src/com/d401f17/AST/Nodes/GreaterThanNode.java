package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

public class GreaterThanNode extends InfixExpressionNode {
    public GreaterThanNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right, int lineNum) {
        super(left, right, lineNum);
    }

    public GreaterThanNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right) {
        super(left, right);
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
