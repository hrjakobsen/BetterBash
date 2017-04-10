package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/16/17.
 */
public class WhileNode extends TypedStatementNode {
    private AST predicate;
    private AST statements;

    public WhileNode(AST predicate, AST statements, int lineNum) {
        this.predicate = predicate;
        this.statements = statements;
        this.setLine(lineNum);
    }

    public AST getPredicate() {
        return predicate;
    }

    public AST getStatements() {
        return statements;
    }

    public void setPredicate(AST predicate) {
        this.predicate = predicate;
    }

    public void setStatements(AST statements) {
        this.statements = statements;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
