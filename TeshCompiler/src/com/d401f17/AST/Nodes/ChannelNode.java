package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/16/17.
 */
public class ChannelNode extends StatementNode {
    private IdentifierNode identifier;
    private ArithmeticExpressionNode expression;

    public ChannelNode(IdentifierNode identifier, ArithmeticExpressionNode expression, int lineNum) {
        this.identifier = identifier;
        this.expression = expression;
        this.lineNum = lineNum;
    }

    public ChannelNode(IdentifierNode identifier, ArithmeticExpressionNode expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public ArithmeticExpressionNode getExpression() {
        return expression;
    }

    public IdentifierNode getIdentifier() {
        return identifier;
    }

    public void setExpression(ArithmeticExpressionNode expression) {
        this.expression = expression;
    }

    public void setIdentifier(IdentifierNode identifier) {
        this.identifier = identifier;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}

