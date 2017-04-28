package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/31/17.
 */
public class ShellNode extends StatementNode {
    ArithmeticExpressionNode command;

    public ShellNode(ArithmeticExpressionNode command, int lineNum) {
        this.command = command;
        this.lineNum = lineNum;
    }

    public ShellNode(ArithmeticExpressionNode command) {
        this.command = command;
    }

    public ArithmeticExpressionNode getCommand() {
        return command;
    }

    public void setCommand(ArithmeticExpressionNode command) {
        this.command = command;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
