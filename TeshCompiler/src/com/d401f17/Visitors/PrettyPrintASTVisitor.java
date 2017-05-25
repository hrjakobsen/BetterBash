package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;

import java.util.ArrayList;
import java.util.List;

public class PrettyPrintASTVisitor extends BaseVisitor<Void> {
    int runningID = 0;
    StringBuilder sb = new StringBuilder();

    @Override
    public String toString() {
        return "graph {\n" + sb.toString() + "\n}";
    }

    public String makeNode(String name, AST ... children) {
        String id = Integer.toString(runningID++);
        sb.append(id).append("\n");
        sb.append(id).append("[label=\"").append(name).append("\"]\n");
        for (AST child : children) {
            sb.append(id).append(" -- ");
            child.accept(this);
            sb.append("\n");
        }
        return id;
    }

    @Override
    public Void visit(AdditionNode node) {
        makeNode("+", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(AndNode node) {
        makeNode("&&", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(ArrayAppendNode node) {
        makeNode("::=", node.getVariable(), node.getExpression());
        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node) {
        makeNode(node.getArray().toString(), node.getIndices().toArray(new ArithmeticExpressionNode[0]));
        return null;
    }

    @Override
    public Void visit(ArrayBuilderNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayElementAssignmentNode node) {
        makeNode("=", node.element, node.expression);
        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        makeNode("=", node.variable, node.expression);
        return null;
    }

    @Override
    public Void visit(AST node) {
        return null;
    }

    @Override
    public Void visit(RecordIdentifierNode node)
    {
        makeNode(node.getName(), node.getChild());
        return null;
    }

    @Override
    public Void visit(LiteralNode node) {
        String id = Integer.toString(runningID++);
        sb.append(id).append("\n").append(id).append("[label=\"").append(node.getType().toString()).append("\\n").append(node.getValue().toString().replace("\\", "\\\\")).append("\"]\n");
        return null;
    }

    @Override
    public Void visit(IntLiteralNode node) {
        String id = Integer.toString(runningID++);
        sb.append(id).append("\n").append(id).append("[label=\"").append(node.getType().toString()).append("\\n").append(node.getValue().toString().replace("\\", "\\\\")).append("\"]\n");
        return null;
    }

    @Override
    public Void visit(BoolLiteralNode node) {
        visit((LiteralNode)node);
        return null;
    }

    @Override
    public Void visit(FloatLiteralNode node) {
        visit((LiteralNode)node);
        return null;
    }

    @Override
    public Void visit(StringLiteralNode node) {
        visit((LiteralNode)node);
        return null;
    }

    @Override
    public Void visit(CharLiteralNode node) {
        visit((LiteralNode)node);
        return null;
    }

    @Override
    public Void visit(RecordLiteralNode node) {
        visit((LiteralNode)node);
        return null;
    }

    @Override
    public Void visit(ArrayLiteralNode node) {
        makeNode("Array", (node.getValue()).toArray(new ArithmeticExpressionNode[0]));
        return null;
    }

    @Override
    public Void visit(DivisionNode node) {
        makeNode("/", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(EqualNode node) {
        makeNode("==", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(ForkNode node) {
        makeNode("fork", node.getChild());
        return null;
    }

    @Override
    public Void visit(ForNode node) {
        makeNode("for", node.getVariable(), node.getArray(), node.getStatements());
        return null;
    }

    @Override
    public Void visit(FunctionCallNode node) {
        makeNode("call " + node.getName().toString(), node.getArguments().toArray(new ArithmeticExpressionNode[0]));
        return null;
    }

    @Override
    public Void visit(FunctionNode node) {
        List<AST> children = new ArrayList<>();
        children.addAll(node.getFormalArguments());
        children.add(node.getStatements());
        children.add(node.getTypeNode());
        makeNode("func " + node.getName().getName(), children.toArray(new AST[0]));
        return null;
    }

    @Override
    public Void visit(GreaterThanNode node) {
        makeNode(">", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(GreaterThanOrEqualNode node) {
        makeNode(">=", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(IfNode node) {
        makeNode("if", node.getPredicate(), node.getTrueBranch(), node.getFalseBranch());
        return null;
    }

    @Override
    public Void visit(LessThanNode node) {
        makeNode("<", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(LessThanOrEqualNode node) {
        makeNode("<=", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(ModuloNode node) {
        makeNode("mod", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(MultiplicationNode node) {
        makeNode("*", node.getLeft(),node.getRight());
        return null;
    }

    @Override
    public Void visit(NegationNode node) {
        makeNode("!", node.getExpression());
        return null;
    }

    @Override
    public Void visit(NotEqualNode node) {
        makeNode("!=", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(OrNode node) {
        makeNode("or", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(RecordDeclarationNode node) {
        makeNode("Rec " + node.getName(), node.getVariables().toArray(new VariableDeclarationNode[0]));
        return null;
    }

    @Override
    public Void visit(ReturnNode node) {
        makeNode("return", node.getExpresssion());
        return null;
    }

    @Override
    public Void visit(ShellNode node) {
        makeNode("$", node.getCommand());
        return null;
    }

    @Override
    public Void visit(ShellToChannelNode node) {
        makeNode("Shell_to_ch", node.getChannel(), node.getCommand());
        return null;
    }

    @Override
    public Void visit(SimpleIdentifierNode node) {
        StringBuilder label = new StringBuilder();
        label.append(node.getName());

        String id = Integer.toString(runningID++);

        sb.append(id).append("\n").append(id).append("[label=\"").append(label).append("\"]\n");
        return null;
    }

    @Override
    public Void visit(StatementsNode node) {
        String id = Integer.toString(runningID++);
        if (node.getChildren().size() == 0) {
            sb.append(id).append("\n");
            sb.append(id).append("[label=\"...\"]\n");
            return null;
        }
        for (AST child : node.getChildren()) {
            sb.append(id).append(" -- ");
            child.accept(this);
            sb.append(id).append("[label=\"...\"]\n");
        }
        return null;
    }

    @Override
    public Void visit(SubtractionNode node) {
        makeNode("-", node.getLeft(), node.getRight());
        return null;
    }

    @Override
    public Void visit(TypeNode node) {
        makeNode(node.getType().toString());
        return null;
    }

    @Override
    public Void visit(VariableDeclarationNode node) {
        makeNode("vardef", node.getName(), node.getTypeNode());
        return null;
    }

    @Override
    public Void visit(WhileNode node) {
        makeNode("while", node.getPredicate(), node.getStatements());
        return null;
    }

    @Override
    public Void visit(ProcedureCallNode node) {
        makeNode("proc call " + node.getName().toString(), node.getArguments().toArray(new ArithmeticExpressionNode[0]));
        return null;
    }

    @Override
    public Void visit(ChannelNode node) {
        makeNode("chn", node.getIdentifier(), node.getExpression());
        return null;
    }

    @Override
    public Void visit(PatternMatchNode node) {
        makeNode("?=", node.getLeft(), node.getRight());
        return null;
    }
}
