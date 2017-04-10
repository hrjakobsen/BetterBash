package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by hense on 4/9/17.
 */
public class TypedStatementNode extends StatementNode {
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
