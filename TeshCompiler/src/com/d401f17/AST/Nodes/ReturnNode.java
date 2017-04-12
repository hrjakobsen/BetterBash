package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/31/17.
 */
public class ReturnNode extends TypedStatementNode {
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
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
