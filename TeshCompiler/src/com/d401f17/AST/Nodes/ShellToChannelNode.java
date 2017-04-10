package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 4/4/17.
 */
public class ShellToChannelNode extends StatementNode {
    private IdentifierNode channel;
    private ShellNode command;

    public ShellToChannelNode(IdentifierNode channel, ShellNode command, int lineNum) {
        this.channel = channel;
        this.command = command;
        this.setLine(lineNum);
    }

    public IdentifierNode getChannel() {
        return channel;
    }

    public void setChannel(IdentifierNode channel) {
        this.channel = channel;
    }

    public ShellNode getCommand() {
        return command;
    }

    public void setCommand(ShellNode command) {
        this.command = command;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
