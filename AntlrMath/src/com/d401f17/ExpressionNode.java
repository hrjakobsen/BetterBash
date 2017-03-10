package com.d401f17;


import java.util.function.Function;

public abstract class ExpressionNode {
}

abstract class InfixExpressionNode extends ExpressionNode{
    private ExpressionNode left;
    private ExpressionNode right;

    public ExpressionNode getLeft() {
        return left;
    }

    public ExpressionNode getRight() {
        return right;
    }

    public void setLeft(ExpressionNode left) {
        this.left = left;
    }

    public void setRight(ExpressionNode right) {
        this.right = right;
    }
}


class AdditionNode  extends InfixExpressionNode {}

class SubtractionNode extends InfixExpressionNode {}

class MultiplicationNode extends InfixExpressionNode {}

class DivisionNode extends InfixExpressionNode {}

class NegateNode extends ExpressionNode {
    private ExpressionNode innerExpression;

    public ExpressionNode getInnerExpression() {
        return innerExpression;
    }

    public void setInnerExpression(ExpressionNode innerExpression) {
        this.innerExpression = innerExpression;
    }
}

 class FunctionNode extends ExpressionNode {
    private Function<Double, Double> function;
    private ExpressionNode argument;

    public Function<Double, Double> getFunction() {
        return function;
    }

    public void setFunction(Function<Double, Double> function) {
        this.function = function;
    }

    public ExpressionNode getArgument() {
        return argument;
    }

    public void setArgument(ExpressionNode argument) {
        this.argument = argument;
    }
}

class NumberNode extends ExpressionNode {
    private double value;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}