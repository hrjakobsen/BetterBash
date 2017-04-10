package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/16/17.
 */
public class IfNode extends TypedStatementNode {
    private ArithmeticExpressionNode predicate;
    private StatementsNode trueBranch;
    private StatementsNode falseBranch;

    public StatementsNode getFalseBranch() {
        return falseBranch;
    }

    public ArithmeticExpressionNode getPredicate() {
        return predicate;
    }

    public StatementsNode getTrueBranch() {
        return trueBranch;
    }

    public void setFalseBranch(StatementsNode falseBranch) {
        this.falseBranch = falseBranch;
    }

    public void setPredicate(ArithmeticExpressionNode predicate) {
        this.predicate = predicate;
    }

    public void setTrueBranch(StatementsNode trueBranch) {
        this.trueBranch = trueBranch;
    }

    public IfNode(ArithmeticExpressionNode predicate, StatementsNode trueBranch, StatementsNode falseBranch) {
        this.predicate = predicate;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
