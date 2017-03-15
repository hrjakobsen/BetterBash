package com.d401f17.AST.Nodes;

/**
 * Created by mathias on 3/15/17.
 */
public class InfixExpressionNode {
    private AST left;
    private AST right;

    public AST getLeft() {
        return left;
    }

    public AST getRight() {
        return right;
    }

    public void setRight(AST right) {
        this.right = right;
    }

    public void setLeft(AST left) {
        this.left = left;
    }

    public InfixExpressionNode(AST left, AST right) {
        this.left = left;
        this.right = right;
    }
}
