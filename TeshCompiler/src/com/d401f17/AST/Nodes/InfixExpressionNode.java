package com.d401f17.AST.Nodes;

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

    public InfixExpressionNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right, int lineNum) {
        this.left = left;
        this.right = right;
        this.lineNum = lineNum;
    }

    public InfixExpressionNode(ArithmeticExpressionNode left, ArithmeticExpressionNode right) {
        this.left = left;
        this.right = right;
    }
}
