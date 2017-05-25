package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

public class ReturnNode extends StatementNode {
    private ArithmeticExpressionNode expresssion;

    public ReturnNode(ArithmeticExpressionNode expresssion, int lineNum) {
        this.expresssion = expresssion;
        this.lineNum = lineNum;
    }

    public ReturnNode(ArithmeticExpressionNode expresssion) {
        this.expresssion = expresssion;
    }

    public ArithmeticExpressionNode getExpresssion() {
        return expresssion;
    }

    public void setExpresssion(ArithmeticExpressionNode expresssion) {
        this.expresssion = expresssion;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
