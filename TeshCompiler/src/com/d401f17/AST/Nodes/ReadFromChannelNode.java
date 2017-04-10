package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/16/17.
 */
public class ReadFromChannelNode extends StatementNode {
    private IdentifierNode variable;
    private IdentifierNode channel;

    public IdentifierNode getChannel() {
        return channel;
    }

    public IdentifierNode getVariable() {
        return variable;
    }

    public void setChannel(IdentifierNode channel) {
        this.channel = channel;
    }

    public void setVariable(IdentifierNode variable) {
        this.variable = variable;
    }

    public ReadFromChannelNode(IdentifierNode variable, IdentifierNode channel, int lineNum) {
        this.variable = variable;
        this.channel = channel;
        this.setLine(lineNum);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
