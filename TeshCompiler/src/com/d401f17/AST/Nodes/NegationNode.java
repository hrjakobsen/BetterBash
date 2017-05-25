package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

public class NegationNode extends ArithmeticExpressionNode {
    private AST expression;

    public NegationNode(AST expression, int lineNum) {
        this.expression = expression;
        this.lineNum = lineNum;
    }

    public NegationNode(AST expression) {
        this.expression = expression;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }

    public AST getExpression() {
        return expression;
    }

    public void setExpression(AST expression) {
        this.expression = expression;
    }
}
