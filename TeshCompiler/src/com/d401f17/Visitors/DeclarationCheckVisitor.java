package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;
import com.d401f17.Helper;
import com.d401f17.TypeSystem.*;
import jdk.nashorn.internal.codegen.types.BooleanType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hense on 5/4/17.
 */
public class DeclarationCheckVisitor extends BaseVisitor<Void> {
    private List<Type> errorNodes = new ArrayList<>();
    private SymTab st;
    private SymTab rt;

    public DeclarationCheckVisitor() {
        this.st = new SymbolTable();
        this.rt = new SymbolTable();
    }

    public DeclarationCheckVisitor(SymTab symbolTable, SymTab recordTable) {
        this.st = symbolTable;
        this.rt = recordTable;
    }

    public List<Type> getErrorNodes() {
        Collections.sort(errorNodes);
        return errorNodes;
    }

    public List<String> getErrors() {
        ArrayList<String> errorStrings = new ArrayList<>();
        for (Type errorType : getErrorNodes()) {
            errorStrings.add(errorType.getErrorMessage());
        }

        return errorStrings;
    }

    public String getAllErrors() {
        StringBuilder sb = new StringBuilder();
        for (String error : getErrors()) {
            sb.append(error);
            sb.append('\n');
        }

        return sb.toString();
    }

    @Override
    public Void visit(AdditionNode node) {
        return null;
    }

    @Override
    public Void visit(AndNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayAppendNode node) {
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
        return null;
    }

    @Override
    public Void visit(AST node) {
        return null;
    }

    @Override
    public Void visit(LiteralNode node) { return null; }

    @Override
    public Void visit(IntLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(BoolLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(FloatLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(StringLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(CharLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(RecordLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(DivisionNode node) {
        return null;
    }

    @Override
    public Void visit(EqualNode node) {
        return null;
    }

    @Override
    public Void visit(ForkNode node) {
        node.getChild().accept(this);

        return null;
    }

    @Override
    public Void visit(ForNode node) {
        node.getStatements().accept(this);

        return null;
    }

    @Override
    public Void visit(FunctionCallNode node) {
        return null;
    }

    @Override
    public Void visit(FunctionNode node) {
        List<VariableDeclarationNode> arguments = node.getFormalArguments();
        Type[] argumentTypes = new Type[arguments.size()];

        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).accept(this);
            argumentTypes[i] = arguments.get(i).getTypeNode().getType();
        }

        String funcName = node.getName().getName();
        Type funcType = node.getTypeNode().getType();

        FunctionType function = new FunctionType(funcName, argumentTypes, funcType);
        try {
            st.insert(function.toString(), new FunctionSymbol(function, node, new SymbolTable((SymbolTable)st)));
            node.setType(funcType);
        } catch (VariableAlreadyDeclaredException e) {
            Type err = new ErrorType(node.getLine(), "Function with signature " + e.getMessage());
            errorNodes.add(err);
            node.setType(err);
            return null;
        }

        node.getStatements().accept(this);

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
        node.getTrueBranch().accept(this);
        node.getFalseBranch().accept(this);

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
        String recordName = node.getName();
        Type record = new RecordType(recordName);

        try {
            rt.insert(recordName, new Symbol(record, node));
        } catch (VariableAlreadyDeclaredException e) {
            errorNodes.add(new ErrorType(node.getLine(), "Record " + e.getMessage()));
        }

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
        List<StatementNode> childNodes = node.getChildren();

        for (StatementNode childNode : childNodes) {
            childNode.accept(this);
        }

        return null;
    }

    @Override
    public Void visit(SubtractionNode node) {
        return null;
    }

    @Override
    public Void visit(TypeNode node) {
        return null;
    }

    @Override
    public Void visit(VariableDeclarationNode node) {
        Type varType = node.getTypeNode().getType();

        if (varType instanceof RecordType) {
            try {
                varType = rt.lookup(((RecordType)varType).getName()).getType();

                //The typenode of a record doesn't know anything about the members of the record
                node.getTypeNode().setType(varType);
            } catch (VariableNotDeclaredException e) {
                errorNodes.add(new ErrorType(node.getLine(), "Record " + e.getMessage()));
                return null;
            }
        }
        return null;
    }

    @Override
    public Void visit(WhileNode node) {
        node.getStatements().accept(this);

        return null;
    }

    @Override
    public Void visit(ProcedureCallNode node) {
        return null;
    }

    @Override
    public Void visit(ProgramNode node) {
        node.getChild().accept(this);

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

    private void openScope() {
        st.openScope();
        rt.openScope();
    }

    private void closeScope() {
        st.closeScope();
        rt.closeScope();
    }
}
