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

    public ReadFromChannelNode(IdentifierNode variable, IdentifierNode channel, int lineNum) {
        this.variable = variable;
        this.channel = channel;
        this.lineNum = lineNum;
    }

    public ReadFromChannelNode(IdentifierNode variable, IdentifierNode channel) {
        this.variable = variable;
        this.channel = channel;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
