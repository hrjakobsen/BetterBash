package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 4/4/17.
 */
public class PatternMatchNode extends InfixExpressionNode {
    public PatternMatchNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right, int lineNum) {
        super(left, right, lineNum);
    }

    public PatternMatchNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right) {
        super(left, right);
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
