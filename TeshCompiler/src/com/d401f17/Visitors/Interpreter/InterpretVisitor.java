package com.d401f17.Visitors.Interpreter;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.BaseVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * Created by mathias on 4/27/17.
 */
public class InterpretVisitor extends BaseVisitor<LiteralNode> {
    private Store store = new Store();
    private SymTab symtab = new SymbolTable();

    @Override
    public LiteralNode visit(AdditionNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);
        LiteralNode result = null;
        if (node.getType() instanceof IntType) {
            result = new IntLiteralNode((int)a1.getValue() + (int)a2.getValue());
        } else if (node.getType() instanceof FloatType) {
            result = new FloatLiteralNode(ToFloat(a1.getValue()) + ToFloat(a2.getValue()));
        } else if (node.getLeft().getType() instanceof StringType) {
            result = new StringLiteralNode(a1.getValue().toString() + a2.getValue().toString());
        }

        //TODO: Add rest of addition node things
        return result;
    }

    @Override
    public LiteralNode visit(AndNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);

        BoolLiteralNode left = (BoolLiteralNode)a1;
        BoolLiteralNode right = (BoolLiteralNode)a2;

        return new BoolLiteralNode(left.getValue() && right.getValue());
    }

    @Override
    public LiteralNode visit(ArrayAppendNode node) {
        return null;
    }


    @Override
    public LiteralNode visit(ArrayAccessNode node) {
        Symbol entry;

        try {
            entry = symtab.lookup(node.getArray().getName());
            ValueArrayLiteralNode values = (ValueArrayLiteralNode) store.getElement(entry.getAddress());
            List<ArithmeticExpressionNode> indices = node.getIndices();
            for (int i = 0, indicesSize = indices.size(); i < indicesSize - 1; i++) {
                ArithmeticExpressionNode child = indices.get(i);
                IntLiteralNode index = (IntLiteralNode)child.accept(this);
                values = (ValueArrayLiteralNode)values.getValue().get(index.getValue());
            }
            ArithmeticExpressionNode last = node.getIndices().get(node.getIndices().size() - 1);
            IntLiteralNode index = (IntLiteralNode)last.accept(this);
            return values.getValue().get(index.getValue());
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiteralNode visit(ArrayBuilderNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(ArrayLiteralNode node) {
        List<LiteralNode> childValues = new ArrayList<>();
        for (ArithmeticExpressionNode child : node.getValue()) {

            childValues.add((LiteralNode) child.accept(this));
        }
        return new ValueArrayLiteralNode(childValues);
    }

    @Override
    public LiteralNode visit(ArrayElementAssignmentNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(AssignmentNode node) {
        LiteralNode value = (LiteralNode)node.getExpression().accept(this);
        String name = node.getVariable().toString();
        Symbol entry = null;
        try {
            entry = symtab.lookup(name);
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        if (node.getVariable().getType() instanceof FloatType) {
            store.setElement(entry.getAddress(), ToFloat(value.getValue()));
        }
        store.setElement(entry.getAddress(), value);
        return null;
    }

    @Override
    public LiteralNode visit(AST node) {
        return null;
    }

    @Override
    public LiteralNode visit(LiteralNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(IntLiteralNode node) {
        return node;
    }

    @Override
    public LiteralNode visit(BoolLiteralNode node) {
        return node;
    }

    @Override
    public LiteralNode visit(FloatLiteralNode node) {
        return node;
    }

    @Override
    public LiteralNode visit(StringLiteralNode node) {
        return node;
    }

    @Override
    public LiteralNode visit(CharLiteralNode node) {
        return node;
    }

    @Override
    public LiteralNode visit(RecordLiteralNode node) {
        return node;
    }

    @Override
    public LiteralNode visit(DivisionNode node) {
        LiteralNode a1 = (LiteralNode)node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode)node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            return new IntLiteralNode((int)a1.getValue() / (int)a2.getValue());
        } else {
            return new FloatLiteralNode(ToFloat(a2.getValue()) / ToFloat(a2.getValue()));
        }
    }

    @Override
    public LiteralNode visit(EqualNode node) {
        LiteralNode a1 = (LiteralNode)node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode)node.getRight().accept(this);
        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
            return new BoolLiteralNode(ToFloat(a1.getValue()).equals(ToFloat(a2.getValue())));
        } else if (childType instanceof StringType || childType instanceof CharType || childType instanceof BoolType) {
            return new BoolLiteralNode(a1.getValue().equals(a2.getValue()));
        }
        return null;
    }

    @Override
    public LiteralNode visit(ForkNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(ForNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(FunctionCallNode node) {

        HashMap<String, Function<LiteralNode, LiteralNode>> standardFunctions= new HashMap<>();
        standardFunctions.put("str(FLOAT)", StandardLib::LiteralToString);
        standardFunctions.put("str(CHAR)", StandardLib::LiteralToString);
        standardFunctions.put("str(STRING)", StandardLib::LiteralToString);
        standardFunctions.put("str(INT)", StandardLib::LiteralToString);
        standardFunctions.put("str(BOOL)", StandardLib::LiteralToString);
        standardFunctions.put("intval(FLOAT)", StandardLib::FloatToInt);

        List<LiteralNode> argResults = new ArrayList<>();
        Type[] argumentTypes = new Type[node.getArguments().size()];

        //Visit argument nodes
        for (int i = 0; i < node.getArguments().size(); i++) {
            argResults.add((LiteralNode)node.getArguments().get(i).accept(this));
            argumentTypes[i] = node.getArguments().get(i).getType();
        }

        FunctionType func = new FunctionType(node.getName().getName(), argumentTypes, new VoidType());
        String signature = func.getSignature();
        if (standardFunctions.containsKey(signature)) {
            Function stdFunc = standardFunctions.get(func.getSignature());
            return ((LiteralNode) stdFunc.apply(argResults.get(0)));
        }

        return null;
    }

    @Override
    public LiteralNode visit(FunctionNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(GreaterThanNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);

        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
            return new BoolLiteralNode(ToFloat(a1.getValue()) > ToFloat(a2.getValue()));
        } else if (childType instanceof CharType) {
            return new BoolLiteralNode(((CharLiteralNode) a1).getValue() > ((CharLiteralNode) a2).getValue());
        }
        return null;
    }

    @Override
    public LiteralNode visit(GreaterThanOrEqualNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);

        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
            return new BoolLiteralNode(ToFloat(a1.getValue()) >= ToFloat(a2.getValue()));
        } else if (childType instanceof CharType) {
            return new BoolLiteralNode(((CharLiteralNode) a1).getValue() >= ((CharLiteralNode) a2).getValue());
        }
        return null;
    }

    @Override
    public LiteralNode visit(IfNode node) {
        BoolLiteralNode predicate = (BoolLiteralNode)node.getPredicate().accept(this);
        if (predicate.getValue()) {
            return (LiteralNode)node.getTrueBranch().accept(this);
        } else {
            return (LiteralNode)node.getFalseBranch().accept(this);
        }
    }

    @Override
    public LiteralNode visit(LessThanNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);

        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
             return new BoolLiteralNode(ToFloat(a1.getValue()) < ToFloat(a2.getValue()));
        } else if (childType instanceof CharType) {
            return new BoolLiteralNode(((CharLiteralNode) a1).getValue() < ((CharLiteralNode) a2).getValue());
        }
        return null;
    }

    @Override
    public LiteralNode visit(LessThanOrEqualNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);

        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
            return new BoolLiteralNode(ToFloat(a1.getValue()) <= ToFloat(a2.getValue()));
        } else if (childType instanceof CharType) {
            return new BoolLiteralNode(((CharLiteralNode) a1).getValue() <= ((CharLiteralNode) a2).getValue());
        }
        return null;
    }

    @Override
    public LiteralNode visit(ModuloNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);

        return new IntLiteralNode((int)a1.getValue() % (int)a2.getValue());
    }

    @Override
    public LiteralNode visit(MultiplicationNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            return new IntLiteralNode((int)a1.getValue() * (int)a2.getValue());
        } else {
            return new FloatLiteralNode(ToFloat(a1.getValue()) * ToFloat(a2.getValue()));
        }
    }

    @Override
    public LiteralNode visit(NegationNode node) {
        BoolLiteralNode a = (BoolLiteralNode)node.getExpression().accept(this);

        return new BoolLiteralNode(!a.getValue());
    }

    @Override
    public LiteralNode visit(NotEqualNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(OrNode node) {
        BoolLiteralNode a1 = (BoolLiteralNode) node.getLeft().accept(this);
        BoolLiteralNode a2 = (BoolLiteralNode) node.getRight().accept(this);

        return new BoolLiteralNode(a1.getValue() || a2.getValue());
    }

    @Override
    public LiteralNode visit(RecordDeclarationNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(RecordIdentifierNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(ReturnNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(ShellNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(ShellToChannelNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(SimpleIdentifierNode node) {
        try {
            Symbol entry = symtab.lookup(node.getName());
            return (LiteralNode)store.getElement(entry.getAddress());

        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiteralNode visit(StatementsNode node) {
        for (StatementNode child : node.getChildren()) {
            LiteralNode res = (LiteralNode) child.accept(this);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    @Override
    public LiteralNode visit(SubtractionNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);

        if (node.getType() instanceof IntType) {
            return new IntLiteralNode((int)a1.getValue() - (int)a2.getValue());
        } else {
            return new FloatLiteralNode(ToFloat(a1.getValue()) - ToFloat(a2.getValue()));
        }
    }

    @Override
    public LiteralNode visit(TypeNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(VariableDeclarationNode node) {
        try {
            Symbol entry = new Symbol(node.getName().getType(), node);
            entry.setAddress(store.getNext());
            symtab.insert(node.getName().getName(), entry);
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiteralNode visit(WhileNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(ProcedureCallNode node) {
        return null;
    }

    @Override
    public LiteralNode visit(ChannelNode node) {
        StringLiteralNode output = (StringLiteralNode) node.getExpression().accept(this);
        if (node.getIdentifier().getName().equals("stdio")) {
            System.out.println(output.getValue());
        }
        return null;
    }

    @Override
    public LiteralNode visit(PatternMatchNode node) {
        String text = ((StringLiteralNode)node.getLeft().accept(this)).getValue();
        String pattern = ((StringLiteralNode)node.getRight().accept(this)).getValue();

        return new BoolLiteralNode(text.matches(pattern));
    }

    private static Float ToFloat(Object o) {
        if (o instanceof Float) return (Float)o;
        if (o instanceof Integer) {
            return ((Integer)o).floatValue();
        }
        return null;
    }
}
