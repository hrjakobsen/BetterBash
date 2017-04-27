package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;

/**
 * Created by mathias on 4/27/17.
 */
public class InterpretVisitor extends BaseVisitor<Void> {
    @Override
    public Void visit(AdditionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            node.setNodeValue(
                    new IntLiteralNode(
                            ((IntLiteralNode)node.getLeft().getNodeValue()).getValue() + ((IntLiteralNode)node.getRight().getNodeValue()).getValue()
                    )
            );
        } else if (node.getType() instanceof FloatType) {
            node.setNodeValue(
                    new FloatLiteralNode(
                            ToFloat(node.getLeft().getNodeValue().getValue()) + ToFloat(node.getRight().getNodeValue().getValue())
                    )
            );
        }
        return null;
    }

    @Override
    public Void visit(AndNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayAppendNode node) {
        return null;
    }

    @Override
    public Void visit(ArithmeticExpressionNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayBuilderNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayElementAssignmentNode node) {
        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        node.getExpression().accept(this);
        System.out.println(node.getExpression().getNodeValue().getValue());
        return null;
    }

    @Override
    public Void visit(AST node) {
        return null;
    }

    @Override
    public Void visit(LiteralNode node) {
        return null;
    }

    @Override
    public Void visit(IntLiteralNode node) {
        node.setNodeValue(node);
        return null;
    }

    @Override
    public Void visit(BoolLiteralNode node) {
        node.setNodeValue(node);
        return null;
    }

    @Override
    public Void visit(FloatLiteralNode node) {
        node.setNodeValue(node);
        return null;
    }

    @Override
    public Void visit(StringLiteralNode node) {
        node.setNodeValue(node);
        return null;
    }

    @Override
    public Void visit(CharLiteralNode node) {
        node.setNodeValue(node);
        return null;
    }

    @Override
    public Void visit(RecordLiteralNode node) {
        node.setNodeValue(node);
        return null;
    }

    @Override
    public Void visit(DivisionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            node.setNodeValue(
                    new IntLiteralNode(
                            (int)node.getLeft().getNodeValue().getValue() / (int)node.getRight().getNodeValue().getValue()
                    )
            );
        } else {
            node.setNodeValue(
                    new FloatLiteralNode(
                            ToFloat(node.getLeft().getNodeValue().getValue()) / ToFloat(node.getRight().getNodeValue().getValue())
                    )
            );
        }
        return null;
    }

    @Override
    public Void visit(EqualNode node) {
        return null;
    }

    @Override
    public Void visit(ForkNode node) {
        return null;
    }

    @Override
    public Void visit(ForNode node) {
        return null;
    }

    @Override
    public Void visit(FunctionCallNode node) {
        return null;
    }

    @Override
    public Void visit(FunctionIdentifierNode node) {
        return null;
    }

    @Override
    public Void visit(FunctionNode node) {
        return null;
    }

    @Override
    public Void visit(GreaterThanNode node) {
        return null;
    }

    @Override
    public Void visit(GreaterThanOrEqualNode node) {
        return null;
    }

    @Override
    public Void visit(IfNode node) {
        return null;
    }

    @Override
    public Void visit(InfixExpressionNode node) {
        return null;
    }

    @Override
    public Void visit(LessThanNode node) {
        return null;
    }

    @Override
    public Void visit(LessThanOrEqualNode node) {
        return null;
    }

    @Override
    public Void visit(ModuloNode node) {
        return null;
    }

    @Override
    public Void visit(MultiplicationNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            node.setNodeValue(
                    new IntLiteralNode(
                            (int)node.getLeft().getNodeValue().getValue() * (int)node.getRight().getNodeValue().getValue()
                    )
            );
        } else {
            node.setNodeValue(
                    new FloatLiteralNode(
                            ToFloat(node.getLeft().getNodeValue().getValue()) * ToFloat(node.getRight().getNodeValue().getValue())
                    )
            );
        }
        return null;
    }

    @Override
    public Void visit(NegationNode node) {
        return null;
    }

    @Override
    public Void visit(NotEqualNode node) {
        return null;
    }

    @Override
    public Void visit(OrNode node) {
        return null;
    }

    @Override
    public Void visit(RecordDeclarationNode node) {
        return null;
    }

    @Override
    public Void visit(RecordIdentifierNode node) {
        return null;
    }

    @Override
    public Void visit(ReturnNode node) {
        return null;
    }

    @Override
    public Void visit(ShellNode node) {
        return null;
    }

    @Override
    public Void visit(ShellToChannelNode node) {
        return null;
    }

    @Override
    public Void visit(SimpleIdentifierNode node) {
        return null;
    }

    @Override
    public Void visit(StatementsNode node) {
        for (StatementNode child : node.getChildren()) {
            child.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(SubtractionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            node.setNodeValue(
                    new IntLiteralNode(
                            (int)node.getLeft().getNodeValue().getValue() - (int)node.getRight().getNodeValue().getValue()
                    )
            );
        } else {
            node.setNodeValue(
                    new FloatLiteralNode(
                            ToFloat(node.getLeft().getNodeValue().getValue()) - ToFloat(node.getRight().getNodeValue().getValue())
                    )
            );
        }
        return null;
    }

    @Override
    public Void visit(TypeNode node) {
        return null;
    }

    @Override
    public Void visit(VariableDeclarationNode node) {
        return null;
    }

    @Override
    public Void visit(WhileNode node) {
        return null;
    }

    @Override
    public Void visit(ProcedureCallNode node) {
        return null;
    }

    @Override
    public Void visit(ChannelNode node) {
        return null;
    }

    @Override
    public Void visit(PatternMatchNode node) {
        return null;
    }

    public static Float ToFloat(Object o) {
        if (o instanceof Float) return (Float)o;
        if (o instanceof Integer) {
            return ((Integer)o).floatValue();
        }
        return null;
    }
}
