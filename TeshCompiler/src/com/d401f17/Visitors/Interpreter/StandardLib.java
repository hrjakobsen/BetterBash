package com.d401f17.Visitors.Interpreter;

import com.d401f17.AST.Nodes.*;
import com.d401f17.SymbolTable.FunctionSymbol;
import com.d401f17.SymbolTable.SymTab;
import com.d401f17.SymbolTable.VariableAlreadyDeclaredException;
import com.d401f17.TypeSystem.*;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Created by mathias on 4/27/17.
 */
public final class StandardLib {
    public static void InsertFunctionNames (SymTab table) {
        try {
            table.insert("intToStr", new FunctionSymbol(new FunctionType("str", new Type[] {new IntType()}, new StringType()), null, null));
            table.insert("floatToStr", new FunctionSymbol(new FunctionType("str", new Type[] {new FloatType()}, new StringType()), null, null));
            table.insert("charToStr", new FunctionSymbol(new FunctionType("str", new Type[] {new CharType()}, new StringType()), null, null));
            table.insert("boolToStr", new FunctionSymbol(new FunctionType("str", new Type[] {new BoolType()}, new StringType()), null, null));
            table.insert("print", new FunctionSymbol(new FunctionType("print", new Type[] {new StringType()}, new VoidType()), null, null));
            table.insert("read", new FunctionSymbol(new FunctionType("read", new Type[] {}, new StringType()), null, null));
            table.insert("sqrt", new FunctionSymbol(new FunctionType("sqrt", new Type[] {new FloatType()}, new FloatType()), null, null));
            table.insert("rnd", new FunctionSymbol(new FunctionType("rnd", new Type[] {}, new FloatType()), null, null));
            table.insert("ceil", new FunctionSymbol(new FunctionType("ceil", new Type[] {new FloatType()}, new IntType()), null, null));
            table.insert("floor", new FunctionSymbol(new FunctionType("float", new Type[] {new FloatType()}, new IntType()), null, null));
        } catch (VariableAlreadyDeclaredException e) {}
    }
    
    public static void InsertFunctions (HashMap<String, Function<LiteralNode[], LiteralNode>> table) {
        table.put("str(FLOAT)", StandardLib::LiteralToString);
        table.put("str(CHAR)", StandardLib::LiteralToString);
        table.put("str(STRING)", StandardLib::LiteralToString);
        table.put("str(INT)", StandardLib::LiteralToString);
        table.put("str(BOOL)", StandardLib::LiteralToString);
        table.put("intval(FLOAT)", StandardLib::FloatToInt);
        table.put("print(STRING)", StandardLib::Print);
        table.put("read()", StandardLib::Read);
        table.put("sqrt(INT)", StandardLib::SquareRoot);
        table.put("sqrt(FLOAT)", StandardLib::SquareRoot);
        table.put("rnd()", StandardLib::Random);
        table.put("ceil(FLOAT)", StandardLib::Ceil);
        table.put("floor(FLOAT)", StandardLib::Floor);
    }

    public static StringLiteralNode LiteralToString(LiteralNode[] nodes) {
        LiteralNode node = nodes[0];
        if (node instanceof FloatLiteralNode || node instanceof IntLiteralNode || node instanceof CharLiteralNode) {
            return new StringLiteralNode(node.getValue().toString());
        }
        return null;
    }

    public static IntLiteralNode FloatToInt(LiteralNode[] nodes) {
        LiteralNode node = nodes[0];
        if (node instanceof FloatLiteralNode) {
            return new IntLiteralNode(((FloatLiteralNode)node).getValue().intValue());
        }
        return null;
    }

    public static StringLiteralNode Read(LiteralNode[] nodes) {
        Scanner scanner = new Scanner(System.in);
        StringLiteralNode s = new StringLiteralNode(scanner.next());
        scanner.close();
        return s;
    }

    public static LiteralNode Print(LiteralNode[] nodes) {
        System.out.println(nodes[0].getValue());
        return null;
    }

    public static FloatLiteralNode SquareRoot(LiteralNode[] nodes) {
        FloatLiteralNode result = null;
        if (nodes[0] instanceof IntLiteralNode) {
            long val = (long) nodes[0].getValue();
            result = new FloatLiteralNode(Math.sqrt(val));
        } else if (nodes[0] instanceof FloatLiteralNode) {
            double val = (double) nodes[0].getValue();
            result = new FloatLiteralNode(Math.sqrt(val));
        }
        return result;
    }

    public static FloatLiteralNode Random(LiteralNode[] nodes) {
        Random rnd = new Random();
        return new FloatLiteralNode(rnd.nextDouble());
    }

    public static IntLiteralNode Ceil(LiteralNode[] nodes) {
        return new IntLiteralNode((long)Math.ceil((double) nodes[0].getValue()));
    }

    public static IntLiteralNode Floor(LiteralNode[] nodes) {
        return new IntLiteralNode((long)Math.floor((double) nodes[0].getValue()));
    }
}
