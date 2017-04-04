package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/15/17.
 */
public class AssignmentNode extends StatementNode {
    public IdentifierNode variable;
    public ArithmeticExpressionNode expression;

    public AssignmentNode(IdentifierNode variable, ArithmeticExpressionNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
