package com.d401f17.Visitors.CodeGenerator;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.BaseVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by mathias on 5/5/17.
 */
@SuppressWarnings("Duplicates")
public class ByteCodeVisitor extends BaseVisitor<Void> {
    private ClassWriter cw = new ClassWriter(0);

    MethodVisitor mv = null;
    private SymbolTable symtab = new SymbolTable();
    private int nextAddress = 0;
    public ByteCodeVisitor() throws IOException {
        //Set up main class
        cw.visit(52,
                ACC_PUBLIC + ACC_STATIC,
                "Main",
                null,
                "java/lang/Object",
                null);
        //Set up main method
        mv = cw.visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null
        );
    }

    @Override
    public Void visit(AdditionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            mv.visitInsn(LADD);
        } else if (node.getType() instanceof FloatType) {
            mv.visitInsn(DADD);
        } else {
            //String operations
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
        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node) {
        try {
            Symbol s = symtab.lookup(node.getArray().getName());
            this.emitLoad(s.getType(), s.getAddress()); //Push arrayref
            for (ArithmeticExpressionNode n : node.getIndices()) {
                n.accept(this); //Push index
                mv.visitInsn(AALOAD); //arrayref, index -> value; load value from array
            }
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Void visit(ArrayBuilderNode node) {
        //this.emitNop();
        
        return null;
    }

    @Override
    public Void visit(ArrayLiteralNode node) {


        return null;
    }

    @Override
    public Void visit(ArrayElementAssignmentNode node) {
        try {
            int elements = node.getElement().getIndices().size()-1; //Number of elements in the array index
            Symbol s = symtab.lookup(node.getElement().getArray().getName());
            this.emitLoad(s.getType(), s.getAddress());    //push arrayref

            //Iterate through all reference but the last one
            //This will leave a ref to the inner array on the stack
            for (int i = 0; i < elements; i++) {
                node.getElement().getIndices().get(i).accept(this); //Push index
                mv.visitInsn(AALOAD); //arrayref, index -> value; load value from array
            }

            node.getElement().getIndices().get(elements).accept(this); //Push index
            node.getExpression().accept(this); //Push value to assign
            mv.visitInsn(AASTORE);//arrayref, index, value -> ; store value in array
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        ArithmeticExpressionNode child = node.getExpression();
        child.accept(this);
        try {
            Symbol s = symtab.lookup(node.variable.getName());
            emitStore(s.getType(), s.getAddress());
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
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
        mv.visitLdcInsn(node.getValue());
        return null;
    }

    @Override
    public Void visit(RecordLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(DivisionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            mv.visitInsn(LDIV);
        } else {
            mv.visitInsn(DDIV);
        }
        return null;
    }

    @Override
    public Void visit(EqualNode node) {
        if (node.getLeft().getType() instanceof FloatType) {
            compareNumerals(IF_ICMPEQ, node);
        } else {
            //TODO: COMPARE STRINGS OR CHARS
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
    public Void visit(FunctionNode node) {
        return null;
    }

    @Override
    public Void visit(GreaterThanNode node) {
        compareNumerals(IF_ICMPGT, node);
        return null;
    }

    private void compareNumerals(int cmp, InfixExpressionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        Label True = new Label();
        Label False = new Label();
        Label done = new Label();
        if (node.getLeft().getType() instanceof IntType && node.getRight().getType() instanceof IntType) {
            mv.visitInsn(LCMP);
        } else {
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
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            mv.visitInsn(LMUL);
        } else {
            mv.visitInsn(DMUL);
        }
        return null;
    }

    @Override
    public Void visit(NegationNode node) {
        mv.visitInsn(ICONST_M1);
        mv.visitInsn(IMUL);
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
        return null;
    }

    @Override
    public Void visit(RecordIdentifierNode node) {
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
        return null;
    }

    @Override
    public Void visit(ShellToChannelNode node) {
        return null;
    }

    @Override
    public Void visit(SimpleIdentifierNode node) {
        try {
            Symbol s = symtab.lookup(node.getName());
            emitLoad(s.getType(), s.getAddress());
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
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
            mv.visitInsn(LSUB);
        } else {
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
        s.setAddress(getWideVariable());
        try {
            symtab.insert(node.getName().getName(), s);
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
        return null;
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

    private int getWideVariable() {
        int address = nextAddress;
        nextAddress += 2;
        return address;
    }

    private int getVariable() {
        int address = nextAddress;
        nextAddress++;
        return address;
    }

    public byte[] getBytes() {
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }

    private int emitStore(Type type) {
        return emitStore(type, getWideVariable());
    }

    private int emitStore(Type type, int address) {
        if (type instanceof IntType) {
            mv.visitVarInsn(LSTORE, address);
        } else if (type instanceof FloatType) {
            mv.visitVarInsn(DSTORE, address);
        } else if (type instanceof StringType) {
            mv.visitVarInsn(ASTORE, address);
        } else if (type instanceof CharType) {
            mv.visitVarInsn(ASTORE, address);
        } else if (type instanceof BoolType) {
            mv.visitVarInsn(ISTORE, address);
        } else if (type instanceof ArrayType) {
            mv.visitVarInsn(ASTORE, address);
        }
        else {
            System.out.println("something was not right");
        }
        return address;
    }

    private int emitLoad(Type type, int address) {
        if (type instanceof IntType) {
            mv.visitVarInsn(LLOAD, address);
        } else if (type instanceof FloatType) {
            mv.visitVarInsn(DLOAD, address);
        } else if (type instanceof StringType) {
            mv.visitVarInsn(ALOAD, address);
        } else if (type instanceof CharType) {
            mv.visitVarInsn(ALOAD, address);
        } else if (type instanceof BoolType) {
            mv.visitVarInsn(ILOAD, address);
        } else if (type instanceof ArrayType) {
            mv.visitVarInsn(ALOAD, address);
        } else {
            System.out.println("something was not right");
        }
        return address;
    }

    private void emitNop() {
        mv.visitInsn(NOP);
        return;
    }
}
