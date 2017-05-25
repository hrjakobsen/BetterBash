package com.d401f17.Visitors.CodeGenerator;

import com.d401f17.AST.Nodes.*;
import com.d401f17.SymbolTable.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.BaseVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by mathias on 5/5/17.
 */

public class ByteCodeVisitor extends BaseVisitor<Void> {

    private static int id = 0;

    private List<ClassDescriptor> otherClasses = new ArrayList<>();
    private HashMap<String, String> standardFunctions= new HashMap<>();
    private ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
    MethodVisitor mv = null;
    private SymbolTable symtab = new SymbolTable();
    private int nextAddress = 0;
    public ByteCodeVisitor() throws IOException {
        standardFunctions.put("print", "(Ljava/lang/String;)V");
        standardFunctions.put("read", "()Ljava/lang/String;");
        standardFunctions.put("intToStr", "(J)Ljava/lang/String;");
        standardFunctions.put("floatToStr", "(D)Ljava/lang/String;");
        standardFunctions.put("charToStr", "(Ljava/lang/String;)Ljava/lang/String;");
        standardFunctions.put("boolToStr", "(I)Ljava/lang/String;");
        standardFunctions.put("empty","(Ljava/util/ArrayDeque;)I");
        standardFunctions.put("getFilesFromDir","(Ljava/lang/String;)Ljava/util/ArrayList;");
        standardFunctions.put("intVal", "(Ljava/lang/String;)J");
        standardFunctions.put("floatVal", "(Ljava/lang/String;)D");
        //Set up main class
        cw.visit(52,
                ACC_PUBLIC + ACC_STATIC,
                "Main",
                null,
                "java/lang/Object",
                null
        );

        //Set up main method
        mv = cw.visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null
        );

        mv.visitTypeInsn(NEW, "RecursiveSymbolTable");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "RecursiveSymbolTable", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 0);
    }

    public List<ClassDescriptor> getOtherClasses() {
        return otherClasses;
    }

    public ClassWriter getCw() {
        return cw;
    }

    @Override
    public Void visit(AdditionNode node) {
        if (node.getType() instanceof IntType) {
            node.getLeft().accept(this);
            node.getRight().accept(this);
            mv.visitInsn(LADD);
        } else if (node.getType() instanceof FloatType) {
            ensureFloat(node.getLeft());
            ensureFloat(node.getRight());
            mv.visitInsn(DADD);
        } else if (node.getLeft().getType() instanceof CharType || node.getRight().getType() instanceof CharType) {
            node.getLeft().accept(this);
            if (node.getLeft().getType() instanceof CharType) {
                charToInt();
            }
            node.getRight().accept(this);
            if (node.getRight().getType() instanceof CharType) {
                charToInt();
            }
            mv.visitInsn(LADD);
            intToChar();
        } else {
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            node.getLeft().accept(this);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            node.getRight().accept(this);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
        }
        return null;
    }

    @Override
    public Void visit(AndNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        mv.visitInsn(IMUL);
        return null;
    }

    @Override
    public Void visit(ArrayAppendNode node) {
        try {
            Symbol s = symtab.lookup(node.getVariable().getName());
            emitLoad(node.getVariable().getName(), s.getType()); //push array ref
            node.getExpression().accept(this); //push value to append
            boxElement(node.getExpression().getType());
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false); //Add to the list. Push null return value//Call appending method
            mv.visitInsn(POP); //Clean stack
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node) {
        //Get Array ref
        this.emitLoad(node.getArray().getName(), node.getArray().getType());//Push array ref

        //Get ref to innermost array
        ArrayType arrayType = (ArrayType) node.getArray().getType();
        for (int i = 0; i < node.getIndices().size(); i++) {
            node.getIndices().get(i).accept(this); //push index
            mv.visitInsn(L2I); //Truncate to 32 bit
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false); //pop ref; pop index; push value
            unboxElement(arrayType.getChildType());
            if (arrayType.getChildType() instanceof ArrayType) {
                arrayType = (ArrayType) arrayType.getChildType();
            }
        }
        return null;
    }

    @Override
    public Void visit(ArrayBuilderNode node) {
        Label evaluate = new Label();
        Label exit = new Label();

        node.getArray().accept(this);

        //Create new array
        mv.visitTypeInsn(NEW, "java/util/ArrayList");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 9);

        //Evaluate array
        mv.visitInsn(DUP);
        mv.visitVarInsn(ASTORE, 7);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I", false);
        mv.visitVarInsn(ISTORE, 5);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 6);

        //Test if still inside array bounds
        mv.visitLabel(evaluate);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitJumpInsn(IF_ICMPGE, exit);


        //Open a new scope in the symbol table
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "openScope", "()V", false);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(node.getVariable().getName());

        //Get the i'th element of the list
        mv.visitVarInsn(ALOAD, 7);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false);
        mv.visitVarInsn(ASTORE, 8);
        mv.visitVarInsn(ALOAD, 8);

        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "insert", "(Ljava/lang/String;Ljava/lang/Object;)V", false);

        symtab.openScope();
        try {
            symtab.insert(node.getVariable().getName(), new Symbol(((ArrayType)node.getArray().getType()).getChildType(), null));
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }

        node.getExpression().accept(this);

        symtab.closeScope();

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "closeScope", "()V", false);

        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 6);

        mv.visitJumpInsn(IFEQ, evaluate);
        mv.visitVarInsn(ALOAD, 9);
        mv.visitVarInsn(ALOAD, 8);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false);
        mv.visitInsn(POP);

        mv.visitJumpInsn(GOTO, evaluate);
        mv.visitLabel(exit);

        mv.visitVarInsn(ALOAD, 9);

        return null;
    }

    @Override
    public Void visit(ArrayLiteralNode node) {
        //Make a new list object
        mv.visitTypeInsn(NEW, "java/util/ArrayList"); //Push list ref
        mv.visitInsn(DUP); //push list ref
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false); //pop list ref, push NULL

        //Add elements to the list
        for (ArithmeticExpressionNode n : node.getValue()) {
            mv.visitInsn(DUP); //Push list ref
            n.accept(this); //Push value to add to the list
            boxElement(n.getType());
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false); //Add to the list. Push null return value
            mv.visitInsn(POP); //Pop and discard return value
        }
        return null;
    }

    @Override
    public Void visit(ArrayElementAssignmentNode node) {
        this.emitNops(4);
        
        //Get list ref
        this.emitLoad(node.getElement().getArray().getName(), node.getElement().getArray().getType()); //Push array ref

        //Traverse list refs to get ref to innermost array
        for (int i = 0; i < node.getElement().getIndices().size()-1; i++) {
            node.getElement().getIndices().get(i).accept(this);//push index
            mv.visitInsn(L2I);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false); //pop list ref; pop index; push list ref
        }

        //Invoke setter
        node.getElement().getIndices().get(node.getElement().getIndices().size()-1).accept(this); //push index
        mv.visitInsn(L2I);
        node.getExpression().accept(this); //push value
        boxElement(node.getExpression().getType());
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;", false); //pop index; pop value; push null
        mv.visitInsn(POP); //pop null

        this.emitNops(4);

        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        ArithmeticExpressionNode child = node.getExpression();
        Symbol s = null;
        try {
            s = symtab.lookup(node.variable.getName());
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }

        if (node.getVariable() instanceof RecordIdentifierNode) {
            IdentifierNode traveller = ((RecordIdentifierNode) node.getVariable()).getChild();
            IdentifierNode previous = node.getVariable();
            previous.setType(s.getType());
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(node.getVariable().getName());
            mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "lookup", "(Ljava/lang/String;)Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, toJavaType(s.getType()));
            //Find the lowest element
            //The second to last element must be the one we are interested in
            while (traveller != null) {
                if (traveller instanceof SimpleIdentifierNode) {
                    //Base case if last element
                    child.accept(this);
                    mv.visitFieldInsn(PUTFIELD, ((RecordType)previous.getType()).getName(), traveller.getName(), toJavaType(node.getExpression().getType()));
                    traveller = null;
                } else {
                    //If another record
                    mv.visitFieldInsn(GETFIELD, ((RecordType)previous.getType()).getName(), traveller.getName(), toJavaType(traveller.getType()));
                    previous = traveller;
                    traveller = ((RecordIdentifierNode)traveller).getChild();
                }
            }
        } else {
            child.accept(this);
            if (child.getType() instanceof IntType && isFloatExactly(node.getVariable().getType()));
            mv.visitInsn(L2D);
            emitStore(node.getVariable().getName(), s.getType(), 0);
        }

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
        mv.visitLdcInsn(node.getValue());
        return null;
    }

    @Override
    public Void visit(BoolLiteralNode node) {
        if (node.getValue()) {
            mv.visitLdcInsn(1);
        } else {
            mv.visitLdcInsn(0);
        }
        return null;
    }

    @Override
    public Void visit(FloatLiteralNode node) {
        mv.visitLdcInsn(node.getValue());
        return null;
    }

    @Override
    public Void visit(StringLiteralNode node) {
        mv.visitLdcInsn(node.getValue());
        return null;
    }

    @Override
    public Void visit(CharLiteralNode node) {
        mv.visitLdcInsn(Character.toString(node.getValue()));
        return null;
    }

    @Override
    public Void visit(RecordLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(DivisionNode node) {
        if (node.getType() instanceof IntType) {
            node.getLeft().accept(this);
            node.getRight().accept(this);
            mv.visitInsn(LDIV);
        } else {
            ensureFloat(node.getLeft());
            ensureFloat(node.getRight());
            mv.visitInsn(DDIV);
        }
        return null;
    }

    @Override
    public Void visit(EqualNode node) {
        if (node.getLeft().getType() instanceof FloatType) {
            compareNumerals(IF_ICMPEQ, node);
        } else if (node.getLeft().getType() instanceof StringType){
            node.getLeft().accept(this);
            node.getRight().accept(this);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
        } else if (node.getLeft().getType() instanceof BoolType) {
            Label t = new Label();
            Label exit = new Label();
            node.getLeft().accept(this);
            node.getRight().accept(this);
            mv.visitInsn(ISUB);
            mv.visitJumpInsn(IFEQ, t);
            mv.visitInsn(ICONST_0);
            mv.visitJumpInsn(GOTO, exit);
            mv.visitLabel(t);
            mv.visitInsn(ICONST_1);
            mv.visitLabel(exit);
        }
        return null;
    }

    @Override
    public Void visit(ForkNode node) {
        ClassWriter innerClass = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
        ClassWriter mainClass = cw;
        cw = innerClass;
        MethodVisitor mainMethod = mv;

        String className = "fork" + id++;

        innerClass.visit(52, ACC_PUBLIC, className, null, "java/lang/Thread", null);

        innerClass.visitField(ACC_PRIVATE, "table", "LRecursiveSymbolTable;", null, null);

        //Create constructor that accepts a SymbolTable
        MethodVisitor constructor = innerClass.visitMethod(ACC_PUBLIC, "<init>", "(LRecursiveSymbolTable;)V", null, null);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Thread", "<init>", "()V", false);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitVarInsn(ALOAD, 1);
        constructor.visitFieldInsn(PUTFIELD, className, "table", "LRecursiveSymbolTable;");
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(0,0);
        constructor.visitEnd();

        //implement the interface
        mv = innerClass.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, "table", "LRecursiveSymbolTable;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "clone", "()LRecursiveSymbolTable;", false);
        mv.visitVarInsn(ASTORE, 0);

        node.getChild().accept(this);

        //Return back to method that used "fork"
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        innerClass.visitEnd();

        otherClasses.add(new ClassDescriptor(className, innerClass));

        cw = mainClass;
        mv = mainMethod;

        //Create new instance of class and call fork
        mv.visitTypeInsn(NEW, className);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "(LRecursiveSymbolTable;)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "start", "()V", false);

        return null;
    }

    @Override
    public Void visit(ForNode node) {
        //Push array ref to top of stack
        node.getArray().accept(this);

        mv.visitVarInsn(ASTORE, 3);

        Label evaluate = new Label();
        Label execute = new Label();

        //Array Index
        mv.visitLdcInsn(0);
        mv.visitVarInsn(ISTORE, 4);

        //Begin while loop
        mv.visitJumpInsn(GOTO, evaluate);
        mv.visitLabel(execute);



        //Get symbol table of function
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "openScope", "()V", false);

        //Bind name to array value
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(node.getVariable().getName());

        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false);

        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "insert", "(Ljava/lang/String;Ljava/lang/Object;)V", false);

        //Save variables on stack
        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ILOAD, 4);

        symtab.openScope();

        try {
            symtab.insert(node.getVariable().getName(), new Symbol(((ArrayType)node.getArray().getType()).getChildType(), null));
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }

        node.getStatements().accept(this);
        symtab.closeScope();

        //Restore variables from stack
        mv.visitVarInsn(ISTORE, 4);
        mv.visitVarInsn(ASTORE, 3);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "closeScope", "()V", false);

        //Increment index
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 4);

        mv.visitLabel(evaluate);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I", false);
        mv.visitJumpInsn(IF_ICMPLT, execute);
        return null;
    }

    @Override
    public Void visit(FunctionCallNode node) {

        String funcName = node.getName().getName();

        if (standardFunctions.containsKey(funcName)) {
            List<Type> types = getFloatAndIntTypes(standardFunctions.get(funcName));
            List<ArithmeticExpressionNode> arguments = node.getArguments();
            for (int i = 0; i < arguments.size(); i++) {
                ArithmeticExpressionNode arg = arguments.get(i);
                if (isFloatExactly(types.get(i))) {
                    ensureFloat(arg);
                } else {
                    arg.accept(this);
                }
            }
            mv.visitMethodInsn(INVOKESTATIC, "StdFunc", funcName, standardFunctions.get(funcName), false);
            return null;
        }

        FunctionNode f = null;
        String methodname = "";

        try {
            FunctionSymbol fs = (FunctionSymbol) symtab.lookup(funcName);
            f = (FunctionNode) fs.getDeclarationNode();
            methodname = fs.getMethodName();
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(funcName);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "lookup", "(Ljava/lang/String;)Ljava/lang/Object;", false);
        mv.visitTypeInsn(CHECKCAST, "RecursiveSymbolTable");
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "clone", "()LRecursiveSymbolTable;", false);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "openScope", "()V", false);
        mv.visitVarInsn(ASTORE, 1);



        //We want to visit the node using this functions symbol table, but bind them in the new functions symbol table
        List<ArithmeticExpressionNode> arguments = node.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            ArithmeticExpressionNode argument = arguments.get(i);
            if (isFloatExactly(f.getFormalArguments().get(i).getTypeNode().getType())) {
                ensureFloat(argument);
                argument.setType(new FloatType());
            } else {
                argument.accept(this);
                cloneIfReference(argument.getType());
            }
            emitStore(f.getFormalArguments().get(i).getName().getName(), argument.getType(), 1);
        }

        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "Main", methodname, "(LRecursiveSymbolTable;)" + toJavaType(node.getType()), false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "closeScope", "()V", false);

        return null;
    }

    private void cloneIfReference(Type type) {
        if (type instanceof ArrayType) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "clone", "()Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, "java/util/ArrayList");
        } else if (type instanceof RecordType) {
            RecordType t = (RecordType)type;
            mv.visitMethodInsn(INVOKEVIRTUAL, t.getName(), "clone", "()L" + t.getName() + ";", false);
        }
    }

    @Override
    public Void visit(FunctionNode node) {
        String methodname = node.getName().getName() + "$" + id++;
        String funcName = node.getName().getName();

        //Get the current symbol table
        mv.visitVarInsn(ALOAD, 0);

        //Create the new symbol table for the function
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "clone", "()LRecursiveSymbolTable;", false);

        //Order the stack: {'REC', 'func name', 'REC', 'REC', }
        mv.visitInsn(DUP);
        mv.visitInsn(DUP);
        mv.visitLdcInsn(funcName);
        mv.visitInsn(SWAP);

        //Insert the new table into itself
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "insert", "(Ljava/lang/String;Ljava/lang/Object;)V", false);
        //(Stack is now: {'REC'})

        //Load the original table
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(SWAP);
        mv.visitLdcInsn(funcName);
        mv.visitInsn(SWAP);

        //Stack is now: {'REC', "func name', 'OLD'}

        //Insert the new table into that as well
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "insert", "(Ljava/lang/String;Ljava/lang/Object;)V", false);

        //Create the snapshot of the symbol table at declaration time
        SymbolTable functionTable = new SymbolTable(symtab);
        functionTable.openScope();

        //Use the snapshot in the function to determine bindings
        SymbolTable old = symtab;
        symtab = functionTable;

        for (VariableDeclarationNode decl : node.getFormalArguments()) {
            try {
                symtab.insert(decl.getName().getName(), new Symbol(decl.getTypeNode().getType(), decl));
            } catch (VariableAlreadyDeclaredException e) {
                e.printStackTrace();
            }
        }

        Type funcType = node.getType();

        FunctionType function = new FunctionType(funcName, null, funcType);

        try {
            //Insert the function into the old symbol table
            FunctionSymbol f = new FunctionSymbol(function, node, new SymbolTable(functionTable));
            f.setMethodName(methodname);
            f.getSymbolTable().insert(funcName, f);
            old.insert(funcName, f);
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }

        //Create the new static method
        MethodVisitor oldMv = mv;
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, methodname, "(LRecursiveSymbolTable;)" + toJavaType(node.getStatements().getType()), null, null);

        //Generate the method itself
        node.getStatements().accept(this);

        //if void it may not have an explicit return statement. Now add it
        if (node.getStatements().getType() instanceof OkType || node.getStatements().getType() instanceof VoidType) {
            mv.visitInsn(RETURN);
        }

        mv.visitMaxs(0,0);

        mv.visitEnd();

        //Restore the symbol table and method to main
        symtab = old;
        mv = oldMv;

        return null;
    }


    @Override
    public Void visit(GreaterThanNode node) {
        compareNumerals(IF_ICMPGT, node);
        return null;
    }

    private void compareNumerals(int cmp, InfixExpressionNode node) {
        Label True = new Label();
        Label False = new Label();
        Label done = new Label();
        if (node.getLeft().getType() instanceof IntType && node.getRight().getType() instanceof IntType) {
            node.getLeft().accept(this);
            node.getRight().accept(this);
            mv.visitInsn(LCMP);
        } else {
            ensureFloat(node.getLeft());
            ensureFloat(node.getRight());
            mv.visitInsn(DCMPG);
        }
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(cmp, True);
        mv.visitLabel(False);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, done);
        mv.visitLabel(True);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(done);

    }

    @Override
    public Void visit(GreaterThanOrEqualNode node) {
        compareNumerals(IF_ICMPGE, node);
        return null;
    }

    @Override
    public Void visit(IfNode node) {
        node.getPredicate().accept(this);
        Label falseBranch = new Label();
        Label exit = new Label();
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(IF_ICMPNE, falseBranch);
        symtab.openScope();
        node.getTrueBranch().accept(this);
        symtab.closeScope();
        mv.visitJumpInsn(GOTO, exit);
        mv.visitLabel(falseBranch);
        symtab.openScope();
        node.getFalseBranch().accept(this);
        symtab.closeScope();
        mv.visitLabel(exit);
        return null;
    }

    @Override
    public Void visit(LessThanNode node) {
        compareNumerals(IF_ICMPLT, node);
        return null;
    }

    @Override
    public Void visit(LessThanOrEqualNode node) {
        compareNumerals(IF_ICMPLE, node);
        return null;
    }

    @Override
    public Void visit(ModuloNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        mv.visitInsn(LREM);
        return null;
    }

    @Override
    public Void visit(MultiplicationNode node) {
        if (node.getType() instanceof IntType) {
            node.getLeft().accept(this);
            node.getRight().accept(this);
            mv.visitInsn(LMUL);
        } else {
            ensureFloat(node.getLeft());
            ensureFloat(node.getRight());
            mv.visitInsn(DMUL);
        }
        return null;
    }

    @Override
    public Void visit(NegationNode node) {
        node.getExpression().accept(this);
        Label f = new Label();
        Label exit = new Label();
        mv.visitJumpInsn(IFEQ, f);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, exit);
        mv.visitLabel(f);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(exit);
        return null;
    }

    @Override
    public Void visit(NotEqualNode node) {
        Label t = new Label();
        Label exit = new Label();
        if (node.getLeft().getType() instanceof FloatType) {
            compareNumerals(IF_ICMPNE, node);
            return null;
        } else if (node.getLeft().getType() instanceof StringType){
            node.getLeft().accept(this);
            node.getRight().accept(this);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(IFEQ, t);
            mv.visitInsn(ICONST_0);
            mv.visitJumpInsn(GOTO, exit);
            mv.visitLabel(t);
            mv.visitInsn(ICONST_1);
            mv.visitLabel(exit);
        } else if (node.getLeft().getType() instanceof BoolType) {
            node.getLeft().accept(this);
            node.getRight().accept(this);
            mv.visitInsn(ISUB);
            mv.visitJumpInsn(IFEQ, t);
            mv.visitInsn(ICONST_1);
            mv.visitJumpInsn(GOTO, exit);
            mv.visitLabel(t);
            mv.visitInsn(ICONST_0);
            mv.visitLabel(exit);
        }
        return null;
    }

    @Override
    public Void visit(OrNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        mv.visitInsn(IADD);
        mv.visitInsn(ICONST_0);
        Label l = new Label();
        Label l2 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l);
        mv.visitInsn(POP);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);

        return null;
    }

    @Override
    public Void visit(RecordDeclarationNode node) {
        String name = node.getName();
        List<VariableDeclarationNode> variables = node.getVariables();

        ClassWriter record = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);

        //Create class
        record.visit(52, ACC_PUBLIC, name, null, "java/lang/Object", null);
        //Create fields
        StringBuilder signature = new StringBuilder();
        for (VariableDeclarationNode variable : variables) {
            String javaType = toJavaType(variable.getTypeNode().getType());
            FieldVisitor fv = record.visitField(ACC_PUBLIC, variable.getName().getName(), javaType, null, null);
            fv.visitEnd();
        }

        //Create default constructor
        MethodVisitor constructor = record.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

        //In our constructor, we want to initialise our fields
        for (VariableDeclarationNode variable : variables) {
            constructor.visitVarInsn(ALOAD, 0);
            MethodVisitor old = mv;
            mv = constructor;
            getDefaultValue(variable.getTypeNode().getType());
            unboxElement(variable.getTypeNode().getType());
            mv = old;

            constructor.visitFieldInsn(PUTFIELD, name, variable.getName().getName(), toJavaType(variable.getTypeNode().getType()));
        }

        constructor.visitInsn(RETURN);

        constructor.visitMaxs(0, 0);
        constructor.visitEnd();

        MethodVisitor clone = record.visitMethod(ACC_PUBLIC, "clone","()L" + name + ";" , null, null);

        clone.visitTypeInsn(NEW, name);
        clone.visitInsn(DUP);
        clone.visitMethodInsn(INVOKESPECIAL, name, "<init>", "()V", false);

        for (VariableDeclarationNode variable : variables) {
            clone.visitInsn(DUP);
            clone.visitVarInsn(ALOAD, 0);
            clone.visitFieldInsn(GETFIELD, name, variable.getName().getName(), toJavaType(variable.getTypeNode().getType()));
            clone.visitFieldInsn(PUTFIELD, name, variable.getName().getName(), toJavaType(variable.getTypeNode().getType()));
        }

        clone.visitInsn(ARETURN);

        clone.visitMaxs(0, 0);
        clone.visitEnd();

        record.visitEnd();

        otherClasses.add(new ClassDescriptor(name, record));

        return null;
    }

    @Override
    public Void visit(RecordIdentifierNode node) {
        String varName = node.getName();
        RecordType recType = null;
        try {
            Symbol s = symtab.lookup(varName);
            recType = (RecordType) s.getType();
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        Type terminalType = node.getType();
        node.setType(recType);
        //Get the symbol table
        mv.visitVarInsn(ALOAD, 0);

        //Lookup the name
        mv.visitLdcInsn(varName);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "lookup", "(Ljava/lang/String;)Ljava/lang/Object;", false);
        mv.visitTypeInsn(CHECKCAST, recType.getName());

        IdentifierNode traveller = node.getChild();
        IdentifierNode previous = node;
        while (traveller != null) {
            if (traveller instanceof SimpleIdentifierNode) {
                //Base case if last element
                mv.visitFieldInsn(GETFIELD, ((RecordType)previous.getType()).getName(), traveller.getName(), toJavaType(terminalType));
                traveller = null;
            } else {
                //If another record
                mv.visitFieldInsn(GETFIELD, ((RecordType)previous.getType()).getName(), traveller.getName(), toJavaType(traveller.getType()));
                previous = traveller;
                traveller = ((RecordIdentifierNode)traveller).getChild();
            }
        }
        return null;
    }

    @Override
    public Void visit(ReturnNode node) {
        if (node.getExpresssion() != null) {
            node.getExpresssion().accept(this);
        }

        if (node.getType() instanceof VoidType) {
            mv.visitInsn(RETURN);
        } else if (node.getType() instanceof IntType) {
            mv.visitInsn(LRETURN);
        } else if (node.getType() instanceof FloatType) {
            mv.visitInsn(DRETURN);
        } else {
            mv.visitInsn(ARETURN);
        }

        return null;
    }

    @Override
    public Void visit(ShellNode node) {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false);
        node.getCommand().accept(this);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Runtime", "exec", "(Ljava/lang/String;)Ljava/lang/Process;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Process", "waitFor", "()I", false);
        mv.visitInsn(POP);
        return null;
    }

    @Override
    public Void visit(ShellToChannelNode node) {
        Label read = new Label();
        Label exit = new Label();

        //Get the channel to write to
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(node.getChannel().getName());
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "lookup", "(Ljava/lang/String;)Ljava/lang/Object;", false);
        mv.visitTypeInsn(CHECKCAST, "java/util/ArrayDeque");
        mv.visitVarInsn(ASTORE, 10);

        //Execute the process
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false);
        node.getCommand().getCommand().accept(this);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Runtime", "exec", "(Ljava/lang/String;)Ljava/lang/Process;", false);
        mv.visitVarInsn(ASTORE, 11);

        //Create a bufferedreader to read from the output of the command
        mv.visitTypeInsn(NEW, "java/io/InputStreamReader");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Process", "getInputStream", "()Ljava/io/InputStream;", false);
        mv.visitMethodInsn(INVOKESPECIAL, "java/io/InputStreamReader", "<init>", "(Ljava/io/InputStream;)V", false);
        mv.visitVarInsn(ASTORE, 9);

        mv.visitTypeInsn(NEW, "java/io/BufferedReader");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 9);
        mv.visitMethodInsn(INVOKESPECIAL, "java/io/BufferedReader","<init>", "(Ljava/io/Reader;)V", false);

        //Read all lines of output
        mv.visitLabel(read);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedReader", "readLine", "()Ljava/lang/String;", false);
        mv.visitInsn(DUP);
        mv.visitJumpInsn(IFNULL, exit);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayDeque", "add", "(Ljava/lang/Object;)Z", false);
        mv.visitInsn(POP);
        mv.visitJumpInsn(GOTO, read);

        mv.visitVarInsn(ALOAD, 11);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Process", "waitFor", "()I", false);
        mv.visitInsn(POP);

        //Clear stack
        mv.visitLabel(exit);
        mv.visitInsn(POP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/BufferedReader", "close","()V", false);

        return null;

    }

    @Override
    public Void visit(SimpleIdentifierNode node) {
        emitLoad(node.getName(), node.getType());

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
        if (node.getType() instanceof IntType) {
            node.getLeft().accept(this);
            node.getRight().accept(this);
            mv.visitInsn(LSUB);
        } else if (node.getLeft().getType() instanceof CharType || node.getRight().getType() instanceof CharType) {
            node.getLeft().accept(this);
            if (node.getLeft().getType() instanceof CharType) {
                charToInt();
            }
            node.getRight().accept(this);
            if (node.getRight().getType() instanceof CharType) {
                charToInt();
            }
            mv.visitInsn(LSUB);
            intToChar();
        } else {
            ensureFloat(node.getLeft());
            ensureFloat(node.getRight());
            mv.visitInsn(DSUB);
        }
        return null;
    }

    @Override
    public Void visit(TypeNode node) {
        return null;
    }

    @Override
    public Void visit(VariableDeclarationNode node) {
        Symbol s = new Symbol(node.getTypeNode().getType(), null);

        getDefaultValue(node.getTypeNode().getType());

        //Insert into symbolTable
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(SWAP);
        mv.visitLdcInsn(node.getName().getName());
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "insert", "(Ljava/lang/String;Ljava/lang/Object;)V", false);
        try {
            symtab.insert(node.getName().getName(), s);
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getDefaultValue(Type type) {
        if (type instanceof IntType) {
            mv.visitTypeInsn(NEW, "java/lang/Long");
            mv.visitInsn(DUP);
            mv.visitLdcInsn(0L);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Long", "<init>", "(J)V", false);
        } else if (type instanceof FloatType) {
            mv.visitTypeInsn(NEW, "java/lang/Double");
            mv.visitInsn(DUP);
            mv.visitLdcInsn(0D);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Double", "<init>", "(D)V", false);
        } else if (type instanceof BoolType) {
            mv.visitTypeInsn(NEW, "java/lang/Integer");
            mv.visitInsn(DUP);
            mv.visitLdcInsn(0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Integer", "<init>", "(I)V", false);
        } else if (type instanceof StringType || type instanceof CharType) {
            mv.visitTypeInsn(NEW, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "()V", false);
        } else if (type instanceof RecordType) {
            RecordType t = (RecordType)type;
            String name = t.getName();
            mv.visitTypeInsn(NEW, name);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, name, "<init>", "()V", false);
        } else if (type instanceof ArrayType) {
            mv.visitTypeInsn(NEW, "java/util/ArrayList");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        } else if (type instanceof ChannelType){
            mv.visitTypeInsn(NEW, "java/util/ArrayDeque");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayDeque", "<init>", "()V", false);
        }
    }

    @Override
    public Void visit(WhileNode node) {
        Label head = new Label();
        Label body = new Label();
        mv.visitJumpInsn(GOTO, head);
        mv.visitLabel(body);
        symtab.openScope();
        node.getStatements().accept(this);
        symtab.closeScope();
         mv.visitLabel(head);
        node.getPredicate().accept(this);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(IF_ICMPEQ, body);
        return null;
    }

    @Override
    public Void visit(ProcedureCallNode node) {
        FunctionCallNode n = node.ToFunction();
        n.accept(this);
        Type returnType = node.getReturnType();
        if (!(returnType instanceof VoidType || returnType instanceof OkType)) {
            //We need to remove the return element. It can be either one or two bytes
            if (returnType instanceof IntType || returnType instanceof FloatType) {
                mv.visitInsn(POP2);
            } else {
                mv.visitInsn(POP);
            }
        }
        return null;
    }

    @Override
    public Void visit(ChannelNode node) {

        if (node.getIdentifier().getType() instanceof ChannelType) {
            //Write to channel
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(node.getIdentifier().getName());
            mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "lookup", "(Ljava/lang/String;)Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, "java/util/ArrayDeque");
            node.getExpression().accept(this);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayDeque", "add", "(Ljava/lang/Object;)Z", false);
            mv.visitInsn(POP);
        } else {
            //Read from channel
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(node.getIdentifier().getName());
            node.getExpression().accept(this);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayDeque", "poll", "()Ljava/lang/Object;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "change", "(Ljava/lang/String;Ljava/lang/Object;)V", false);
        }

        return null;
    }

    @Override
    public Void visit(PatternMatchNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Label exit = new Label();
        Label falseBranch = new Label();

        mv.visitMethodInsn(INVOKEVIRTUAL,"java/lang/String", "matches", "(Ljava/lang/String;)Z", false);

        //Convert the boolean (Z) value to a 1/0 integer.
        mv.visitJumpInsn(IFEQ, falseBranch);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(GOTO, exit);
        mv.visitLabel(falseBranch);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(exit);

        return null;
    }

    private void emitStore(String name, Type type, int address) {
        boxElement(type);
        //set the element in the heap
        mv.visitVarInsn(ALOAD, address);
        mv.visitInsn(SWAP);
        mv.visitLdcInsn(name);
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "change", "(Ljava/lang/String;Ljava/lang/Object;)V", false);
    }

    private void boxElement(Type type) {
        //Boxing of primitive types
        if (type instanceof IntType) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (type instanceof FloatType) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        } else if (type instanceof StringType) {
        } else if (type instanceof CharType) {
        } else if (type instanceof BoolType) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        }
    }

    private void unboxElement(Type type) {
        if (type instanceof IntType) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
        } else if (type instanceof FloatType) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
        } else if (type instanceof StringType) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        } else if (type instanceof CharType) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        } else if (type instanceof BoolType) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
        } else if (type instanceof ArrayType) {
            mv.visitTypeInsn(CHECKCAST, "java/util/ArrayList");
        } else if (type instanceof ChannelType) {
            mv.visitTypeInsn(CHECKCAST, "java/util/ArrayDeque");
        } else if (type instanceof RecordType) {
            mv.visitTypeInsn(CHECKCAST, ((RecordType)type).getName());
        }
    }

    private void emitLoad(String name, Type type) {
        //Load the element from the heap
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "lookup", "(Ljava/lang/String;)Ljava/lang/Object;", false);

        //Unboxing of primitive types
        unboxElement(type);
    }

    private String toJavaType(Type variable) {
        if (variable instanceof IntType) {
            return "J";
        } else if (variable instanceof FloatType) {
            return "D";
        } else if (variable instanceof StringType || variable instanceof CharType) {
            return "Ljava/lang/String;";
        } else if (variable instanceof ArrayType) {
            return "Ljava/util/ArrayList;";
        } else if (variable instanceof VoidType || variable instanceof OkType) {
            return "V";
        } else if (variable instanceof BoolType) {
            return "I";
        } else if (variable instanceof RecordType) {
            return "L" + ((RecordType)variable).getName() + ";";
        } else {
            System.out.println(variable);
            System.out.println("Invalid type conversion");
        }
        return null;
    }

    private int LoadIns(Type variable) {
        if (variable instanceof IntType) {
            return LLOAD;
        } else if (variable instanceof FloatType) {
            return DLOAD;
        } else if (variable instanceof StringType || variable instanceof CharType) {
            return ALOAD;
        } else if (variable instanceof ArrayType) {
            return ALOAD;
        } else {
            System.out.println(variable);
            System.out.println("Invalid type conversion");
        }
        return 0;
    }

    private void print() {
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    private void print(String s) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(s);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public void End() {
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();
    }

    private void emitNops(int n) {
        for (int i = 0; i<n; i++) {
            mv.visitInsn(NOP);
        }
    }

    private void ensureFloat(ArithmeticExpressionNode node) {
        node.accept(this);
        if (node.getType() instanceof IntType) {
            mv.visitInsn(L2D);
        }
    }

    private List<Type> getFloatAndIntTypes(String signature) {
        ArrayList<Type> types = new ArrayList<>();
        boolean skip = false;
        for (int i = 0, size = signature.length(); i < size; i++) {
            char c = signature.charAt(i);
            switch (c) {
                case 'J':
                    if (!skip)
                        types.add(new IntType());
                    break;
                case 'D':
                    if (!skip)
                        types.add(new FloatType());
                    break;
                case 'L':
                    skip = true;
                    break;
                case ';':
                    types.add(new OkType());
                    skip = false;
                    break;
                case '(':
                case ')':
                    break;
                default:
                    if (!skip)
                        types.add(new OkType());
                    break;
            }
        }
        return types;
    }

    private boolean isFloatExactly(Type t) {
        return t.getClass() == FloatType.class;
    }

    private void charToInt() {
        mv.visitInsn(ICONST_0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String","charAt", "(I)C", false);
        mv.visitInsn(I2L);
    }

    private void intToChar() {
        mv.visitInsn(L2I);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "toString", "(C)Ljava/lang/String;", false);
    }
}
