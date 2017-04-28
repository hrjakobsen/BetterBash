package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/15/17.
 */
public class NegationNode extends AST {
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
