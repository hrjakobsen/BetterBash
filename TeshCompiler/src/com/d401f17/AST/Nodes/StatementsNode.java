package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatementsNode extends StatementNode {
    private List<StatementNode> children;

    public StatementsNode(int lineNum, StatementNode ... children) {
        this.lineNum = lineNum;
        this.children = new ArrayList<>(Arrays.asList(children));
    }

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
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
