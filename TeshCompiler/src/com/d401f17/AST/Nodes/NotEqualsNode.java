package com.d401f17.AST.Nodes;

/**
 * Created by mathias on 3/15/17.
 */
public class NotEqualsNode extends InfixExpressionNode {
    public NotEqualsNode(AST left, AST right) {
        super(left, right);
    }
}
