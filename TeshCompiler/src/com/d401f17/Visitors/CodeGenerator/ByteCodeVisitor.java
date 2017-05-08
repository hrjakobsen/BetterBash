package com.d401f17.Visitors.CodeGenerator;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.BaseVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by mathias on 5/5/17.
 */
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
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getLeft().getType() instanceof IntType && node.getRight().getType() instanceof IntType) {
            mv.visitInsn(LCMP);
        } else {
            mv.visitInsn(DCMPL);
        }
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
    public Void visit(LessThanNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getLeft().getType() instanceof IntType && node.getRight().getType() instanceof IntType) {
            mv.visitInsn(LCMP);
        } else {
            mv.visitInsn(DCMPL);
        }
        mv.visitInsn(ICONST_0);
        mv.visitInsn(IMUL);
        return null;
    }

    @Override
    public Void visit(LessThanOrEqualNode node) {
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
        } else {
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
        } else {
            System.out.println("something was not right");
        }
        return address;
    }
}
