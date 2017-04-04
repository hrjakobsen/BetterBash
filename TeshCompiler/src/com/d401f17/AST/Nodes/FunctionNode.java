package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.List;

/**
 * Created by mathias on 3/31/17.
 */
public class FunctionNode extends StatementNode {
    private SimpleIdentifierNode name;
    private List<VariableDeclarationNode> formalArguments;
    private StatementsNode statements;
    private TypeNode type;

    public List<VariableDeclarationNode> getFormalArguments() {
        return formalArguments;
    }

    public void setFormalArguments(List<VariableDeclarationNode> formalArguments) {
        this.formalArguments = formalArguments;
    }

    public StatementsNode getStatements() {
        return statements;
    }

    public void setStatements(StatementsNode statements) {
        this.statements = statements;
    }

    public FunctionNode(SimpleIdentifierNode name, TypeNode type, List<VariableDeclarationNode> formalArguments, StatementsNode statements) {

        this.name = name;
        this.formalArguments = formalArguments;
        this.statements = statements;
        this.type = type;
    }

    public SimpleIdentifierNode getName() {

        return name;
    }

    public void setName(SimpleIdentifierNode name) {
        this.name = name;
    }

    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
