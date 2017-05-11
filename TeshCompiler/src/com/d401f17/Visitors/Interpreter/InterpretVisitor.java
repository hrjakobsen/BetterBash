package com.d401f17.Visitors.Interpreter;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.TypeSystem.SymbolTable.*;
import com.d401f17.Visitors.BaseVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;

/**
 * Created by mathias on 4/27/17.
 */
public class InterpretVisitor extends BaseVisitor<LiteralNode> {
    private HashMap<String, Function<LiteralNode[], LiteralNode>> standardFunctions= new HashMap<>();

    public InterpretVisitor() {
        this(new SymbolTable());
    }

    private Store store = new Store();
    private SymbolTable symtab = new SymbolTable();
    private SymTab recTable;

    public InterpretVisitor(SymTab recTable) {
        standardFunctions.put("str(FLOAT)", StandardLib::LiteralToString);
        standardFunctions.put("str(CHAR)", StandardLib::LiteralToString);
        standardFunctions.put("str(STRING)", StandardLib::LiteralToString);
        standardFunctions.put("str(INT)", StandardLib::LiteralToString);
        standardFunctions.put("str(BOOL)", StandardLib::LiteralToString);
        standardFunctions.put("intval(FLOAT)", StandardLib::FloatToInt);
        standardFunctions.put("print(STRING)", StandardLib::Print);
        standardFunctions.put("read()", StandardLib::Read);
        this.recTable = recTable;
    }

    @Override
    public LiteralNode visit(AdditionNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);
        LiteralNode result = null;
        if (node.getType() instanceof IntType) {
            result = new IntLiteralNode(Math.addExact((long)a1.getValue(), (long)a2.getValue()));
        } else if (node.getType() instanceof FloatType) {
            result = new FloatLiteralNode(ToFloat(a1.getValue()) + ToFloat(a2.getValue()));
        } else if (node.getLeft().getType() instanceof StringType) {
            result = new StringLiteralNode(a1.getValue().toString() + a2.getValue().toString());
        } else if (node.getLeft().getType() instanceof CharType && node.getRight().getType() instanceof IntType) {
            int asciiValue = (char)a1.getValue();
            long value = (long)a2.getValue();
            result = new CharLiteralNode((char)(asciiValue + value));
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
        Symbol entry;

        try {
            entry = symtab.lookup(node.getVariable().getName());
            ValueArrayLiteralNode array = (ValueArrayLiteralNode) store.getElement(entry.getAddress());
            array.getValue().add((LiteralNode) node.expression.accept(this));
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
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
                values = (ValueArrayLiteralNode)values.getValue().get(index.getValue().intValue());
            }
            ArithmeticExpressionNode last = node.getIndices().get(node.getIndices().size() - 1);
            IntLiteralNode index = (IntLiteralNode)last.accept(this);
            return values.getValue().get(index.getValue().intValue());
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiteralNode visit(ArrayBuilderNode node) {
        ValueArrayLiteralNode array = (ValueArrayLiteralNode)node.getArray().accept(this);
        ValueArrayLiteralNode newArray = new ValueArrayLiteralNode(new ArrayList<>());
        try {
            for (LiteralNode child : array.getValue()) {
                symtab.openScope();
                Symbol newEntry = new Symbol(child.getType(), child);
                newEntry.setAddress(store.setNext(child));
                symtab.insert(node.getVariable().getName(), newEntry);

                BoolLiteralNode predRes = (BoolLiteralNode)node.expression.accept(this);
                if (predRes.getValue()) newArray.getValue().add(child);
                symtab.closeScope();
            }
            return newArray;
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
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
        Symbol entry;
        ArrayAccessNode elementNode = node.getElement();
        try {
            entry = symtab.lookup(elementNode.getArray().getName());
            ValueArrayLiteralNode values = (ValueArrayLiteralNode) store.getElement(entry.getAddress());
            List<ArithmeticExpressionNode> indices = elementNode.getIndices();
            for (int i = 0, indicesSize = indices.size(); i < indicesSize - 1; i++) {
                ArithmeticExpressionNode child = indices.get(i);
                IntLiteralNode index = (IntLiteralNode)child.accept(this);
                values = (ValueArrayLiteralNode)values.getValue().get(index.getValue().intValue());
            }
            ArithmeticExpressionNode last = elementNode.getIndices().get(elementNode.getIndices().size() - 1);
            IntLiteralNode index = (IntLiteralNode)last.accept(this);
            values.getValue().set(index.getValue().intValue(), (LiteralNode) node.expression.accept(this));
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiteralNode visit(AssignmentNode node) {
        LiteralNode value = (LiteralNode)node.getExpression().accept(this);
        IdentifierNode variable = node.getVariable();

        Symbol entry;
        try {
            if (variable instanceof SimpleIdentifierNode) {
                entry = symtab.lookup(variable.getName());
                if (variable.getType() instanceof FloatType) {
                    store.setElement(entry.getAddress(), ToFloat(value.getValue()));
                }
                store.setElement(entry.getAddress(), value);
            } else if (variable instanceof RecordIdentifierNode) {
                //Find record in store
                entry = symtab.lookup(variable.getName());
                RecordLiteralNode element = (RecordLiteralNode)store.getElement(entry.getAddress());
                //Find the element denoted in the source code
                while (!(((RecordIdentifierNode) variable).getChild() instanceof SimpleIdentifierNode)) {
                    variable = ((RecordIdentifierNode)variable).getChild();
                    element = (RecordLiteralNode) element.getValue().get(variable.getName());
                }
                element.getValue().put(((RecordIdentifierNode) variable).getChild().getName(), value);
            }
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
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
            return new IntLiteralNode((long)a1.getValue() / (long)a2.getValue());
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
        ValueArrayLiteralNode array = (ValueArrayLiteralNode)node.getArray().accept(this);
        try {
            for (LiteralNode child : array.getValue()) {
                symtab.openScope();
                Symbol newEntry = new Symbol(child.getType(), child);
                newEntry.setAddress(store.setNext(child));
                symtab.insert(node.getVariable().getName(), newEntry);

                LiteralNode res = (LiteralNode)node.getStatements().accept(this);
                if (res != null) {
                    symtab.closeScope();
                    return res;
                }
                symtab.closeScope();
            }
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiteralNode visit(FunctionCallNode node) {
        LiteralNode[] argResults = new LiteralNode[node.getArguments().size()];
        List<Type> argumentTypes = new ArrayList<>();

        //Visit argument nodes
        for (int i = 0; i < node.getArguments().size(); i++) {
            argResults[i]=((LiteralNode)node.getArguments().get(i).accept(this));
            argumentTypes.add(node.getArguments().get(i).getType());
        }

        FunctionType func = new FunctionType(node.getName().getName(), argumentTypes, new VoidType());
        String signature = func.toString();
        if (standardFunctions.containsKey(signature)) {
            Function stdFunc = standardFunctions.get(func.toString());
            return ((LiteralNode) stdFunc.apply(argResults));
        }

        SymbolTable old = symtab;
        LiteralNode res = null;
        try {
            FunctionSymbol function = (FunctionSymbol)symtab.lookup(signature);
            symtab = new SymbolTable(function.getSymbolTable());
            symtab.openScope();
            FunctionNode declarationNode = (FunctionNode)function.getDeclarationNode();
            for (int i = 0; i < argResults.length; i++) {
                Symbol s = symtab.lookup(declarationNode.getFormalArguments().get(i).getName().getName());
                store.setElement(s.getAddress(), argResults[i]);
            }
            res =  (LiteralNode)declarationNode.getStatements().accept(this);
            symtab.closeScope();
            symtab = old;
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    public LiteralNode visit(FunctionNode node) {
        SymbolTable functionTable = new SymbolTable(symtab);
        functionTable.openScope();

        List<VariableDeclarationNode> arguments = node.getFormalArguments();
        List<Type> argumentTypes = new ArrayList<>();
        SymbolTable old = symtab;
        symtab = functionTable;
        for (int i = 0; i < arguments.size(); i++) {

            /*
            MATHIAS ARE YOU SURE ABOUT THE ORDER OF THE TWO LINES BELOW??????????
             */

            argumentTypes.add(arguments.get(i).getTypeNode().getType());
            arguments.get(i).accept(this);
        }
        symtab = old;

        String funcName = node.getName().getName();
        Type funcType = node.getType();

        FunctionType function = new FunctionType(funcName, argumentTypes, funcType);
        try {
            FunctionSymbol f = new FunctionSymbol(function, node, new SymbolTable(functionTable));
            f.getSymbolTable().insert(function.toString(), f);
            symtab.insert(function.toString(), f);
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
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
        LiteralNode res;
        symtab.openScope();
        if (predicate.getValue()) {
            res = (LiteralNode)node.getTrueBranch().accept(this);
        } else {
            res =  (LiteralNode)node.getFalseBranch().accept(this);
        }
        symtab.closeScope();
        return res;
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

        return new IntLiteralNode((long)a1.getValue() % (long)a2.getValue());
    }

    @Override
    public LiteralNode visit(MultiplicationNode node) {
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            return new IntLiteralNode(Math.multiplyExact((long)a1.getValue(), (long)a2.getValue()));
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
        LiteralNode a1 = (LiteralNode) node.getLeft().accept(this);
        LiteralNode a2 = (LiteralNode) node.getRight().accept(this);

        Type childType = node.getLeft().getType();
        if (childType instanceof FloatType) {
            return new BoolLiteralNode(!Objects.equals(ToFloat(a1.getValue()), ToFloat(a2.getValue())));
        } else if (childType instanceof CharType) {
            return new BoolLiteralNode(((CharLiteralNode) a1).getValue() != ((CharLiteralNode) a2).getValue());
        }
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
        try {
            Symbol identifier = symtab.lookup(node.getName());
            LiteralNode element = (LiteralNode) store.getElement(identifier.getAddress());
            IdentifierNode child = node.getChild();
            //Follow the chain of record identifiers
            while (child instanceof RecordIdentifierNode) {
                element = ((RecordLiteralNode)element).getValue().get(child.getName());
                child = ((RecordIdentifierNode) child).getChild();
            }
            element = ((RecordLiteralNode)element).getValue().get(child.getName());

            //Get the last name which is the simple identifier
            return element;
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiteralNode visit(ReturnNode node) {
        return (LiteralNode)node.getExpresssion().accept(this);
    }

    @Override
    public LiteralNode visit(ShellNode node) {
        try {
            StringLiteralNode command = (StringLiteralNode)node.getCommand().accept(this);
            BufferedReader b = runCommand(command.getValue());

            String line;

            while ((line = b.readLine()) != null) {
                System.out.println(line);
            }

            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            return new IntLiteralNode(Math.subtractExact((long)a1.getValue(), (long)a2.getValue()));
        } else if (node.getLeft().getType() instanceof CharType && node.getRight().getType() instanceof IntType) {
            int asciiValue = (char)a1.getValue();
            long value = (long)a2.getValue();
            return new CharLiteralNode((char)(asciiValue - value));
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
            if (node.getTypeNode().getType() instanceof RecordType) {
                store.setElement(entry.getAddress(), createRecord(((RecordType)node.getTypeNode().getType()).getName()));
            } else {
                store.setElement(entry.getAddress(), LiteralNode.fromType(node.getTypeNode().getType()));
            }
            symtab.insert(node.getName().getName(), entry);
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public LiteralNode visit(WhileNode node) {
        while ((boolean)((LiteralNode)node.getPredicate().accept(this)).getValue()) {
            symtab.openScope();
            LiteralNode res = (LiteralNode) node.getStatements().accept(this);
            if (res != null) {
                symtab.closeScope();
                return res;
            }
            symtab.closeScope();
        }
        return null;
    }

    @Override
    public LiteralNode visit(ProcedureCallNode node) {
        return (LiteralNode)node.ToFunction().accept(this);
    }

    @Override
    public LiteralNode visit(ChannelNode node) {
        if (node.getIdentifier().getName().equals("stdio")) {
            StringLiteralNode output = (StringLiteralNode) node.getExpression().accept(this);
            System.out.println(output.getValue());
            return null;
        }

        //If we are to write to a channel
        if (node.getIdentifier().getType() instanceof ChannelType) {
            StringLiteralNode output = (StringLiteralNode)node.getExpression().accept(this);
            try {
                Symbol symbol = symtab.lookup(node.getIdentifier().getName());
                ChannelLiteralNode c = (ChannelLiteralNode) store.getElement(symbol.getAddress());
                c.write(output.getValue());
            } catch (VariableNotDeclaredException e) {
                e.printStackTrace();
            }
        } else {
            //We now know that the right-hand side is a channel, and the left hand side is a variable with type string
            ChannelLiteralNode c = (ChannelLiteralNode) node.getExpression().accept(this);
            Symbol symbol = null;
            try {
                symbol = symtab.lookup(node.getIdentifier().getName());
                store.setElement(symbol.getAddress(), new StringLiteralNode(c.read()));
            } catch (VariableNotDeclaredException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public LiteralNode visit(PatternMatchNode node) {
        String text = ((StringLiteralNode)node.getLeft().accept(this)).getValue();
        String pattern = ((StringLiteralNode)node.getRight().accept(this)).getValue();

        return new BoolLiteralNode(text.matches(pattern));
    }

    private static Double ToFloat(Object o) {
        if (o instanceof Float || o instanceof Double) return (Double) o;
        if (o instanceof Integer || o instanceof Long) {
            return ((Long) o).doubleValue();
        }
        return null;
    }

    private BufferedReader runCommand(String command) throws IOException, InterruptedException {
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(command);
        p.waitFor();
        BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
        return b;
    }

    private RecordLiteralNode createRecord(String name) {
        RecordLiteralNode val = null;
        try {
            Symbol recSymbol = recTable.lookup(name);
            RecordType type = (RecordType)recSymbol.getType();
            Iterator it = type.getMembers().entrySet().iterator();
            val = new RecordLiteralNode(new HashMap<>(), type);
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if (pair.getValue() instanceof RecordType) {
                    val.getValue().put(pair.getKey().toString(), createRecord(((RecordType)pair.getValue()).getName()));
                } else{
                    val.getValue().put(pair.getKey().toString(), LiteralNode.fromType((Type) pair.getValue()));
                }
            }
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        return val;
    }
}
