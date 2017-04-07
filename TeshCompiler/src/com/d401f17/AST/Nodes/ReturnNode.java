package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/31/17.
 */
public class ReturnNode extends StatementNode {
    private ArithmeticExpressionNode expresssion;

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
