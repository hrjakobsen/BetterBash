package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/15/17.
 */
public class GreaterThanOrEqualNode extends InfixExpressionNode {
    public GreaterThanOrEqualNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right, int lineNum) {
        super(left, right, lineNum);
    }

    public GreaterThanOrEqualNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right) {
        super(left, right);
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
