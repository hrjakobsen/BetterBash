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
        } else if (node.getLeft().getType() instanceof StringType) {
            node.setNodeValue(new StringLiteralNode(
                    (String)node.getLeft().getNodeValue().getValue() + (String)node.getRight().getNodeValue().getValue()
            ));
        }

        //TODO: Add rest of addition node things
        return null;
    }

    @Override
    public Void visit(AndNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        BoolLiteralNode left = (BoolLiteralNode) node.getLeft().getNodeValue();
        BoolLiteralNode right = (BoolLiteralNode) node.getRight().getNodeValue();

        node.setNodeValue(new BoolLiteralNode(left.getValue() && right.getValue()));
        return null;
    }

    @Override
    public Void visit(ArrayAppendNode node) {
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
        node.getLeft().accept(this);
        node.getRight().accept(this);
        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
            node.setNodeValue(
                    new BoolLiteralNode(
                        ToFloat(node.getLeft().getNodeValue().getValue()).equals(ToFloat(node.getRight().getNodeValue().getValue()))
                    )
            );
        } else if (childType instanceof StringType || childType instanceof CharType || childType instanceof BoolType) {
            node.setNodeValue(
                    new BoolLiteralNode(
                            (node.getLeft().getNodeValue().getValue()).equals((node.getRight().getNodeValue().getValue()))
                    )
            );
        }

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
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
            node.setNodeValue(new BoolLiteralNode(
                    ToFloat(node.getLeft().getNodeValue()) > ToFloat(node.getRight().getNodeValue())
            ));
        } else if (childType instanceof CharType) {
            node.setNodeValue(new BoolLiteralNode(
                    ((CharLiteralNode) node.getLeft().getNodeValue()).getValue() > ((CharLiteralNode) node.getRight().getNodeValue()).getValue()
            ));
        }
        return null;
    }

    @Override
    public Void visit(GreaterThanOrEqualNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
            node.setNodeValue(new BoolLiteralNode(
                    ToFloat(node.getLeft().getNodeValue()) >= ToFloat(node.getRight().getNodeValue())
            ));
        } else if (childType instanceof CharType) {
            node.setNodeValue(new BoolLiteralNode(
                    ((CharLiteralNode) node.getLeft().getNodeValue()).getValue() >= ((CharLiteralNode) node.getRight().getNodeValue()).getValue()
            ));
        }
        return null;
    }

    @Override
    public Void visit(IfNode node) {
        ArithmeticExpressionNode predicate = node.getPredicate();
        predicate.accept(this);
        if (((BoolLiteralNode)predicate.getNodeValue()).getValue()) {
            node.getTrueBranch().accept(this);
        } else {
            node.getFalseBranch().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(LessThanNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
            node.setNodeValue(new BoolLiteralNode(
                    ToFloat(node.getLeft().getNodeValue()) < ToFloat(node.getRight().getNodeValue())
            ));
        } else if (childType instanceof CharType) {
            node.setNodeValue(new BoolLiteralNode(
                    ((CharLiteralNode) node.getLeft().getNodeValue()).getValue() < ((CharLiteralNode) node.getRight().getNodeValue()).getValue()
            ));
        }
        return null;
    }

    @Override
    public Void visit(LessThanOrEqualNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
            node.setNodeValue(new BoolLiteralNode(
                    ToFloat(node.getLeft().getNodeValue()) <= ToFloat(node.getRight().getNodeValue())
            ));
        } else if (childType instanceof CharType) {
            node.setNodeValue(new BoolLiteralNode(
                    ((CharLiteralNode) node.getLeft().getNodeValue()).getValue() <= ((CharLiteralNode) node.getRight().getNodeValue()).getValue()
            ));
        }
        return null;
    }

    @Override
    public Void visit(ModuloNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        int a1 = ((IntLiteralNode)node.getLeft().getNodeValue()).getValue();
        int a2 = ((IntLiteralNode)node.getRight().getNodeValue()).getValue();

        node.setNodeValue(new IntLiteralNode(
                a1 % a2
        ));
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
        node.getExpression().accept(this);

        BoolLiteralNode child = (BoolLiteralNode) node.getExpression().getNodeValue();

        node.setNodeValue(new BoolLiteralNode(!child.getValue()));
        return null;
    }

    @Override
    public Void visit(NotEqualNode node) {
        return null;
    }

    @Override
    public Void visit(OrNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        BoolLiteralNode left = (BoolLiteralNode) node.getLeft().getNodeValue();
        BoolLiteralNode right = (BoolLiteralNode) node.getRight().getNodeValue();

        node.setNodeValue(new BoolLiteralNode(left.getValue() || right.getValue()));
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
        node.getLeft().accept(this);
        node.getRight().accept(this);

        String text = ((StringLiteralNode)node.getLeft().getNodeValue()).getValue();
        String pattern = ((StringLiteralNode)node.getRight().getNodeValue()).getValue();

        node.setNodeValue(new BoolLiteralNode(
                text.matches(pattern)
        ));
        return null;
    }

    private static Float ToFloat(Object o) {
        if (o instanceof Float) return (Float)o;
        if (o instanceof Integer) {
            return ((Integer)o).floatValue();
        }
        return null;
    }
}
