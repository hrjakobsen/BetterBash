package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

public class WhileNode extends StatementNode {
    private AST predicate;
    private AST statements;

    public WhileNode(AST predicate, AST statements, int lineNum) {
        this.predicate = predicate;
        this.statements = statements;
        this.lineNum = lineNum;
    }

    public WhileNode(AST predicate, AST statements) {
        this.predicate = predicate;
        this.statements = statements;
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
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
