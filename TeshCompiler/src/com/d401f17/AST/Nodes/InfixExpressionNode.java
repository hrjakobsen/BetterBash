package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/15/17.
 */
public abstract class InfixExpressionNode extends ArithmeticExpressionNode {
    private ArithmeticExpressionNode left;
    private ArithmeticExpressionNode right;

    public ArithmeticExpressionNode getLeft() {
        return left;
    }

    public ArithmeticExpressionNode getRight() {
        return right;
    }

    public void setRight(ArithmeticExpressionNode right) {
        this.right = right;
    }

    public void setLeft(ArithmeticExpressionNode left) {
        this.left = left;
    }

    public InfixExpressionNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right) {
        this.left = left;
        this.right = right;
    }
}
