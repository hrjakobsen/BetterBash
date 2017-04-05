package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mathias on 3/15/17.
 */
public class StatementsNode extends StatementNode {
    private List<StatementNode> children;

    public StatementsNode(StatementNode ... children) {
        this.children = new ArrayList<>(Arrays.asList(children));
    }

    public List<StatementNode> getChildren() {
        return children;
    }

    public void setChildren(List<StatementNode> children) {
        this.children = children;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
