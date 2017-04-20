package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TeshBaseVisitor;
import com.d401f17.TeshParser;
import com.d401f17.AST.TypeSystem.Types;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by mathias on 3/15/17.
 */
public class BuildAstVisitor extends TeshBaseVisitor<AST>{
    @Override
    public AST visitCompileUnit(TeshParser.CompileUnitContext ctx) {
        StatementsNode prog = new StatementsNode(ctx.start.getLine());
        for (ParseTree tree : ctx.statement()) {
            AST child = visit(tree);
            if (child != null) {
                prog.getChildren().add((StatementNode) child);
            }
        }
        return prog;
    }

    @Override
    public AST visitForkSimpleStatement(TeshParser.ForkSimpleStatementContext ctx) {
        return new ForkNode(
                (StatementNode) visit(ctx.simpleStatement()),
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitAssignmentStatement(TeshParser.AssignmentStatementContext ctx) {
        return new AssignmentNode(
                (IdentifierNode) visit(ctx.identifier()),
                (ArithmeticExpressionNode) visit(ctx.expression()),
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitNewlineStatement(TeshParser.NewlineStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitCompoundAssignment(TeshParser.CompoundAssignmentContext ctx) {
        IdentifierNode variable = (IdentifierNode) visit(ctx.identifier());
        ArithmeticExpressionNode expression = (ArithmeticExpressionNode) visit(ctx.expression());
        int lineNum = ctx.start.getLine();

        switch (ctx.op.getType()) {
            case TeshParser.OP_INCREMENT:
                return new AssignmentNode(variable, new AdditionNode(variable, expression, lineNum), lineNum);
            case TeshParser.OP_DECREMENT:
                return new AssignmentNode(variable, new SubtractionNode(variable, expression, lineNum), lineNum);
            case TeshParser.OP_SCALE:
                return new AssignmentNode(variable, new MultiplicationNode(variable, expression, lineNum), lineNum);
            case TeshParser.OP_DIVIDE:
                return new AssignmentNode(variable, new DivisionNode(variable, expression, lineNum), lineNum);
        }
        return null;
    }

    @Override
    public AST visitForStatement(TeshParser.ForStatementContext ctx) {
        visit(ctx.block());
        return new ForNode(
                new SimpleIdentifierNode(
                        ctx.SIMPLE_IDENTIFIER().getText(),
                        ctx.start.getLine()
                ),
                visit(ctx.identifier()),
                visit(ctx.block()),
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitExecuteShellCommandIntoChannelStatement(TeshParser.ExecuteShellCommandIntoChannelStatementContext ctx) {
        return new ShellToChannelNode(
                new SimpleIdentifierNode(
                        ctx.SIMPLE_IDENTIFIER().getText(),
                        ctx.start.getLine()
                ),
                new ShellNode(
                        (ArithmeticExpressionNode) visit(ctx.expression()),
                        ctx.start.getLine()
                ),
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitIfStatement(TeshParser.IfStatementContext ctx) {
        return new IfNode(
                (ArithmeticExpressionNode) visit(ctx.expression()),
                (StatementsNode) visit(ctx.trueBranch),
                (StatementsNode) visit(ctx.falseBranch),
                ctx.start.getLine()
        );
    }


    @Override
    public AST visitFunctionCall(TeshParser.FunctionCallContext ctx) {
        FunctionCallNode node =  new FunctionCallNode(
                (IdentifierNode) visit(ctx.identifier()),
                ctx.start.getLine()
        );
        for (ParseTree argument : ctx.expression()) {
            node.getArguments().add((ArithmeticExpressionNode) visit(argument));
        }
        return node;
    }

    @Override
    public AST visitReturnStatement(TeshParser.ReturnStatementContext ctx) {
        ArithmeticExpressionNode expNode;
        if (ctx.expression() == null) {
            expNode = new LiteralNode(0, Types.VOID);
        } else {
            expNode = (ArithmeticExpressionNode) visit(ctx.expression());
        }

        return new ReturnNode(
                expNode,
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitFunctionCallStatement(TeshParser.FunctionCallStatementContext ctx) {
        ProcedureCallNode node =  new ProcedureCallNode(
                (IdentifierNode) visit(ctx.functionCall().identifier()),
                ctx.start.getLine()
        );
        for (ParseTree argument : ctx.functionCall().expression()) {
            node.getArguments().add((ArithmeticExpressionNode) visit(argument));
        }
        return node;
    }

    @Override
    public AST visitBackgroundExecuteShellCommandStatement(TeshParser.BackgroundExecuteShellCommandStatementContext ctx) {
        return new ForkNode(
                new ShellNode(
                        (ArithmeticExpressionNode) visit(ctx.expression()),
                        ctx.start.getLine()
                ),
                ctx.start.getLine()
        );
    }
/*
    @Override
    public AST visitReadFromChannelStatementToArray(TeshParser.ReadFromChannelStatementToArrayContext ctx) {
        return new ChannelNode(
                (ArrayAccessNode) visit(ctx.arrayAccess()),
                new SimpleIdentifierNode(
                        ctx.SIMPLE_IDENTIFIER().getText(),
                        ctx.start.getLine()
                ),
                ctx.start.getLine()
        );
    }
*/
    @Override
    public AST visitCompoundArrayStatement(TeshParser.CompoundArrayStatementContext ctx) {
        ArrayAccessNode element = (ArrayAccessNode) visit(ctx.arrayAccess());
        ArithmeticExpressionNode expression = (ArithmeticExpressionNode) visit(ctx.expression());
        int lineNum = ctx.start.getLine();

        switch (ctx.op.getType()) {
            case TeshParser.OP_INCREMENT:
                return new ArrayElementAssignmentNode(element, new AdditionNode(element, expression, lineNum), lineNum);
            case TeshParser.OP_DECREMENT:
                return new AssignmentNode(element, new SubtractionNode(element, expression, lineNum), lineNum);
            case TeshParser.OP_SCALE:
                return new AssignmentNode(element, new MultiplicationNode(element, expression, lineNum), lineNum);
            case TeshParser.OP_DIVIDE:
                return new AssignmentNode(element, new DivisionNode(element, expression, lineNum), lineNum);
        }

        return null;
    }

    @Override
    public AST visitExecuteShellCommandStatement(TeshParser.ExecuteShellCommandStatementContext ctx) {
        return new ShellNode(
                (ArithmeticExpressionNode)visit(ctx.expression()),
                ctx.start.getLine()
        );
    }


    @Override
    public AST visitWhileStatement(TeshParser.WhileStatementContext ctx) {
        return new WhileNode(
                visit(ctx.expression()), visit(ctx.block()),
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitArrayElementAssignmentStatement(TeshParser.ArrayElementAssignmentStatementContext ctx) {
        List<ArithmeticExpressionNode> indices = new ArrayList<>();
        for (ParseTree index : ctx.arrayAccess().expression()) {
            indices.add((ArithmeticExpressionNode) visit(index));
        }

        return new ArrayElementAssignmentNode(
                (ArrayAccessNode) visit(ctx.arrayAccess()),
                (ArithmeticExpressionNode) visit(ctx.expression()),
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitVariableDeclarationStatement(TeshParser.VariableDeclarationStatementContext ctx) {
        VariableDeclarationNode node = (VariableDeclarationNode)visit(ctx.variableDeclaration());
        int lineNum = ctx.start.getLine();

        if (ctx.expression() != null) {
            return new StatementsNode(
                lineNum,
                node,
                new AssignmentNode(
                    new SimpleIdentifierNode(
                        node.getName().getName(),
                        lineNum
                    ),
                    (ArithmeticExpressionNode)visit(ctx.expression()),
                    lineNum
                )
            );
        }
        return node;
    }

    @Override
    public AST visitArrayBuilderStatement(TeshParser.ArrayBuilderStatementContext ctx) {
        int lineNum = ctx.start.getLine();

        return new ArrayBuilderNode(
                new SimpleIdentifierNode(
                        ctx.variableName.getText(),
                        lineNum
                ),
                (IdentifierNode) visit(ctx.arrayName),
                (ArithmeticExpressionNode) visit(ctx.expression()),
                lineNum
        );
    }

    @Override
    public AST visitMultipleStatements(TeshParser.MultipleStatementsContext ctx) {
        StatementsNode node = new StatementsNode(ctx.start.getLine());
        if (ctx.children != null) {
            for (ParseTree subtree : ctx.children) {
                StatementNode child = (StatementNode) visit(subtree);
                if (child != null) {
                    if (child instanceof StatementsNode) {
                        StatementsNode childNodes = (StatementsNode) child;
                        node.getChildren().addAll(childNodes.getChildren());
                    } else {
                        node.getChildren().add(child);
                    }
                }
            }
        }
        return node;
    }

    @Override
    public AST visitForkCompoundStatement(TeshParser.ForkCompoundStatementContext ctx) {
        return new ForkNode(
            (StatementNode) visit(ctx.compoundStatement()),
            ctx.start.getLine()
        );
    }

    @Override
    public AST visitLogicalComparison(TeshParser.LogicalComparisonContext ctx) {
        int lineNum = ctx.start.getLine();

        switch (ctx.op.getType()) {
            case TeshParser.OP_AND:
                return new AndNode(
                        (ArithmeticExpressionNode) visit(ctx.boolm()),
                        (ArithmeticExpressionNode) visit(ctx.boolc()),
                        lineNum
                );
            case TeshParser.OP_OR:
                return new OrNode(
                        (ArithmeticExpressionNode) visit(ctx.boolm()),
                        (ArithmeticExpressionNode) visit(ctx.boolc()),
                        lineNum
                );
            default:
                return null;
        }
    }

    @Override
    public AST visitBoolComparison(TeshParser.BoolComparisonContext ctx) {
        ArithmeticExpressionNode left = (ArithmeticExpressionNode) visit(ctx.boolc());
        ArithmeticExpressionNode right = (ArithmeticExpressionNode)  visit(ctx.arithmeticExpression());
        int lineNum = ctx.start.getLine();

        switch (ctx.op.getType()) {
            case TeshParser.OP_EQ:
                return new EqualNode(left, right, lineNum);
            case TeshParser.OP_NEQ:
                return new NotEqualNode(left, right, lineNum);
            case TeshParser.OP_LT:
                return new LessThanNode(left, right, lineNum);
            case TeshParser.OP_GT:
                return new GreaterThanNode(left, right, lineNum);
            case TeshParser.OP_LEQ:
                return new LessThanOrEqualNode(left, right, lineNum);
            case TeshParser.OP_GEQ:
                return new GreaterThanOrEqualNode(left, right, lineNum);
            case TeshParser.OP_PAT:
                return new PatternMatchNode(left, right, lineNum);
        }
        return null;
    }

    @Override
    public AST visitArithmeticExpr(TeshParser.ArithmeticExprContext ctx) {
        ArithmeticExpressionNode left = (ArithmeticExpressionNode) visit(ctx.arithmeticExpression());
        ArithmeticExpressionNode right = (ArithmeticExpressionNode) visit(ctx.term());
        int lineNum = ctx.start.getLine();

        switch (ctx.op.getType()) {
            case TeshParser.OP_ADD:
                return new AdditionNode(left, right, lineNum);
            case TeshParser.OP_SUB:
                return new SubtractionNode(left, right, lineNum);
        }
        return null;
    }

    @Override
    public AST visitTermExpr(TeshParser.TermExprContext ctx) {
        ArithmeticExpressionNode left = (ArithmeticExpressionNode) visit(ctx.term());
        ArithmeticExpressionNode right = (ArithmeticExpressionNode) visit(ctx.value());
        int lineNum = ctx.start.getLine();

        switch (ctx.op.getType()) {
            case TeshParser.OP_MUL:
                return new MultiplicationNode(left, right, lineNum);
            case TeshParser.OP_DIV:
                return new DivisionNode(left, right, lineNum);
            case TeshParser.OP_MOD:
                return new ModuloNode(left, right, lineNum);
        }
        return null;
    }


    @Override
    public AST visitUnaryOperator(TeshParser.UnaryOperatorContext ctx) {
        if (ctx.op.getType() == TeshParser.OP_SUB) {
            return new MultiplicationNode(
                    (ArithmeticExpressionNode) visit(ctx.value()),
                    new LiteralNode(
                            -1,
                            Types.INT
                    ),
                    ctx.start.getLine()
            );
        }
        return visit(ctx.value());
    }

    @Override
    public AST visitNegateOperator(TeshParser.NegateOperatorContext ctx) {
        return new NegationNode(
                visit(ctx.finalValue()),
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitIdentifier(TeshParser.IdentifierContext ctx) {
        if (ctx.SIMPLE_IDENTIFIER() != null) {
            return new SimpleIdentifierNode(
                    ctx.SIMPLE_IDENTIFIER().getText(),
                    ctx.start.getLine()
            );
        } else {
            //Record identifier
            String[] subIdentifiers = ctx.IDENTIFIER().getText().split("\\.");
            int linenum = ctx.start.getLine();
            SimpleIdentifierNode terminal = new SimpleIdentifierNode(subIdentifiers[subIdentifiers.length - 1], linenum);
            RecordIdentifierNode first = new RecordIdentifierNode(terminal, subIdentifiers[0], linenum);
            RecordIdentifierNode latest = first;
            for (int i = 1; i < subIdentifiers.length - 1; i++) {
                latest.setChild(new RecordIdentifierNode(latest.getChild(), subIdentifiers[i], linenum));
                latest = (RecordIdentifierNode) latest.getChild();
            }
            return first;
        }
    }

    @Override
    public AST visitArrayConstant(TeshParser.ArrayConstantContext ctx) {
        List<ArithmeticExpressionNode> values = new ArrayList<>();
        if (ctx.arithmeticExpression() != null) {
            for (ParseTree child : ctx.arithmeticExpression()) {
                values.add((ArithmeticExpressionNode)visit(child));
            }
        }
        return new ArrayLiteralNode(values, ctx.start.getLine());
    }

    @Override
    public AST visitRecordDeclaration(TeshParser.RecordDeclarationContext ctx) {
        List<VariableDeclarationNode> variables = new ArrayList<>();

        for (ParseTree var : ctx.variableDeclaration()) {
            variables.add((VariableDeclarationNode) visit(var));
        }

        return new RecordDeclarationNode(
                ctx.SIMPLE_IDENTIFIER().getText(),
                variables,
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitVariableDeclaration(TeshParser.VariableDeclarationContext ctx) {
        return new VariableDeclarationNode(
                new SimpleIdentifierNode(
                        ctx.SIMPLE_IDENTIFIER().getText(),
                        ctx.start.getLine()
                ),
                (TypeNode) visit(ctx.type()),
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitFunctionDeclaration(TeshParser.FunctionDeclarationContext ctx) {
        List<VariableDeclarationNode> formalArgs = new ArrayList<>();
        int numberOfArgs = ctx.SIMPLE_IDENTIFIER().size();
        int lineNum = ctx.start.getLine();
        // Skip first simpleIdentifier as the name of the function is the first element
        for (int i = 1; i < numberOfArgs; i++) {
            formalArgs.add(
                    new VariableDeclarationNode(
                        new SimpleIdentifierNode(
                            ctx.SIMPLE_IDENTIFIER(i).getText(),
                            ctx.start.getLine()
                        ),
                        //The return type of the function is the last element of the type list
                        //so we need to subtract one access this array as 0-indexed
                        new TypeNode(ctx.type(i - 1).getText()),
                        lineNum
                    )
            );
        }

        return new FunctionNode(
                new SimpleIdentifierNode(
                        ctx.name.getText(),
                        lineNum
                ),
                new TypeNode(ctx.returntype.getText()),
                formalArgs,
                (StatementsNode)visit(ctx.block()),
                lineNum
        );

    }

    @Override
    public AST visitArrayAccess(TeshParser.ArrayAccessContext ctx) {
        List<ArithmeticExpressionNode> indices = new ArrayList<>();
        for (ParseTree index : ctx.expression()) {
            indices.add((ArithmeticExpressionNode) visit(index));
        }

        return new ArrayAccessNode(
                (IdentifierNode) visit(ctx.identifier()),
                indices,
                ctx.start.getLine()
        );
    }

    @Override
    public AST visitConstant(TeshParser.ConstantContext ctx) {
        if (ctx.BOOL_LITERAL() != null) {
            return new BoolLiteralNode(Objects.equals(ctx.BOOL_LITERAL().getText(), "true"));
        } else if (ctx.CHAR_LITERAL() != null) {
            return new LiteralNode(ctx.CHAR_LITERAL().getText().charAt(1), Types.CHAR);
        } else if (ctx.FLOAT_LITERAL() != null) {
            return new FloatLiteralNode(Float.parseFloat(ctx.FLOAT_LITERAL().getText()));
        } else if (ctx.INT_LITERAL() != null) {
            return new IntLiteralNode(Integer.parseInt(ctx.INT_LITERAL().getText()));
        } else {
            return new StringLiteralNode(ctx.STRING_LITERAL().getText().substring(1, ctx.STRING_LITERAL().getText().length() - 1));
        }
    }

    @Override
    public AST visitType(TeshParser.TypeContext ctx) {
        return new TypeNode(ctx.getText());
    }

    @Override
    public AST visitBlock(TeshParser.BlockContext ctx) {
        return visit(ctx.multipleStatements());
    }

    @Override
    public AST visitParenthesisExpr(TeshParser.ParenthesisExprContext ctx) {
        return visit(ctx.arithmeticExpression());
    }
}