package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/15/17.
 * Corrected by hense on 4/12/17.
 */
public class NotEqualNode extends InfixExpressionNode {
    public NotEqualNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right, int lineNum) {
        super(left, right, lineNum);
    }

    public NotEqualNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right) {
        super(left, right);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
