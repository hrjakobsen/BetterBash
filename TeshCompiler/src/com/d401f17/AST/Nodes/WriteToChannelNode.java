package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/16/17.
 */
public class WriteToChannelNode extends StatementNode {
    private IdentifierNode identifier;
    private ArithmeticExpressionNode expression;

    public WriteToChannelNode(IdentifierNode identifier, ArithmeticExpressionNode expression) {
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
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

