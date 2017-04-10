package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mathias on 3/15/17.
 */
public class StatementsNode extends StatementNode {
    private ArrayList<StatementNode> children;

    public StatementsNode(int lineNum, StatementNode ... children) {
        this.setLine(lineNum);
        this.children = new ArrayList<>(Arrays.asList(children));
    }

    public ArrayList<StatementNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<StatementNode> children) {
        this.children = children;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
