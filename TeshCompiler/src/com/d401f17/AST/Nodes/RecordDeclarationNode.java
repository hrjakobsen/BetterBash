package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.List;

/**
 * Created by mathias on 4/4/17.
 */
public class RecordDeclarationNode extends StatementNode {
    private String name;
    List<VariableDeclarationNode> variables;

    public RecordDeclarationNode(String name, List<VariableDeclarationNode> variables, int lineNum) {
        this.name = name;
        this.variables = variables;
        this.lineNum = lineNum;
    }

    public RecordDeclarationNode(String name, List<VariableDeclarationNode> variables) {
        this.name = name;
        this.variables = variables;
    }

    public List<VariableDeclarationNode> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableDeclarationNode> variables) {
        this.variables = variables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
